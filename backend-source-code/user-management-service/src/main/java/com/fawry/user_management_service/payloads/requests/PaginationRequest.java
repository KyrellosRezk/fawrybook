package com.fawry.user_management_service.payloads.requests;

public record PaginationRequest(
        Integer size,
        Integer page
) {
}
