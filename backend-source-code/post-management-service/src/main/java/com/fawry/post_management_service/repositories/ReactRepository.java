package com.fawry.post_management_service.repositories;

import com.fawry.post_management_service.models.ReactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactRepository extends JpaRepository<ReactEntity, String> {
    Optional<ReactEntity> findByUserIdAndPost_Id(String userId, String s);
    void deleteByUserIdAndId(String userId, String id);
}