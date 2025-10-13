package com.fawry.user_management_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fawry.user_management_service.enums.RequestReplyActionEnum;
import com.fawry.user_management_service.enums.UserStatusEnum;
import com.fawry.user_management_service.exceptions.BadRequestException;
import com.fawry.user_management_service.models.UserEntity;
import com.fawry.user_management_service.payloads.dtos.RabbitFollowingDto;
import com.fawry.user_management_service.payloads.dtos.RedisUserDto;
import com.fawry.user_management_service.payloads.requests.CreateUserRequest;
import com.fawry.user_management_service.payloads.requests.PaginationRequest;
import com.fawry.user_management_service.payloads.requests.RequestActionRequest;
import com.fawry.user_management_service.payloads.requests.SendRequestRequest;
import com.fawry.user_management_service.payloads.responses.SuggestionFriend;
import com.fawry.user_management_service.payloads.responses.UserBasicDataResponse;
import com.fawry.user_management_service.payloads.responses.UserProfile;
import com.fawry.user_management_service.repositories.UserRepository;
import com.fawry.user_management_service.utils.RabbitPublisher;
import com.fawry.user_management_service.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j(topic = "UserService")
public class UserService {

    @Value("${rabbitmq.exchange.follow}")
    private String followExchange;

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final RabbitPublisher rabbitPublisher;

