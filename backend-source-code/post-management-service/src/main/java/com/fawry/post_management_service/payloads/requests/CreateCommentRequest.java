package com.fawry.post_management_service.payloads.requests;

public record CreateCommentRequest(
        String postId,
        Boolean hasMedia,
        String content,
        String parentCommentId
) {}
