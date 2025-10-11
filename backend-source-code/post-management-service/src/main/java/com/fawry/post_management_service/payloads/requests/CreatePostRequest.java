package com.fawry.post_management_service.payloads.requests;

import jakarta.validation.constraints.NotNull;

public record CreatePostRequest(
        String content,

        @NotNull(message = "hasMedia flag can not be null")
        Boolean hasMedia
) {}
