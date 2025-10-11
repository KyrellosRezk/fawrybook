package com.fawry.post_management_service.repositories;

import com.fawry.post_management_service.models.FollowingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowingRepository extends JpaRepository<FollowingEntity, String> {
}
