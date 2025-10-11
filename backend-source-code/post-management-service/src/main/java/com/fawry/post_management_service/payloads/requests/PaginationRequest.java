package com.fawry.post_management_service.payloads.requests;

public record PaginationRequest(
        Integer size,
        Integer page,
        String filterUserId
) {}
