package com.fawry.post_management_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fawry.post_management_service.enums.EntityNameEnum;
import com.fawry.post_management_service.exceptions.BadRequestException;
import com.fawry.post_management_service.models.PostEntity;
import com.fawry.post_management_service.payloads.dtos.RedisUserDto;
import com.fawry.post_management_service.payloads.requests.CreatePostRequest;
import com.fawry.post_management_service.payloads.requests.PaginationRequest;
import com.fawry.post_management_service.payloads.requests.UpdateMediaEntityRequest;
import com.fawry.post_management_service.payloads.responses.CreateMediaEntityResponse;
import com.fawry.post_management_service.payloads.responses.PostPagination;
import com.fawry.post_management_service.repositories.PostRepository;
import com.fawry.post_management_service.utils.RedisUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j(topic = "PostService")
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ObjectMapper objectMapper;

    public CreateMediaEntityResponse create(CreatePostRequest createPostRequest, String userId) {
        return CreateMediaEntityResponse.map(
            this.postRepository.save(new PostEntity(createPostRequest, userId)).getId(),
            createPostRequest.hasMedia() ? EntityNameEnum.POST : null
        );
    }

    public CreateMediaEntityResponse update(
            @NotNull UpdateMediaEntityRequest updateMediaEntityRequest,
            String userId
    ) {
        Optional<PostEntity> postEntity = this.postRepository.findByIdAndUserId(
                updateMediaEntityRequest.id(),
                userId
        );
        if(postEntity.isEmpty()) {
            throw new BadRequestException("Post with id: " + "not found");
        }
        postEntity.get().setContent(updateMediaEntityRequest.content());
        postEntity.get().setUpdatedAt(Instant.now());
        return CreateMediaEntityResponse.map(
                this.postRepository.save(postEntity.get()).getId(),
                updateMediaEntityRequest.hasMedia() ? EntityNameEnum.POST : null
        );
    }

    public void delete(String id, String userId) {
        this.postRepository.deleteByIdAndUserId(id, userId);
    }

    public Page<PostPagination> getFeed(
            String userId,
            @NotNull PaginationRequest paginationRequest
    ) {
        Pageable pageable = PageRequest.of(
                paginationRequest.page() == null ? 0 : paginationRequest.page(),
                paginationRequest.size() == null ? 5 : paginationRequest.size()
        );

        Page<Object[]> result = postRepository.findFeedWithCounts(
                userId,
                paginationRequest.filterUserId(),
                pageable
        );

        return result.map(row -> {
            try {
                return new PostPagination(
                        (String) row[0],
                        (String) row[1],
                        objectMapper.readValue(
                                RedisUtils.getJsonValue("user", (String) row[2]),
                                RedisUserDto.class
                        ),
                        ((Number) row[4]).intValue(),
                        ((Number) row[5]).intValue(),
                        ((Number) row[6]).intValue(),
                        (Boolean) row[3]
                );
            } catch (JsonProcessingException e) {
                throw new BadRequestException(e.getMessage());
            }
        });
    }
}
