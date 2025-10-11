package com.fawry.user_management_service.payloads.dtos;

public record RabbitFollowingDto(
        String followerId,
        String followedId
) {
}
