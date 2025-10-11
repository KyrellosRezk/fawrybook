package com.fawry.post_management_service.payloads.responses;

import com.fawry.post_management_service.payloads.dtos.RedisUserDto;

public record PostPagination(
    String id,
    String content,
    RedisUserDto user,
    Integer commentsCount,
    Integer likeCount,
    Integer disLikeCount,
    Boolean hasMedia
) {}
