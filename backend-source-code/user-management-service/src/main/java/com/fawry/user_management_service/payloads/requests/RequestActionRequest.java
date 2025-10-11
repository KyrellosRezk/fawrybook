package com.fawry.user_management_service.payloads.requests;

import com.fawry.user_management_service.enums.RequestReplyActionEnum;

public record RequestActionRequest(
        String senderId,
        RequestReplyActionEnum action
) {}
