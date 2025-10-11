package com.fawry.user_management_service.payloads.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SignInResponse(
        String accessToken,
        String refreshToken,
        String OTPToken,
        UserBasicDataResponse user
) {}
