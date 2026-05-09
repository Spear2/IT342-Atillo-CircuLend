package edu.cit.atillo.circulend.features.inventory;

import edu.cit.atillo.circulend.entity.Category;
import edu.cit.atillo.circulend.entity.Item;
import edu.cit.atillo.circulend.entity.enums.ItemStatus;
import edu.cit.atillo.circulend.features.inventory.dto.CreateItemRequest;
import edu.cit.atillo.circulend.features.inventory.dto.ItemResponseDTO;
import edu.cit.atillo.circulend.features.inventory.dto.UpdateItemRequest;
import edu.cit.atillo.circulend.features.shared.dto.PagedResponseDTO;
import edu.cit.atillo.circulend.repository.CategoryRepository;
import edu.cit.atillo.circulend.repository.ItemRepository;
import edu.cit.atillo.circulend.features.shared.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    private static final long MAX_IMAGE_BYTES = 5 * 1024 * 1024;
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

        validateImage(imageFile);
        String imageUrl = fileStorageService.uploadItemImage(imageFile);

        Item item = new Item();
        item.setName(req.getName().trim());
        item.setDescription(req.getDescription());
        item.setAssetTag(req.getAssetTag().trim());
        item.setCategory(category);
        item.setStatus(ItemStatus.AVAILABLE);
        item.setImageFileUrl(imageUrl);

        try {
            return ItemResponseDTO.from(itemRepository.save(item));
        } catch (RuntimeException ex) {
            fileStorageService.deleteByUrl(imageUrl);
            throw ex;
        }
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

        String oldImageUrl = item.getImageFileUrl();
        String newImageUrl = null;

        if (imageFile != null && !imageFile.isEmpty()) {
            validateImage(imageFile);
            newImageUrl = fileStorageService.uploadItemImage(imageFile);
            item.setImageFileUrl(newImageUrl);
        }

        try {
            Item saved = itemRepository.save(item);
            if (newImageUrl != null && oldImageUrl != null && !oldImageUrl.equals(newImageUrl)) {
                fileStorageService.deleteByUrl(oldImageUrl);
            }
            return ItemResponseDTO.from(saved);
        } catch (RuntimeException ex) {
            if (newImageUrl != null) {
                fileStorageService.deleteByUrl(newImageUrl);
            }
            throw ex;
        }
    }

    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        String imageUrl = item.getImageFileUrl();
        itemRepository.delete(item);
        fileStorageService.deleteByUrl(imageUrl);
    }

    private void validateImage(MultipartFile file) {
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
    }

    public PagedResponseDTO<ItemResponseDTO> searchItems(String query, Long categoryId, String status, int page, int size) {
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

        Page<Item> result = itemRepository.searchItems(trimmedQuery, pattern, categoryId, itemStatus, pageable);
        List<ItemResponseDTO> content = result.getContent().stream().map(ItemResponseDTO::from).toList();
        return new PagedResponseDTO<>(content, result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages(), result.isLast());
    }
}
