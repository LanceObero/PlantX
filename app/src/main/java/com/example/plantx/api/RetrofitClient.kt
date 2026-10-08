package com.example.plantx.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.201:8000/"

    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private val authInterceptor = Interceptor { chain ->

        val request = chain.request()
        val path = request.url.encodedPath

        val isAuthRequest =
            path == "/api/auth/login/" ||
                    path == "/api/auth/register/"

        val builder = request.newBuilder()

        if (!isAuthRequest) {

            val token = appContext
                .getSharedPreferences(
                    "PlantXPrefs",
                    Context.MODE_PRIVATE
                )
                .getString("access_token", null)

            if (!token.isNullOrEmpty()) {
                builder.addHeader(
                    "Authorization",
                    "Bearer $token"
                )
            }
        }

        chain.proceed(builder.build())
    }

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(180, TimeUnit.SECONDS)
        .callTimeout(180, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val plantApi: PlantApi by lazy {
        retrofit.create(PlantApi::class.java)
    }
}