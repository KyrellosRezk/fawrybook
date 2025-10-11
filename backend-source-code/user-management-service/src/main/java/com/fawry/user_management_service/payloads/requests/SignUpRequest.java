package com.fawry.user_management_service.payloads.requests;

import jakarta.validation.constraints.*;

public record SignUpRequest(
        @NotBlank(message = "Username is required")
        @Size(max = 255, message = "Username must not exceed 255 characters")
        String username,

        @NotBlank(message = "First name is required")
        @Size(max = 255, message = "First name must not exceed 255 characters")
        String firstName,

        @NotBlank(message = "Middle name is required")
        @Size(max = 255, message = "Middle name must not exceed 255 characters")
        String middleName,

        @NotBlank(message = "Last name is required")
        @Size(max = 255, message = "Last name must not exceed 255 characters")
        String lastName,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9]{8,15}$", message = "Phone number must contain only digits (8–15 digits)")
        String phoneNumber,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid format")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
        Double lng,

        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
        Double lat,

        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^+=_~])[A-Za-z\\d@$!%*?&#^+=_~]{8,}$",
                message = "Password must be at least 8 characters long, contain upper and lower case letters, a number, and a special character"
        )
        String password,

        @NotBlank(message = "Governorate ID is required")
        String governorateId,

        @NotBlank(message = "Position ID is required")
        String positionId
) {}