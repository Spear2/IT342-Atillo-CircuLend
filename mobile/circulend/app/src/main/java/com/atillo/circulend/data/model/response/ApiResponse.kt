package com.atillo.circulend.data.model.response

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: ErrorBody?,
    val timestamp: String?
)
data class ErrorBody(
    val code: String?,
    val message: String?,
    val details: Any?
)