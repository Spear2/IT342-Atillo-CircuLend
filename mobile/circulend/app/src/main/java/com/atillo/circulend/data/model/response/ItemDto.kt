package com.atillo.circulend.data.model.response

data class ItemDto(
    val itemId: Long,
    val name: String,
    val description: String?,
    val assetTag: String?,
    val status: String,
    val imageFileUrl: String?,
    val categoryId: Long?,
    val categoryName: String?
)