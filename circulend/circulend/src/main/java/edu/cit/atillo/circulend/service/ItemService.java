package edu.cit.atillo.circulend.service;

import edu.cit.atillo.circulend.dto.item.CreateItemRequest;
import edu.cit.atillo.circulend.dto.item.ItemResponseDTO;
import edu.cit.atillo.circulend.dto.item.UpdateItemRequest;
import edu.cit.atillo.circulend.dto.page.PagedResponseDTO;
import edu.cit.atillo.circulend.entity.Category;
import edu.cit.atillo.circulend.entity.Item;
import edu.cit.atillo.circulend.entity.enums.ItemStatus;
import edu.cit.atillo.circulend.repository.CategoryRepository;
import edu.cit.atillo.circulend.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    @Value("${app.upload.dir:uploads/items}")
    private String uploadDir;

    private static final long MAX_IMAGE_BYTES = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/png", "image/jpeg", "image/webp"
    );

    public List<ItemResponseDTO> getAllItems() {
        return itemRepository.findAll().stream().map(ItemResponseDTO::from).toList();
    }

    public ItemResponseDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        return ItemResponseDTO.from(item);
    }

    public ItemResponseDTO createItem(CreateItemRequest req, MultipartFile imageFile) {
        if (itemRepository.existsByAssetTag(req.getAssetTag())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Asset tag already exists");
        }

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid categoryId"));

        String imageUrl = saveImage(imageFile);

        Item item = new Item();
        item.setName(req.getName().trim());
        item.setDescription(req.getDescription());
        item.setAssetTag(req.getAssetTag().trim());
        item.setCategory(category);
        item.setStatus(ItemStatus.AVAILABLE);
        item.setImageFileUrl(imageUrl);

        return ItemResponseDTO.from(itemRepository.save(item));
    }

    public ItemResponseDTO updateItem(Long id, UpdateItemRequest req, MultipartFile imageFile) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        if (req.getAssetTag() != null && !req.getAssetTag().isBlank()) {
            String newTag = req.getAssetTag().trim();
            if (itemRepository.existsByAssetTagAndItemIdNot(newTag, id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Asset tag already exists");
            }
            item.setAssetTag(newTag);
        }

        if (req.getName() != null && !req.getName().isBlank()) {
            item.setName(req.getName().trim());
        }

        if (req.getDescription() != null) {
            item.setDescription(req.getDescription());
        }

        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid categoryId"));
            item.setCategory(category);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            item.setImageFileUrl(saveImage(imageFile));
        }

        return ItemResponseDTO.from(itemRepository.save(item));
    }

    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        // Optional strict rule:
        // if (item.getStatus() == ItemStatus.BORROWED) throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete borrowed item");

        itemRepository.delete(item);
    }

    private String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is required");
        }

        if (file.getSize() > MAX_IMAGE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image exceeds 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image type. Allowed: png, jpeg, webp");
        }

        String ext = extensionFromContentType(contentType);
        String filename = UUID.randomUUID() + "." + ext;

        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);

            Path target = dir.resolve(filename).normalize();
            file.transferTo(target);

            return "/uploads/items/" + filename; // public URL path
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save image");
        }
    }

    private String extensionFromContentType(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/png" -> "png";
            case "image/jpeg" -> "jpg";
            case "image/webp" -> "webp";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported image type");
        };
    }


    public PagedResponseDTO<ItemResponseDTO> searchItems(
            String query, Long categoryId, String status, int page, int size
    ) {
        ItemStatus itemStatus = null;

        if (status != null && !status.isBlank()) {
            try {
                itemStatus = ItemStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "VALID-001: Invalid status. Allowed: AVAILABLE, BORROWED, MAINTENANCE"
                );
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        String trimmedQuery = (query == null || query.isBlank()) ? null : query.trim();
        String pattern = trimmedQuery != null ? "%" + trimmedQuery.toLowerCase() + "%" : null;

        Page<Item> result = itemRepository.searchItems(
                trimmedQuery,
                pattern,
                categoryId,
                itemStatus,
                pageable
        );

        List<ItemResponseDTO> content = result.getContent().stream().map(ItemResponseDTO::from).toList();
        return new PagedResponseDTO<>(content, result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages(), result.isLast());
    }
}