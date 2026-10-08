package com.example.plantx.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class PlantData(
    val common_name: String?,
    val scientific_name: String?,
    val confidence: String?,
    val description: String?,
    val key_features: String?
)

data class PlantIdentifyResponse(
    val success: Boolean,
    val message: String?,
    val plant: PlantData?,
    val filename: String?
)

interface PlantApi {

    @Multipart
    @POST("api/plants/identify/")
    suspend fun identifyPlant(
        @Part image: MultipartBody.Part
    ): Response<PlantIdentifyResponse>
}

