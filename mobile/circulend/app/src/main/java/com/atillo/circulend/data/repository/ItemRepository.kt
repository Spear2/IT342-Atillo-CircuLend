package com.atillo.circulend.data.repository

import com.atillo.circulend.data.model.response.ApiResponse
import com.atillo.circulend.data.model.response.ItemDto
import com.atillo.circulend.data.model.response.PagedResponse
import com.atillo.circulend.data.remote.ItemApiService
import com.atillo.circulend.data.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemRepository(
    private val api: ItemApiService = RetrofitClient.itemApi
) {
    fun getItems(
        query: String? = null,
        categoryId: Long? = null,
        status: String? = null,
        page: Int = 0,
        size: Int = 12,
        onResult: (CatalogResult) -> Unit
    ) {
        api.getItems(query, categoryId, status, page, size)
            .enqueue(object : Callback<ApiResponse<PagedResponse<ItemDto>>> {
                override fun onResponse(
                    call: Call<ApiResponse<PagedResponse<ItemDto>>>,
                    response: Response<ApiResponse<PagedResponse<ItemDto>>>
                ) {
                    val body = response.body()
                    when {
                        response.code() == 401 -> onResult(CatalogResult.Unauthorized)
                        response.isSuccessful && body?.success == true && body.data != null -> {
                            val p = body.data
                            onResult(
                                CatalogResult.Success(
                                    items = p.content,
                                    page = p.page,
                                    size = p.size,
                                    totalElements = p.totalElements,
                                    totalPages = p.totalPages,
                                    last = p.last
                                )
                            )
                        }
                        else -> onResult(
                            CatalogResult.Error(
                                body?.error?.message ?: "Failed to load items (${response.code()})"
                            )
                        )
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<PagedResponse<ItemDto>>>,
                    t: Throwable
                ) {
                    onResult(CatalogResult.Error("Network error: ${t.localizedMessage ?: "Unknown"}"))
                }
            })
    }
}

sealed class CatalogResult {
    data class Success(
        val items: List<ItemDto>,
        val page: Int,
        val size: Int,
        val totalElements: Long,
        val totalPages: Int,
        val last: Boolean
    ) : CatalogResult()

    data object Unauthorized : CatalogResult()
    data class Error(val message: String) : CatalogResult()
}