package com.fawry.file_management_service.services;

import com.fawry.file_management_service.enums.EntityNameEnum;
import com.fawry.file_management_service.exceptions.BadRequestException;
import com.fawry.file_management_service.payloads.responses.UploadFilesResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j(topic = "FileService")
public class FileService {

    @Value("${storage.path}")
    private String storagePath;

    private final List<String> ALLOWED_EXTENSION = List.of("jpg", "png", "webp", "jpeg", "mp4");

    public UploadFilesResponse save(
            @NotNull Map<String,
            MultipartFile> files,
            EntityNameEnum entityName,
            String entityId,
            String userId
    ) {
        if (files.isEmpty()) {
            throw new BadRequestException("No file uploaded");
        }
        log.info("Received {} files for upload", files.size());
        List<String> uploadedPaths = new ArrayList<>();
        for (Map.Entry<String, MultipartFile> fileEntry : files.entrySet()) {
            String key = fileEntry.getKey();
            MultipartFile file = fileEntry.getValue();
            try {
                String path = this.saveFile(file, entityName, entityId, key, userId);
                uploadedPaths.add(path);
                log.info("Uploaded file [{}] as {}", key, path);
            } catch (Exception e) {
                log.error("Failed to upload file [{}]: {}", key, e.getMessage());
            }
        }
        return new UploadFilesResponse(uploadedPaths.size(), uploadedPaths);
    }

    private @NotNull String saveFile(
            @NotNull MultipartFile file,
            EntityNameEnum entityName,
            String entityId,
            String fileName,
            String userId
    ) throws Exception {
        if (file.isEmpty()) {
            throw new BadRequestException("Empty file cannot be saved");
        }

        Path entityDir = Paths.get(storagePath, userId ,entityName.toString(), entityId);
        Files.createDirectories(entityDir);

        String originalName = file.getOriginalFilename();
        if(originalName == null || !originalName.contains(".")) {
            throw new BadRequestException("The file has no extension");
        }
        String fileExtension = originalName.substring(originalName.lastIndexOf('.'));
        if(!ALLOWED_EXTENSION.contains(fileExtension)) {
            throw new BadRequestException("The file extension: " + fileExtension + " is not allowed");
        }
        Path targetPath = entityDir.resolve(fileName + fileExtension);
        file.transferTo(targetPath.toFile());
        
        Path relativePath = Paths.get(storagePath).relativize(targetPath);
        return relativePath.toString().replace("\\", "/");
    }

    public void deleteFile(String relativePath, String userId) {
        try {
            String normalizedPath = relativePath.replace("\\", "/")
                    .replaceAll("^/+", "")
                    .replaceAll("/+$", "");

            String[] parts = normalizedPath.split("/", 2);
            if (parts.length == 0 || !parts[0].equals(userId)) {
                throw new BadRequestException("Unauthorized file path: " + relativePath);
            }

            Path fullPath = Paths.get(storagePath, relativePath);
            if (!fullPath.normalize().startsWith(Paths.get(storagePath))) {
                throw new BadRequestException("Invalid file path: " + relativePath);
            }
            if (Files.exists(fullPath)) {
                Files.delete(fullPath);
                log.info("Deleted file successfully: {}", fullPath);
            } else {
                log.warn("File not found for deletion: {}", fullPath);
            }
        } catch (Exception e) {
            log.error("Failed to delete file [{}]: {}", relativePath, e.getMessage());
            throw new BadRequestException("Failed to delete file: " + relativePath);
        }
    }

    public void delete(List<String> relativePaths, String userId) {
        if (relativePaths == null || relativePaths.isEmpty()) {
            throw new BadRequestException("No file paths provided for deletion");
        }
        log.info("Deleting {} files for user {}", relativePaths.size(), userId);
        for (String path : relativePaths) {
            deleteFile(path, userId);
        }
    }

    public Resource getFile(String relativePath) {
        try {
            Path fullPath = Paths.get(storagePath, relativePath).normalize();
            if (!fullPath.startsWith(Paths.get(storagePath))) {
                throw new BadRequestException("Invalid file path: " + relativePath);
            }
            if (!Files.exists(fullPath) || !Files.isRegularFile(fullPath)) {
                throw new BadRequestException("File not found: " + relativePath);
            }
            return new FileSystemResource(fullPath);
        } catch (Exception e) {
            log.error("Failed to load file [{}]: {}", relativePath, e.getMessage());
            throw new BadRequestException("Unable to load file: " + relativePath);
        }
    }
}
