package com.example.mohammedehsanullahshareef_hw1

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/items")
    suspend fun getItems(): Response<ItemsResponse>

    @Multipart
    @POST("api/items")
    suspend fun uploadItem(
        @Part image: MultipartBody.Part
    ): Response<ItemResponse>

    @DELETE("api/items/{id}")
    suspend fun deleteItem(@Path("id") id: String): Response<DeleteResponse>

    @POST("api/search")
    suspend fun searchOutfit(@Body request: SearchRequest): Response<SearchResponse>
}
