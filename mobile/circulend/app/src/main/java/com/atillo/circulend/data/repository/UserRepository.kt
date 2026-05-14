package com.atillo.circulend.data.repository

import com.atillo.circulend.data.model.response.ApiResponse
import com.atillo.circulend.data.model.response.UserDto
import com.atillo.circulend.data.remote.RetrofitClient
import com.atillo.circulend.data.remote.UserApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(
    private val api: UserApiService = RetrofitClient.userApi
) {
    fun me(onResult: (AuthResult<UserDto>) -> Unit) {
        api.me().enqueue(object : Callback<ApiResponse<UserDto>> {
            override fun onResponse(
                call: Call<ApiResponse<UserDto>>,
                response: Response<ApiResponse<UserDto>>
            ) {
                val body = response.body()
                if (response.isSuccessful && body?.success == true && body.data != null) {
                    onResult(AuthResult.Success(body.data))
                } else {
                    if (response.code() == 401) {
                        onResult(AuthResult.Error("UNAUTHORIZED"))
                    } else {
                        onResult(AuthResult.Error(body?.error?.message ?: "Request failed (${response.code()})"))
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserDto>>, t: Throwable) {
                onResult(AuthResult.Error("Network error: ${t.localizedMessage}"))
            }
        })
    }
}