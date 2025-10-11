package com.fawry.user_management_service.repositories;

import com.fawry.user_management_service.models.GovernorateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GovernorateRepository extends JpaRepository<GovernorateEntity, String> {
}
