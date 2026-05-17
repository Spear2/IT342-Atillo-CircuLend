package com.atillo.circulend.data.remote

import android.content.Context
import com.atillo.circulend.util.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.174.106.226:8080/"

    private lateinit var sessionManager: SessionManager

    // call this once at app/login startup
    fun init(context: Context) {
        sessionManager = SessionManager(context.applicationContext)
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        val path = request.url.encodedPath
        val token = if (::sessionManager.isInitialized) sessionManager.getToken() else null

        val newRequest = request.newBuilder().apply {
            if (!path.startsWith("/api/auth/") && !token.isNullOrBlank()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        chain.proceed(newRequest)
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    val userApi: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
    val itemApi: ItemApiService by lazy { retrofit.create(ItemApiService::class.java) }
}