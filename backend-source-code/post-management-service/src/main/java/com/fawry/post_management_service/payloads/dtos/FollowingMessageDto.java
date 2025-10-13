package com.fawry.post_management_service.payloads.dtos;

import java.io.Serializable;

public record FollowingMessageDto(
        String followerId,
        String followedId
) implements Serializable {}
