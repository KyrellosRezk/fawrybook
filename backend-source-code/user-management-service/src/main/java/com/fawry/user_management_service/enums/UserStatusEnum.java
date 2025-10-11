package com.fawry.user_management_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStatusEnum {
    UNVERIFIED("Unverified"),
    ACTIVE("Active"),
    DELETED("Deleted");

    private final String status;
}
