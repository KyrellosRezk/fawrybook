package com.fawry.post_management_service.services;

import com.fawry.post_management_service.enums.EntityNameEnum;
import com.fawry.post_management_service.exceptions.BadRequestException;
import com.fawry.post_management_service.models.CommentEntity;
import com.fawry.post_management_service.payloads.requests.CreateCommentRequest;
import com.fawry.post_management_service.payloads.requests.UpdateMediaEntityRequest;
import com.fawry.post_management_service.payloads.responses.CreateMediaEntityResponse;
import com.fawry.post_management_service.repositories.CommentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j(topic = "CommentService")
public class CommentService {

    private final CommentRepository commentRepository;

    public CreateMediaEntityResponse create(CreateCommentRequest createCommentRequest, String userId) {
        return CreateMediaEntityResponse.map(
                this.commentRepository.save(new CommentEntity(createCommentRequest, userId)).getId(),
                createCommentRequest.hasMedia() ? EntityNameEnum.COMMENT : null
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
}
