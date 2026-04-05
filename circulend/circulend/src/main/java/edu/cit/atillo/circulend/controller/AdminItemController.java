package edu.cit.atillo.circulend.controller;

import edu.cit.atillo.circulend.dto.ApiResponse;
import edu.cit.atillo.circulend.dto.item.CreateItemRequest;
import edu.cit.atillo.circulend.dto.item.ItemResponseDTO;
import edu.cit.atillo.circulend.dto.item.UpdateItemRequest;
import edu.cit.atillo.circulend.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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