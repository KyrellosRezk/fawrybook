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
        COUNT(DISTINCT c.id) AS commentsCount,
        COALESCE(SUM(CASE WHEN r.type = 'LIKE' THEN 1 ELSE 0 END), 0) AS likeCount,
        COALESCE(SUM(CASE WHEN r.type = 'DISLIKE' THEN 1 ELSE 0 END), 0) AS disLikeCount
    FROM post_management.post p
    LEFT JOIN post_management.comment c ON p.id = c.post_id
    LEFT JOIN post_management.react r ON p.id = r.post_id
    JOIN follow_management.following f ON f.user_id = :userId
    WHERE (
        :filterUserId IS NULL
        OR p.user_id = :filterUserId
        OR p.user_id = :userId
        OR p.user_id = ANY(f.followed_user_ids)
    )
    GROUP BY p.id, p.content, p.user_id, p.has_media, p.created_at
    ORDER BY p.created_at DESC
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM post_management.post p
    JOIN follow_management.following f ON f.user_id = :userId
    WHERE (
        :filterUserId IS NULL
        OR p.user_id = :filterUserId
        OR p.user_id = :userId
        OR p.user_id = ANY(f.followed_user_ids)
    )
    """,
            nativeQuery = true)
    Page<Object[]> findFeedWithCounts(
            @Param("userId") String userId,
            @Param("filterUserId") String filterUserId,
            Pageable pageable
    );
    Optional<PostEntity> findByIdAndUserId(String id, String userId);
    void deleteByIdAndUserId(String id, String userId);
}
