package com.fawry.user_management_service.payloads.requests;

import com.fawry.user_management_service.enums.UserRoleEnum;

public record CreateUserRequest(
        String username,
        String firstName,
        String middleName,
        String lastName,
        String phoneNumber,
        String email,
        Double lng,
        Double lat,
        String governorateId,
        UserRoleEnum role,
        String positionId
) {
    public static CreateUserRequest mapToRequest(SignUpRequest signUpRequest) {
        return new CreateUserRequest(
                signUpRequest.username(),
                signUpRequest.firstName(),
                signUpRequest.middleName(),
                signUpRequest.lastName(),
                signUpRequest.phoneNumber(),
                signUpRequest.email(),
                signUpRequest.lng(),
                signUpRequest.lat(),
                signUpRequest.governorateId(),
                UserRoleEnum.USER,
                signUpRequest.positionId()
        );
    }
}