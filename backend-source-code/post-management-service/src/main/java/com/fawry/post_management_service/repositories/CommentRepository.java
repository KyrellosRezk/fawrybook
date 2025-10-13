package com.fawry.post_management_service.repositories;

import com.fawry.post_management_service.models.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, String> {
    void deleteByIdAndUserId(String id, String userId);
    Optional<CommentEntity> findByIdAndUserId(String id, String userId);
    List<CommentEntity> findByPostId(String postId);
}
