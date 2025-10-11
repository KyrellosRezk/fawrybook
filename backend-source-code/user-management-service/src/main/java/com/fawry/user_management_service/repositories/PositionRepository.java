package com.fawry.user_management_service.repositories;

import com.fawry.user_management_service.models.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, String> {
}
