package com.example.designme.data.internet

import com.example.designme.models.designs
import com.example.designme.models.designsItem
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

/**
 * the Api which will send GET and POST requests to the server
 */
interface DesignmeApi {

    @GET("/designs")
    suspend fun getdesigns(
    ): Response<designs>

    @GET("/designs/search")
    suspend fun searchdesigns(
        //@QueryMap searchQuery: Map<String, String>
        @Query("title") title: String

    ): Response<designs>

    @GET("/designs/filter")
    suspend fun filterdesigns(
        @Query("category") category: String
    ): Response<designs>

    @POST("/designs")
    suspend fun addDesigns(
        @Body body :designsItem
    ):Response<String>

    @Multipart
    @POST("/images")
    suspend fun uploadImage(@Part filePart: MultipartBody.Part?): Response<String>


}
