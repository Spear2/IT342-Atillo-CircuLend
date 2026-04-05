package edu.cit.atillo.circulend.dto.item;

import lombok.Data;

@Data
public class UpdateItemRequest {
    private String name;
    private String description;
    private Long categoryId;
    private String assetTag;
}