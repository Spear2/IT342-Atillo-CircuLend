package com.atillo.circulend.data.repository

import com.atillo.circulend.data.model.request.LoginRequest
import com.atillo.circulend.data.model.request.RegisterRequest
import com.atillo.circulend.data.model.response.ApiResponse
import com.atillo.circulend.data.model.response.LoginData
import com.atillo.circulend.data.remote.AuthApiService
import com.atillo.circulend.data.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(
    private val api: AuthApiService = RetrofitClient.authApi
) {

    fun login(
        email: String,
        password: String,
        onResult: (AuthResult<LoginData>) -> Unit
    ) {
        val request = LoginRequest(email = email, password = password)
        api.login(request).enqueue(object : Callback<ApiResponse<LoginData>> {
            override fun onResponse(
                call: Call<ApiResponse<LoginData>>,
                response: Response<ApiResponse<LoginData>>
            ) {
                val body = response.body()
                if (response.isSuccessful && body?.success == true && body.data != null) {
                    onResult(AuthResult.Success(body.data))
                } else {
                    val msg = body?.error?.message ?: "Invalid email or password."
                    onResult(AuthResult.Error(msg))
                }
            }

            override fun onFailure(call: Call<ApiResponse<LoginData>>, t: Throwable) {
                onResult(AuthResult.Error("Network error: ${t.localizedMessage ?: "Unknown error"}"))
            }
        })
    }

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onResult: (AuthResult<LoginData>) -> Unit
    ) {
        val request = RegisterRequest(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password
        )

        api.register(request).enqueue(object : Callback<ApiResponse<LoginData>> {
            override fun onResponse(
                call: Call<ApiResponse<LoginData>>,
                response: Response<ApiResponse<LoginData>>
            ) {
                val body = response.body()
                if (response.isSuccessful && body?.success == true && body.data != null) {
                    onResult(AuthResult.Success(body.data))
                } else {
                    val msg = body?.error?.message ?: "Registration failed."
                    onResult(AuthResult.Error(msg))
                }
            }

            override fun onFailure(call: Call<ApiResponse<LoginData>>, t: Throwable) {
                onResult(AuthResult.Error("Network error: ${t.localizedMessage ?: "Unknown error"}"))
            }
        })
    }
}

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}