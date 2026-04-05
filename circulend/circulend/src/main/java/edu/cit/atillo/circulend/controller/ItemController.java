package edu.cit.atillo.circulend.controller;

import edu.cit.atillo.circulend.dto.ApiResponse;
import edu.cit.atillo.circulend.dto.item.ItemResponseDTO;
import edu.cit.atillo.circulend.dto.page.PagedResponseDTO;
import edu.cit.atillo.circulend.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponseDTO<ItemResponseDTO>>> getItems(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                itemService.searchItems(query, categoryId, status, page, size)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponseDTO>> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(itemService.getItemById(id)));
    }
}