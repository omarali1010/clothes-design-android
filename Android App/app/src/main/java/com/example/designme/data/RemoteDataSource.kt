package com.example.designme.data

import com.example.designme.data.internet.DesignmeApi
import com.example.designme.models.designs
import com.example.designme.models.designsItem
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject


class RemoteDataSource @Inject constructor(
    private val designmeApi: DesignmeApi
) {

    suspend fun getDesigns(): Response<designs> {
        return designmeApi.getdesigns()
    }

    suspend fun searchDesigns(title : String): Response<designs> {
        return designmeApi.searchdesigns(title)
    }

    suspend fun filterdesigns(category: String): Response<designs> {
        return designmeApi.filterdesigns(category)
    }

    suspend fun addDesigns(designs: designsItem) : Response<String>{
        return  designmeApi.addDesigns(designs)
    }

    suspend fun uploadphoto(filePart: MultipartBody.Part?) : Response<String>{
        return  designmeApi.uploadImage(filePart)
    }

}