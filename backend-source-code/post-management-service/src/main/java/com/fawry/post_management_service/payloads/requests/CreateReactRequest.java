package com.fawry.post_management_service.payloads.requests;

import com.fawry.post_management_service.enums.ReactTypeEnum;

public record CreateReactRequest(
        String postId,
        ReactTypeEnum type
) {}
