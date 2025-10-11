package com.fawry.file_management_service.payloads.responses;

import java.util.List;

public record UploadFilesResponse(
        Integer count,
        List<String> paths
) {}
