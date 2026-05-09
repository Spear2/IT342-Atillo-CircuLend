package edu.cit.atillo.circulend.features.shared.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String uploadItemImage(MultipartFile file);

    void deleteByUrl(String url);
}
