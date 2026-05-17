package com.atillo.circulend.data.repository

import com.atillo.circulend.data.model.request.BorrowRequest
import com.atillo.circulend.data.model.response.ApiResponse
import com.atillo.circulend.data.model.response.BorrowResponse
import com.atillo.circulend.data.remote.RetrofitClient
import com.atillo.circulend.data.remote.TransactionApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionRepository(
    private val api: TransactionApiService = RetrofitClient.transactionApi
) {
    fun borrow(itemId: Long, assetTag: String, onResult: (BorrowResult) -> Unit) {
        api.borrow(BorrowRequest(itemId = itemId, assetTag = assetTag))
            .enqueue(object : Callback<ApiResponse<BorrowResponse>> {
                override fun onResponse(
                    call: Call<ApiResponse<BorrowResponse>>,
                    response: Response<ApiResponse<BorrowResponse>>
                ) {
                    val body = response.body()
                    when {
                        response.code() == 401 -> onResult(BorrowResult.Unauthorized)
                        response.isSuccessful && body?.success == true && body.data != null ->
                            onResult(BorrowResult.Success(body.data))
                        else -> onResult(
                            BorrowResult.Error(
                                body?.error?.message ?: "Borrow failed (${response.code()})"
                            )
                        )
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<BorrowResponse>>,
                    t: Throwable
                ) {
                    onResult(
                        BorrowResult.Error("Network error: ${t.localizedMessage ?: "Unknown"}")
                    )
                }
            })
    }
}

sealed class BorrowResult {
    data class Success(val data: BorrowResponse) : BorrowResult()
    data object Unauthorized : BorrowResult()
    data class Error(val message: String) : BorrowResult()
}
