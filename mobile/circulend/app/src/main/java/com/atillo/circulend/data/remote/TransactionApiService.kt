package com.atillo.circulend.data.remote

import com.atillo.circulend.data.model.request.BorrowRequest
import com.atillo.circulend.data.model.response.ApiResponse
import com.atillo.circulend.data.model.response.BorrowResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionApiService {
    @POST("api/transactions/borrow")
    fun borrow(@Body request: BorrowRequest): Call<ApiResponse<BorrowResponse>>
}
