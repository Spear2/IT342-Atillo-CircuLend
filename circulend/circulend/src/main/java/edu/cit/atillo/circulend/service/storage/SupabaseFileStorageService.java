package edu.cit.atillo.circulend.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "supabase")
public class SupabaseFileStorageService implements FileStorageService {

    @Value("${app.supabase.url}")
    private String supabaseUrl;

    @Value("${app.supabase.bucket}")
    private String bucket;

    @Value("${app.supabase.service-role-key}")
    private String serviceRoleKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String uploadItemImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is required");
        }

        String contentType = file.getContentType();
        String ext = extensionFromContentType(contentType);
        String objectPath = "items/" + UUID.randomUUID() + "." + ext;

        String base = trimTrailingSlash(supabaseUrl);
        String uploadUrl = base + "/storage/v1/object/" + bucket + "/" + objectPath;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .header("apikey", serviceRoleKey)
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("Content-Type", contentType)
                    .header("x-upsert", "false")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Supabase upload failed: " + response.body()
                );
            }

            return base + "/storage/v1/object/public/" + bucket + "/" + objectPath;

        } catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image");
        }
    }

    @Override
    public void deleteByUrl(String url) {
        if (url == null || url.isBlank()) return;

        String base = trimTrailingSlash(supabaseUrl);
        String prefix = base + "/storage/v1/object/public/" + bucket + "/";
        if (!url.startsWith(prefix)) return;

        String objectPath = url.substring(prefix.length());
        if (objectPath.isBlank()) return;

        String deleteUrl = base + "/storage/v1/object/" + bucket + "/" + objectPath;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(deleteUrl))
                    .header("apikey", serviceRoleKey)
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .DELETE()
                    .build();

            // best-effort cleanup; don't break main flow if deletion fails
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ignored) {
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

    private String trimTrailingSlash(String url) {
        return (url != null && url.endsWith("/")) ? url.substring(0, url.length() - 1) : url;
    }
}