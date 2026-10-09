package com.example.plantx.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
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

data class PlantHistoryResponse(
    val success: Boolean,
    val count: Int,
    val history: List<PlantHistoryItem>
)

data class PlantHistoryItem(
    val id: Int,
    @SerializedName("common_name")
    val commonName: String,
    @SerializedName("scientific_name")
    val scientificName: String,
    val confidence: String,
    val description: String?,
    @SerializedName("key_features")
    val keyFeatures: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("identified_at")
    val identifiedAt: String
)

interface PlantApi {

    @Multipart
    @POST("api/plants/identify/")
    suspend fun identifyPlant(
        @Part image: MultipartBody.Part
    ): Response<PlantIdentifyResponse>

    @GET("api/plants/history/")
    suspend fun getPlantHistory(): Response<PlantHistoryResponse>
}