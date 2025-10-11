package com.fawry.user_management_service.payloads.responses;

public record SuggestionFriend(
        String id,
        String email,
        String firstName,
        String middleName,
        String lastName,
        String logo
) {}
