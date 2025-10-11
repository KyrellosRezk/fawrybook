package com.fawry.user_management_service.payloads.dtos;

import com.fawry.user_management_service.enums.UserRoleEnum;
import com.fawry.user_management_service.enums.UserStatusEnum;
import com.fawry.user_management_service.models.GovernorateEntity;
import com.fawry.user_management_service.models.PositionEntity;
import com.fawry.user_management_service.models.UserEntity;

public record RedisUserDto(
    String id,
    String username,
    String firstName,
    String middleName,
    String lastName,
    UserStatusEnum status,
    UserRoleEnum role,
    GovernorateEntity governorate,
    PositionEntity position,
    String phoneNumber,
    String email,
    Double lng,
    Double lat
) {
    public static RedisUserDto mapFromEntity(UserEntity userEntity) {
        return new RedisUserDto(
            userEntity.getId(),
            userEntity.getUsername(),
            userEntity.getFirstName(),
            userEntity.getMiddleName(),
            userEntity.getLastName(),
            userEntity.getStatus(),
            userEntity.getRole(),
            userEntity.getGovernorate(),
            userEntity.getPosition(),
            userEntity.getPhoneNumber(),
            userEntity.getEmail(),
            userEntity.getLng(),
            userEntity.getLat()
        );
    }
}
