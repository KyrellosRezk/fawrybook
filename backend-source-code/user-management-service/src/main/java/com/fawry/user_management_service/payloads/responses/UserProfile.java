package com.fawry.user_management_service.payloads.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fawry.user_management_service.enums.UserRoleEnum;
import com.fawry.user_management_service.models.GovernorateEntity;
import com.fawry.user_management_service.models.PositionEntity;
import com.fawry.user_management_service.payloads.dtos.RedisUserDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserProfile(
    String username,
    String firstName,
    String middleName,
    String lastName,
    String phoneNumber,
    String email,
    Double lng,
    Double lat,
    GovernorateEntity governorate,
    UserRoleEnum role,
    PositionEntity position,
    Boolean isFollower
) {
    public static UserProfile mapToMainProfile(RedisUserDto user) {
        return new UserProfile(
            user.username(),
            user.firstName(),
            user.middleName(),
            user.lastName(),
            user.phoneNumber(),
            user.email(),
            user.lng(),
            user.lat(),
            user.governorate(),
            user.role(),
            user.position(),
            null
        );
    }

    public static UserProfile mapToProfile(RedisUserDto user, Boolean isFollower) {
        return new UserProfile(
            user.username(),
            user.firstName(),
            user.middleName(),
            user.lastName(),
null,
            user.email(),
            null,
            null,
            user.governorate(),
            null,
            user.position(),
            isFollower
        );
    }
}
