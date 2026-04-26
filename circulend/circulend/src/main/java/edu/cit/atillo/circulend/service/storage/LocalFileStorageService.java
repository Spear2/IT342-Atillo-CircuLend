package edu.cit.atillo.circulend.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {

    @Value("${app.upload.dir:uploads/items}")
    private String uploadDir;

    @Override
    public String uploadItemImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is required");
        }

        String ext = extensionFromContentType(file.getContentType());
        String filename = UUID.randomUUID() + "." + ext;

        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            Path target = dir.resolve(filename).normalize();
            file.transferTo(target);
            return "/uploads/items/" + filename;
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save image");
        }
    }

    @Override
    public void deleteByUrl(String url) {
        if (url == null || url.isBlank()) return;
        if (!url.startsWith("/uploads/items/")) return;

        String filename = url.substring("/uploads/items/".length());
        if (filename.isBlank()) return;

        try {
            Path target = Paths.get(uploadDir).resolve(filename).normalize();
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
            // Keep delete best-effort; do not fail API due to cleanup issue
        }
    }

    private String extensionFromContentType(String contentType) {
        if (contentType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image type");
        }
        return switch (contentType.toLowerCase()) {
            case "image/png" -> "png";
            case "image/jpeg" -> "jpg";
            case "image/webp" -> "webp";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported image type");
        };
    }
}