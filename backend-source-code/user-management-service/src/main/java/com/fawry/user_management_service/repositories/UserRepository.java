package com.fawry.user_management_service.repositories;

import com.fawry.user_management_service.models.UserEntity;
import com.fawry.user_management_service.payloads.responses.SuggestionFriend;
import com.fawry.user_management_service.payloads.responses.UserBasicDataResponse;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String id);

    Optional<UserEntity> findByUsernameOrEmailOrPhoneNumber(String identifier, String identifier1, String identifier2);

    @Query(value = """
SELECT 
    u.id,
    u.email,
    u.first_name AS firstName,
    u.middle_name AS middleName,
    u.last_name AS lastName,
    NULL AS something
FROM USER_MANAGEMENT.USER u
LEFT JOIN user_management.following f 
       ON f.FOLLOWER_ID = :userId AND f.FOLLOWED_ID = u.id
LEFT JOIN user_management.following_request s 
       ON s.SENDER_ID = :userId AND s.RECEIVER_ID = u.id
JOIN basic_data.position p 
       ON p.id = u.position_id
WHERE u.status = 'ACTIVE'
  AND u.id <> :userId
  AND (u.position_id = :positionId 
       OR u.governorate_id = :governorateId 
       OR p.industry_id = :industryId)
  AND f.FOLLOWED_ID IS NULL
  AND s.RECEIVER_ID IS NULL
""",
            countQuery = """
SELECT COUNT(*)
FROM USER_MANAGEMENT.USER u
LEFT JOIN user_management.FOLLOWING f 
       ON f.FOLLOWER_ID = :userId AND f.FOLLOWED_ID = u.id
LEFT JOIN user_management.FOLLOWING_REQUEST s 
       ON s.SENDER_ID = :userId AND s.RECEIVER_ID = u.id
JOIN basic_data.position p 
       ON p.id = u.position_id
WHERE u.status = 'ACTIVE'
  AND u.id <> :userId
  AND (u.position_id = :positionId 
       OR u.governorate_id = :governorateId 
       OR p.industry_id = :industryId)
  AND f.FOLLOWED_ID IS NULL
  AND s.RECEIVER_ID IS NULL
""",
            nativeQuery = true)
    Page<SuggestionFriend> findSuggestions(
            @Param("userId") String userId,
            @Param("positionId") String positionId,
            @Param("governorateId") String governorateId,
            @Param("industryId") String industryId,
            Pageable pageable
    );


    @Query(value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
        FROM user_management.FOLLOWING f
        WHERE f.FOLLOWER_ID = :followerId
          AND f.FOLLOWED_ID = :followedId
    """, nativeQuery = true)
    Boolean isFollowerById(
            @Param("followerId") String followerId,
            @Param("followedId") String followedId
    );

    @Query(
            value = """
        SELECT 
            u.id AS id,
            u.email AS email,
            u.first_name AS firstName,
            u.middle_name AS middleName,
            u.last_name AS lastName,
            NULL AS logoPath
        FROM user_management.user u
        JOIN user_management.following_request fr
          ON u.id = fr.sender_id
        WHERE fr.receiver_id = :id
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM user_management.user u
        JOIN user_management.following_request fr
          ON u.id = fr.sender_id
        WHERE fr.receiver_id = :id
        """,
            nativeQuery = true
    )
    Page<UserBasicDataResponse> findRequests(@Param("id") String id, Pageable pageable);


    @Query(nativeQuery = true, value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
        FROM user_management.FOLLOWING_REQUEST
        WHERE SENDER_ID = :senderId AND RECEIVER_ID = :receiverId
    """)
    Boolean existsFollowRequest(@Param("receiverId") String receiverId,
                                        @Param("senderId") String senderId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM user_management.FOLLOWING_REQUEST
        WHERE SENDER_ID = :senderId AND RECEIVER_ID = :receiverId
    """)
    int deleteFollowRequest(@Param("senderId") String senderId,
                             @Param("receiverId") String receiverId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = """
    INSERT INTO user_management.FOLLOWING_REQUEST(SENDER_ID, RECEIVER_ID)
    VALUES (:senderId, :receiverId)
    ON CONFLICT DO NOTHING
""")
    int insertFollowRequest(@Param("senderId") String senderId,
                            @Param("receiverId") String receiverId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = """
        INSERT INTO user_management.FOLLOWING(FOLLOWER_ID, FOLLOWED_ID)
        VALUES (:followerId, :followedId)
        ON CONFLICT DO NOTHING
    """)
    int insertFollowing(@Param("followerId") String followerId,
                         @Param("followedId") String followedId);
}

