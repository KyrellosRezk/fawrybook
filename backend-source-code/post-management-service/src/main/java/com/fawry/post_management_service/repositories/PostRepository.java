package com.fawry.post_management_service.repositories;

import com.fawry.post_management_service.models.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, String> {

    @Query(value = """
    SELECT
        p.id AS id,
        p.content AS content,
        p.user_id AS userId,
        p.has_media AS hasMedia,
        COALESCE(c.commentsCount, 0) AS commentsCount,
        COALESCE(r.likeCount, 0) AS likeCount,
        COALESCE(r.disLikeCount, 0) AS disLikeCount
    FROM post_management.post p
    LEFT JOIN (
        SELECT 
            post_id, 
            COUNT(DISTINCT id) AS commentsCount
        FROM post_management.comment
        GROUP BY post_id
    ) c ON p.id = c.post_id
    LEFT JOIN (
        SELECT 
            post_id,
            SUM(CASE WHEN type = 'LIKE' THEN 1 ELSE 0 END) AS likeCount,
            SUM(CASE WHEN type = 'DISLIKE' THEN 1 ELSE 0 END) AS disLikeCount
        FROM post_management.react
        GROUP BY post_id
    ) r ON p.id = r.post_id
    WHERE p.user_id = :filterUserId
    ORDER BY p.created_at DESC
    """,
            countQuery = """
        SELECT COUNT(*)
        FROM post_management.post p
        WHERE p.user_id = :filterUserId
    """,
            nativeQuery = true)
    Page<Object[]> findPostsByUserId(@Param("filterUserId") String filterUserId, Pageable pageable);

    @Query(value = """
    SELECT
        p.id AS id,
        p.content AS content,
        p.user_id AS userId,
        p.has_media AS hasMedia,
        COALESCE(c.commentsCount, 0) AS commentsCount,
        COALESCE(r.likeCount, 0) AS likeCount,
        COALESCE(r.disLikeCount, 0) AS disLikeCount
    FROM post_management.post p
    LEFT JOIN (
        SELECT 
            post_id, 
            COUNT(DISTINCT id) AS commentsCount
        FROM post_management.comment
        GROUP BY post_id
    ) c ON p.id = c.post_id
    LEFT JOIN (
        SELECT 
            post_id,
            SUM(CASE WHEN type = 'LIKE' THEN 1 ELSE 0 END) AS likeCount,
            SUM(CASE WHEN type = 'DISLIKE' THEN 1 ELSE 0 END) AS disLikeCount
        FROM post_management.react
        GROUP BY post_id
    ) r ON p.id = r.post_id
    LEFT JOIN follow_management.following f ON f.user_id = :userId
    WHERE p.user_id = :userId OR p.user_id = ANY(f.followed_user_ids)
    ORDER BY p.created_at DESC
    """,
            countQuery = """
        SELECT COUNT(*)
        FROM post_management.post p
        JOIN follow_management.following f ON f.user_id = :userId
        WHERE p.user_id = :userId OR p.user_id = ANY(f.followed_user_ids)
    """,
            nativeQuery = true)
    Page<Object[]> findFeedForUser(@Param("userId") String userId, Pageable pageable);

    Optional<PostEntity> findByIdAndUserId(String id, String userId);
    void deleteByIdAndUserId(String id, String userId);
}
