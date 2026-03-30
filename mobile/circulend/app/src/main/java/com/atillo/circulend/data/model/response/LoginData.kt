package com.atillo.circulend.data.model.response

import com.google.gson.annotations.SerializedName

data class LoginData(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String?
)
data class UserDto(
    val userId: Long,
    @SerializedName("firstname") val firstName: String,
    @SerializedName("lastname") val lastName: String,
    val email: String,
    val role: String
)