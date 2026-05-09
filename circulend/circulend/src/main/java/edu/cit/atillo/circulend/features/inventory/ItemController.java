package edu.cit.atillo.circulend.features.inventory;

import edu.cit.atillo.circulend.features.inventory.dto.ItemResponseDTO;
import edu.cit.atillo.circulend.features.shared.dto.ApiResponse;
import edu.cit.atillo.circulend.features.shared.dto.PagedResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
