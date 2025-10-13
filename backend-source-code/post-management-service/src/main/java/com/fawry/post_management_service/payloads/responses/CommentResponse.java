package com.fawry.post_management_service.payloads.responses;

public record CommentResponse(
    String id,
    String content,
    String firstName,
    String secondName,
    String lastName,
    String userId
) {}
