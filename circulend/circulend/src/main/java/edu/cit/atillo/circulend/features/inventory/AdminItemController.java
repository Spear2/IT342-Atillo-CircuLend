package edu.cit.atillo.circulend.features.inventory;

import edu.cit.atillo.circulend.features.inventory.dto.CreateItemRequest;
import edu.cit.atillo.circulend.features.inventory.dto.ItemResponseDTO;
import edu.cit.atillo.circulend.features.inventory.dto.UpdateItemRequest;
import edu.cit.atillo.circulend.features.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/items")
@RequiredArgsConstructor
public class AdminItemController {

    private final ItemService itemService;

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ItemResponseDTO>> createItem(
            @Valid @ModelAttribute CreateItemRequest request,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        return ResponseEntity.ok(ApiResponse.success(itemService.createItem(request, imageFile)));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ItemResponseDTO>> updateItem(
            @PathVariable Long id,
            @ModelAttribute UpdateItemRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        return ResponseEntity.ok(ApiResponse.success(itemService.updateItem(id, request, imageFile)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok(ApiResponse.success("Item deleted successfully."));
    }
}
