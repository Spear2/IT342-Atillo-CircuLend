package com.atillo.circulend.data.remote

import com.atillo.circulend.data.model.response.ApiResponse
import com.atillo.circulend.data.model.response.UserDto
import retrofit2.Call
import retrofit2.http.GET

interface UserApiService {
    @GET("api/user/me")
    fun me(): Call<ApiResponse<UserDto>>
}