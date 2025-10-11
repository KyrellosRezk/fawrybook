package com.fawry.post_management_service.payloads.dtos;

public record FollowingMessageDto(
        String followerId,
        String followedId
) {}
