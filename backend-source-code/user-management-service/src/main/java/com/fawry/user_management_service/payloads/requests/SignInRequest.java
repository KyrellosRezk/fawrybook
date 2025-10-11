package com.fawry.user_management_service.payloads.requests;

public record SignInRequest(
        String identifier,
        String password
) {}
