package edu.cit.atillo.circulend.features.shared.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class StorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void localStorageUploadsAndDeletesFile() throws Exception {
        LocalFileStorageService service = new LocalFileStorageService();
        ReflectionTestUtils.setField(service, "uploadDir", tempDir.toString());

        MockMultipartFile file = new MockMultipartFile(
                "imageFile", "img.png", "image/png", new byte[]{1, 2, 3}
        );

        String url = service.uploadItemImage(file);
        assertTrue(url.startsWith("/uploads/items/"));

        String filename = url.substring("/uploads/items/".length());
        Path storedPath = tempDir.resolve(filename);
        assertTrue(Files.exists(storedPath));

        service.deleteByUrl(url);
        assertFalse(Files.exists(storedPath));
    }

    @Test
    void localStorageRejectsUnsupportedImageType() {
        LocalFileStorageService service = new LocalFileStorageService();
        ReflectionTestUtils.setField(service, "uploadDir", tempDir.toString());

        MockMultipartFile file = new MockMultipartFile(
                "imageFile", "img.gif", "image/gif", new byte[]{1}
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.uploadItemImage(file));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void supabaseStorageRejectsNullFile() {
        SupabaseFileStorageService service = new SupabaseFileStorageService();
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.uploadItemImage(null));
        assertEquals(400, ex.getStatusCode().value());
    }
}
