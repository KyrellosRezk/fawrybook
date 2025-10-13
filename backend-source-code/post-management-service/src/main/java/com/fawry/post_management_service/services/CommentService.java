package com.fawry.post_management_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fawry.post_management_service.enums.EntityNameEnum;
import com.fawry.post_management_service.exceptions.BadRequestException;
import com.fawry.post_management_service.models.CommentEntity;
import com.fawry.post_management_service.payloads.dtos.RedisUserDto;
import com.fawry.post_management_service.payloads.requests.CreateCommentRequest;
import com.fawry.post_management_service.payloads.requests.UpdateMediaEntityRequest;
import com.fawry.post_management_service.payloads.responses.CommentResponse;
import com.fawry.post_management_service.payloads.responses.CreateMediaEntityResponse;
import com.fawry.post_management_service.repositories.CommentRepository;
import com.fawry.post_management_service.utils.RedisUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j(topic = "CommentService")
public class CommentService {

    private final CommentRepository commentRepository;
    private final ObjectMapper objectMapper;

    public CreateMediaEntityResponse create(CreateCommentRequest createCommentRequest, String userId) {
        return CreateMediaEntityResponse.map(
                this.commentRepository.save(new CommentEntity(createCommentRequest, userId)).getId(),
                //createCommentRequest.hasMedia() ? EntityNameEnum.COMMENT : null
                null
        );
    }

    public void delete(String id, String userId) {
        this.commentRepository.deleteByIdAndUserId(id, userId);
    }

    public CreateMediaEntityResponse update(UpdateMediaEntityRequest updateMediaEntityRequest, String userId) {
        Optional<CommentEntity> commentEntity = this.commentRepository.findByIdAndUserId(
                updateMediaEntityRequest.id(),
                userId
        );
        if(commentEntity.isEmpty()) {
            throw new BadRequestException("Comment with id: " + "not found");
        }
        commentEntity.get().setContent(updateMediaEntityRequest.content());
        commentEntity.get().setUpdatedAt(Instant.now());
        return CreateMediaEntityResponse.map(
                this.commentRepository.save(commentEntity.get()).getId(),
                updateMediaEntityRequest.hasMedia() ? EntityNameEnum.COMMENT : null
        );
    }

    public List<CommentResponse> getComments(String postId) {
        List<CommentResponse> comments = new ArrayList<>();
        List<CommentEntity> commentEntities = this.commentRepository.findByPostId(postId);
        commentEntities.forEach(commentEntity -> {
            RedisUserDto user = null;
            try {
                user = objectMapper.readValue(
                    RedisUtils.getJsonValue("user", commentEntity.getUserId()),
                    RedisUserDto.class
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            comments.add(new CommentResponse(
                        commentEntity.getId(),
                        commentEntity.getContent(),
                        user.firstName(),
                        user.middleName(),
                        user.lastName(),
                        user.id()
                    )
            );
        });
        return comments;
    }
}
