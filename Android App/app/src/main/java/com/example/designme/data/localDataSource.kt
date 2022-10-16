package com.example.designme.data

import com.example.designme.data.database.Designsdao
import com.example.designme.data.database.entites.designsEntity
import com.example.designme.data.database.entites.favEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class localDataSource @Inject constructor(
    private val designsdao: Designsdao
) {

    fun readDesigns(): Flow<List<designsEntity>> {
        return designsdao.readDesigns()
    }

    fun readFavoriteDesigns(): Flow<List<favEntity>> {
        return designsdao.readFavDesign()
    }


    suspend fun insertDesign(DesignEntity: designsEntity) {
        designsdao.insertDesigns(DesignEntity)
    }

    suspend fun insertfavDesign(favoritesEntity: favEntity) {
        designsdao.insertFavoriteDesign(favoritesEntity)
    }

    suspend fun deleteFavoriteDesign(favoritesEntity: favEntity) {
        designsdao.deleteFavoriteDesign(favoritesEntity)
    }

    suspend fun deleteAllFavoriteDesigns() {
        designsdao.deleteAllFavoriteDesign()
    }

}