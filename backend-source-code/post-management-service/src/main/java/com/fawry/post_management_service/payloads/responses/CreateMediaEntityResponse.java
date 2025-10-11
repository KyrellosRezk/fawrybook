package com.fawry.post_management_service.payloads.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fawry.post_management_service.enums.EntityNameEnum;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateMediaEntityResponse(
        EntityNameEnum entityName,
        String id
) {
    public static CreateMediaEntityResponse map(String id, EntityNameEnum entityName) {
        return new CreateMediaEntityResponse(
                entityName,
                id
        );
    }
}
