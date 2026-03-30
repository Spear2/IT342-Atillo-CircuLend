package com.atillo.circulend.util

// util/SessionManager.kt
import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("circulend_session", Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit().putString("token", token).apply()
    fun saveRole(role: String) = prefs.edit().putString("role", role).apply()

    fun getToken(): String? = prefs.getString("token", null)
    fun clear() = prefs.edit().clear().apply()
}