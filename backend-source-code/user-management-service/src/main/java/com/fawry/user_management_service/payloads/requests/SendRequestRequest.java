package com.fawry.user_management_service.payloads.requests;

import com.fawry.user_management_service.enums.RequestActionEnum;

public record SendRequestRequest(
   String receiverId,
   RequestActionEnum action
) {}
