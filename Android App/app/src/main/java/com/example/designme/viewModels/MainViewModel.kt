package com.example.designme.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.designme.MyApplication
import com.example.designme.data.Repository
import com.example.designme.data.database.entites.designsEntity
import com.example.designme.data.database.entites.favEntity
import com.example.designme.models.designs
import com.example.designme.models.designsItem
import com.example.designme.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {


    /** ROOM DATABASE */

    /**
     * Two live data first for all designs and second for favourite Designs   which wil be observed for changes for the UI
     */
    val readDesigns: LiveData<List<designsEntity>> = repository.local.readDesigns().asLiveData()
    val readFavoriteDesigns: LiveData<List<favEntity>> = repository.local.readFavoriteDesigns().asLiveData()


    /**
     * inserting and deleting into the Room database
     */
    private fun insertDesigns(designentity: designsEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertDesign(designentity)
        }


    fun insertFavoriteDesign(favoritesEntity: favEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertfavDesign(favoritesEntity)
        }

    fun deleteFavoriteDesign(favoritesEntity: favEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteDesign(favoritesEntity)
        }

    fun deleteAllFavoriteDesigns() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllFavoriteDesigns()
        }







    //-----------Retrofit ------

    /**
     * Server side requests
     *
     *
     * Getting all designs ,searching and filtering
     */
    var DesignsResponse: MutableLiveData<NetworkResult<designs>> = MutableLiveData()
    var searchedDesignsResponse: MutableLiveData<NetworkResult<designs>> = MutableLiveData()
    var filterResponse: MutableLiveData<NetworkResult<designs>> = MutableLiveData()
   var addResponse : MutableLiveData<NetworkResult<String>> = MutableLiveData()
    fun getDesigns() = viewModelScope.launch {
        getDesignsSafeCall()
    }


    fun searchDesigns(searchQuery: String) = viewModelScope.launch {
        searchDesignsSafeCall(searchQuery)
    }

     fun addDesign(design: designsItem)=viewModelScope.launch(Dispatchers.IO){
        addDesignSafeCall(design)    }

    fun filterDesigns(category: String) = viewModelScope.launch {
        getaddgedignSafeCall(category)
    }

    fun uploadPhoto( pathname : Uri)=viewModelScope.launch {
        val inputStream = getApplication<MyApplication>().contentResolver.openInputStream(pathname)
        val imageByteArray = inputStream?.readBytes()

            val client = HttpClient(CIO)
            try {

                if (imageByteArray != null) {

                    val response: HttpResponse = client.submitFormWithBinaryData(
                        url = "http:/10.0.2.2:8080/images",
                        formData = formData {
                            append("description", "Ktor logoo")
                            append("image", imageByteArray, Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=ktor_logoo.png"
                                )
                            })
                        }
                    )


                    println(response.bodyAsText())
                }
                else{
                    Log.d("mainmain","no Uri")

                }
            }
            catch (e:Exception){
                Log.d("mainmain","$e")
            }

    }

    private suspend fun addImagesafecall(filePart: MultipartBody.Part?) {

        addResponse.postValue( NetworkResult.Loading())
        if (hasInternetConnection()) {
            try {
                Log.d("hithere","hh")

                val response = repository.remote.uploadphoto(filePart)
                Log.d("hithere",response.message())
                addResponse.postValue(handleAddDesign(response))

            } catch (e: Exception) {
                addResponse.postValue( NetworkResult.Error("error"))
            }
        } else {
            addResponse.postValue(NetworkResult.Error("No Internet Connection."))
        }
    }

    private suspend fun getDesignsSafeCall() {
        DesignsResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                Log.d("hithere","hh")

                val response = repository.remote.getDesigns()
                Log.d("hithere",response.message())
                DesignsResponse.value = handleDesignResponse(response)

                val designing = DesignsResponse.value!!.data
                if(designing != null) {
                    offlineCacheDesigns(designing)
                }
            } catch (e: Exception) {
                DesignsResponse.value = NetworkResult.Error("error")
            }
        } else {
            DesignsResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }



    private suspend fun addDesignSafeCall(design: designsItem) {
        addResponse.postValue( NetworkResult.Loading())
        if (hasInternetConnection()) {
            try {
                Log.d("hithere","hh")

                val response = repository.remote.addDesigns(design)
                Log.d("hithere",response.message())
                addResponse.postValue(handleAddDesign(response))

            } catch (e: Exception) {
                addResponse.postValue( NetworkResult.Error("error"))
            }
        } else {
            addResponse.postValue(NetworkResult.Error("No Internet Connection."))
        }
    }

    private suspend fun searchDesignsSafeCall(searcchtitle :String) {
        searchedDesignsResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchDesigns(searcchtitle)
                searchedDesignsResponse.value = handleDesignResponse(response)
            } catch (e: Exception) {
                searchedDesignsResponse.value = NetworkResult.Error("Desgins not found.")
            }
        } else {
            searchedDesignsResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun getaddgedignSafeCall(category: String) {
        filterResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.filterdesigns(category)
                filterResponse.value = handleadddesignResponse(response)

                val design = filterResponse.value!!.data
                if(design != null){
                }
            } catch (e: Exception) {
                filterResponse.value = NetworkResult.Error("Designs not found.")
            }
        } else {
            filterResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }


    /**
     *  this fuction handles the responses and look for errors by the response
     */
    private fun handleDesignResponse(response: Response<designs>): NetworkResult<designs> {
    when {
        response.message().toString().contains("timeout") -> {
            return NetworkResult.Error("Timeout")
        }
        response.code() == 402 -> {
            return NetworkResult.Error("API Key Limited.")
        }
        response.body()!!.isNullOrEmpty() -> {
            return NetworkResult.Error("Designs not found.")
        }
        response.isSuccessful -> {
            val design = response.body()
            return NetworkResult.Success(design!!)
        }
        else -> {
            return NetworkResult.Error(response.message())
        }
    }
}
    private fun handleAddDesign(response: Response<String>): NetworkResult<String> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }
            response.body()!!.isEmpty() -> {
                return NetworkResult.Error("Designs not found.")
            }
            response.isSuccessful -> {
                val design = response.body()
                return NetworkResult.Success(design!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }


    private fun handleadddesignResponse(response: Response<designs>): NetworkResult<designs> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }
            response.isSuccessful -> {
                val design = response.body()
                NetworkResult.Success(design!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    /**
     * THis function checks if the USer has internet Connection or not
     */
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }


    /**
     * inserts the Designs in the Database
     */
    private fun offlineCacheDesigns(design: designs) {
        val designsEntity = designsEntity(design)
        insertDesigns(designsEntity)
    }




}