    public UserService(UserRepository userRepository, ObjectMapper objectMapper, RabbitPublisher rabbitPublisher) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.rabbitPublisher = rabbitPublisher;
    }

    public UserEntity create(@NotNull CreateUserRequest createUserRequest) throws Exception {
        UserEntity userEntity = this.userRepository.save(new UserEntity(createUserRequest));
        RedisUserDto redisUserDto = RedisUserDto.mapFromEntity(userEntity);
        RedisUtils.setJsonValue(
                "user",
                redisUserDto.id(),
                objectMapper.writeValueAsString(redisUserDto)
        );
        return userEntity;
    }

    public void verify(String email) throws Exception {
        UserEntity userEntity = this.getByEmail(email);
        if(!userEntity.getStatus().equals(UserStatusEnum.UNVERIFIED)) {
            throw new BadRequestException("User with email: " + email + " already verified");
        }
        userEntity.setStatus(UserStatusEnum.ACTIVE);
        userEntity.setUpdatedAt(Instant.now());
        RedisUserDto redisUserDto = RedisUserDto.mapFromEntity(this.userRepository.save(userEntity));
        RedisUtils.setJsonValue(
                "user",
                redisUserDto.id(),
                objectMapper.writeValueAsString(redisUserDto)
        );
    }

    public void delete(String userId) throws JsonProcessingException {
        Optional<UserEntity> userEntity = this.userRepository.findById(userId);
        if(userEntity.isEmpty()) {
            throw new BadRequestException("User with id: " + userId + " not found");
        }
        if(userEntity.get().getStatus().equals(UserStatusEnum.DELETED)) {
            throw new BadRequestException("User with id: " + userId + " already deleted");
        }
        userEntity.get().setStatus(UserStatusEnum.DELETED);
        userEntity.get().setDeletedAt(Instant.now());
        RedisUserDto redisUserDto = RedisUserDto.mapFromEntity(this.userRepository.save(userEntity.get()));
        RedisUtils.setJsonValue(
            "user",
            redisUserDto.id(),
            objectMapper.writeValueAsString(redisUserDto)
        );
    }

    public UserEntity getById(String userId) {
        Optional<UserEntity> userEntity = this.userRepository.findById(userId);
        if(userEntity.isEmpty()) {
            throw new BadRequestException("User with id: " + userId + " not found");
        }
        return userEntity.get();
    }

    public UserEntity getByEmail(String email) {
        Optional<UserEntity> userEntity = this.userRepository.findByEmail(email);
        if(userEntity.isEmpty()) {
            throw new BadRequestException("User with id: " + email + " not found");
        }
        return userEntity.get();
    }

    public @NotNull UserEntity getByIdentifier(String identifier) {
        Optional<UserEntity> userEntity
                = userRepository.findByUsernameOrEmailOrPhoneNumber(identifier, identifier, identifier);
        if(userEntity.isEmpty()) {
            throw new BadRequestException("User with identifier: " + identifier + " not found");
        }
        return userEntity.get();
    }

    public Page<SuggestionFriend> getSuggestions(@NotNull PaginationRequest paginationRequest, String userId)
            throws Exception {
         Pageable pageable = PageRequest.of(
                paginationRequest.page() == null ? 0 : paginationRequest.page(),
                paginationRequest.size() == null ? 5 : paginationRequest.size()
        );
        RedisUserDto user = objectMapper.readValue(
            RedisUtils.getJsonValue("user", userId),
            RedisUserDto.class
        );
        return this.userRepository.findSuggestions(
            user.id(),
            user.position().getId(),
            user.governorate().getId(),
            user.position().getIndustry().getId(),
            pageable
        );
    }

    public UserProfile getUserProfile(String profileId, String userId) throws Exception {
        if(profileId == null || profileId.isEmpty()) {
            RedisUserDto user = objectMapper.readValue(
                    RedisUtils.getJsonValue("user", userId),
                    RedisUserDto.class
            );
            return UserProfile.mapToMainProfile(user);
        }
        String stringProfileUser = RedisUtils.getJsonValue("user", profileId);
        if(stringProfileUser == null ) {
            throw new BadRequestException("User with id: " + profileId + " not found");
        }
        RedisUserDto profileUser = objectMapper.readValue(
                stringProfileUser,
                RedisUserDto.class
        );
        if(!profileUser.status().equals(UserStatusEnum.ACTIVE)) {
            throw new BadRequestException("User with id: " + profileUser.username() + " is not active");
        }
        Boolean isFollower = null;
        if(!profileId.equals(userId)) {
            isFollower = this.userRepository.isFollowerById(userId, profileId);
        }
        return UserProfile.mapToProfile(profileUser, isFollower);
    }

    public Page<UserBasicDataResponse> getRequests(@NotNull PaginationRequest paginationRequest, String id) {
        Pageable pageable = PageRequest.of(
                paginationRequest.page() == null ? 0 : paginationRequest.page(),
                paginationRequest.size() == null ? 5 : paginationRequest.size()
        );
        return this.userRepository.findRequests(id, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public void takeActionOnRequest(@NotNull RequestActionRequest requestActionRequest, String id) {
        if(!this.userRepository.existsFollowRequest(id, requestActionRequest.senderId())) {
            throw new BadRequestException("There is no request from user id: " + requestActionRequest.senderId());
        }
        if(requestActionRequest.action().equals(RequestReplyActionEnum.APPROVED)) {
            this.userRepository.insertFollowing(requestActionRequest.senderId(), id);
            this.rabbitPublisher.publish(
                    followExchange,
                    "follow",
                    new RabbitFollowingDto(requestActionRequest.senderId(), id)
            );
        }
        this.userRepository.deleteFollowRequest(requestActionRequest.senderId(), id);
    }

    public void sendRequest(@NotNull SendRequestRequest sendRequestRequest, String id) {
        if (this.userRepository.isFollowerById(id, sendRequestRequest.receiverId())) {
            throw new BadRequestException("You already follow user with id: " + sendRequestRequest.receiverId());
        }

        switch (sendRequestRequest.action()) {
            case CREATE -> createRequest(id, sendRequestRequest.receiverId());
            case REVOKE -> revokeRequest(id, sendRequestRequest.receiverId());
            default -> throw new BadRequestException("Invalid request action: " + sendRequestRequest.action());
        }
    }

    @Transactional
    protected void createRequest(String senderId, String receiverId) {
        Boolean isRequestExist = this.userRepository.existsFollowRequest(receiverId, senderId);
        if (isRequestExist) {
            throw new BadRequestException("You already sent follow request for user with id: " + receiverId);
        }
        this.userRepository.insertFollowRequest(senderId, receiverId);
    }

    @Transactional
    protected void revokeRequest(String senderId, String receiverId) {
        Boolean isRequestExist = this.userRepository.existsFollowRequest(receiverId, senderId);
        if (!isRequestExist) {
            throw new BadRequestException("You did not send follow request for user with id: " + receiverId);
        }
        this.userRepository.deleteFollowRequest(senderId, receiverId);
    }
}
