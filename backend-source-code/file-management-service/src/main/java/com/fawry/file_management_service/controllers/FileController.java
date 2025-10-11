package com.fawry.file_management_service.controllers;

import com.fawry.file_management_service.enums.EntityNameEnum;
import com.fawry.file_management_service.payloads.responses.UploadFilesResponse;
import com.fawry.file_management_service.services.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Slf4j(topic = "FileController")
@RestController
@RequestMapping("v1/file")
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = {"multipart/form-data"})
    public UploadFilesResponse uploadFiles(
            @RequestParam("files") Map<String, MultipartFile> files,
            @RequestParam("entity name") EntityNameEnum entityName,
            @RequestParam("entity id") String entityId,
            HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute("id");
        return this.fileService.save(files, entityName, entityId, userId);
    }

    @DeleteMapping
    public void deleteFile(
            @RequestParam("paths")List<String> paths,
            HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute("id");
        this.fileService.delete(paths, userId);
    }

    @GetMapping
    public Resource getFile(
            @RequestParam("path") String relativePath
    ) { return this.fileService.getFile(relativePath); }
}
