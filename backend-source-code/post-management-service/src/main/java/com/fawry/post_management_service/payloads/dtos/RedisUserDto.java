package com.fawry.post_management_service.payloads.dtos;

public record RedisUserDto(
    String id,
    String username,
    String firstName,
    String middleName,
    String lastName
) {}
