package edu.cit.atillo.circulend.features.inventory;

import edu.cit.atillo.circulend.entity.Category;
import edu.cit.atillo.circulend.entity.Item;
import edu.cit.atillo.circulend.entity.enums.ItemStatus;
import edu.cit.atillo.circulend.features.inventory.dto.CreateItemRequest;
import edu.cit.atillo.circulend.features.inventory.dto.UpdateItemRequest;
import edu.cit.atillo.circulend.features.shared.storage.FileStorageService;
import edu.cit.atillo.circulend.repository.CategoryRepository;
import edu.cit.atillo.circulend.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ItemService itemService;

    @Test
    void createItemThrowsOnDuplicateAssetTag() {
        CreateItemRequest req = new CreateItemRequest();
        req.setAssetTag("A-100");
        req.setCategoryId(1L);
        req.setName("Laptop");
        MockMultipartFile file = new MockMultipartFile("imageFile", "a.png", "image/png", new byte[]{1});

        when(itemRepository.existsByAssetTag("A-100")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> itemService.createItem(req, file));
        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void createItemSavesWithAvailableStatus() {
        CreateItemRequest req = new CreateItemRequest();
        req.setAssetTag("A-100");
        req.setCategoryId(1L);
        req.setName("Laptop");
        req.setDescription("desc");
        MockMultipartFile file = new MockMultipartFile("imageFile", "a.png", "image/png", new byte[]{1, 2, 3});

        Category category = new Category();
        category.setCategoryId(1L);
        category.setName("Electronics");

        when(itemRepository.existsByAssetTag("A-100")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(fileStorageService.uploadItemImage(file)).thenReturn("/uploads/items/x.png");
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> {
            Item saved = i.getArgument(0);
            saved.setItemId(22L);
            return saved;
        });

        var result = itemService.createItem(req, file);

        assertEquals(22L, result.getItemId());
        assertEquals(ItemStatus.AVAILABLE.name(), result.getStatus());
    }

    @Test
    void updateItemThrowsWhenAssetTagAlreadyExists() {
        Item existing = new Item();
        existing.setItemId(10L);
        existing.setAssetTag("OLD");

        UpdateItemRequest req = new UpdateItemRequest();
        req.setAssetTag("NEW");

        when(itemRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(itemRepository.existsByAssetTagAndItemIdNot("NEW", 10L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> itemService.updateItem(10L, req, null)
        );
        assertEquals(409, ex.getStatusCode().value());
    }
}
