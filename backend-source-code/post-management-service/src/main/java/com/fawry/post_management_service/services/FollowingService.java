package com.fawry.post_management_service.services;

import com.fawry.post_management_service.exceptions.BadRequestException;
import com.fawry.post_management_service.models.FollowingEntity;
import com.fawry.post_management_service.payloads.dtos.FollowingMessageDto;
import com.fawry.post_management_service.repositories.FollowingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j(topic = "FollowingService")
@AllArgsConstructor
public class FollowingService {

    private final FollowingRepository followingRepository;

    public void follow(FollowingMessageDto followingMessageDto) {
        Optional<FollowingEntity> followingEntity = this.followingRepository.findById(
                followingMessageDto.followerId()
        );
        if(followingEntity.isEmpty()) {
            this.followingRepository.save(new FollowingEntity(
                  followingMessageDto.followerId(),
                  Set.of(followingMessageDto.followedId()))
            );
        } else {
            Set<String> followedUserIds = followingEntity.get().getFollowedUserIds();
            followedUserIds.add(followingMessageDto.followedId());
            followingEntity.get().setFollowedUserIds(followedUserIds);
            this.followingRepository.save(followingEntity.get());
        }
    }

    public void unfollow(FollowingMessageDto followingMessageDto) {
        Optional<FollowingEntity> followingEntity = this.followingRepository.findById(
                followingMessageDto.followerId()
        );
        if(followingEntity.isEmpty()) {
            throw new BadRequestException(
                    "User with id: " + followingMessageDto.followedId() + " does not follow anyone"
            );
        }
        Set<String> followedUserIds = followingEntity.get().getFollowedUserIds();
        followedUserIds.remove(followingMessageDto.followedId());
        followingEntity.get().setFollowedUserIds(followedUserIds);
        this.followingRepository.save(followingEntity.get());
    }
}
