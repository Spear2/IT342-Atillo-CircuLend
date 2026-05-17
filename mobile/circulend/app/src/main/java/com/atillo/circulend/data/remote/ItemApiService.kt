package com.atillo.circulend.data.remote

import com.atillo.circulend.data.model.response.ApiResponse
import com.atillo.circulend.data.model.response.ItemDto
import com.atillo.circulend.data.model.response.PagedResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItemApiService {
    @GET("api/items")
    fun getItems(
        @Query("query") query: String? = null,
        @Query("categoryId") categoryId: Long? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 12
    ): Call<ApiResponse<PagedResponse<ItemDto>>>
}