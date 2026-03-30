package com.atillo.circulend.data.remote

import com.atillo.circulend.data.model.request.LoginRequest
import com.atillo.circulend.data.model.request.RegisterRequest
import com.atillo.circulend.data.model.response.ApiResponse
import com.atillo.circulend.data.model.response.LoginData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<ApiResponse<LoginData>>

    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<ApiResponse<LoginData>>
}