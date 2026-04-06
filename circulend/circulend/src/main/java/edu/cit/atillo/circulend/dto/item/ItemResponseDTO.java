package edu.cit.atillo.circulend.dto.item;

import edu.cit.atillo.circulend.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemResponseDTO {
    private Long itemId;
    private String name;
    private String description;
    private String assetTag;
    private String status;
    private String imageFileUrl;
    private Long categoryId;
    private String categoryName;

    public static ItemResponseDTO from(Item item) {
        return new ItemResponseDTO(
                item.getItemId(),
                item.getName(),
                item.getDescription(),
                item.getAssetTag(),
                item.getStatus() != null ? item.getStatus().name() : "AVAILABLE",
                item.getImageFileUrl(),
                item.getCategory() != null ? item.getCategory().getCategoryId() : null,
                item.getCategory() != null ? item.getCategory().getName() : null
        );
    }
}