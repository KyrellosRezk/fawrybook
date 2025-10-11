package com.fawry.post_management_service.payloads.requests;

public record UpdateMediaEntityRequest(
    String id,
    Boolean hasMedia,
    String content
) {}
