package com.fawry.user_management_service.repositories;

import com.fawry.user_management_service.models.UserCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredentialEntity, String> {
    Optional<UserCredentialEntity> findByUser_Id(String userId);
}
