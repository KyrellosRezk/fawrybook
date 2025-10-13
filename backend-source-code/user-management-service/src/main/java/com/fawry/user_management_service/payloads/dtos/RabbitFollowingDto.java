package com.fawry.user_management_service.payloads.dtos;

import java.io.Serializable;

public record RabbitFollowingDto(
        String followerId,
        String followedId
) implements Serializable {
}
