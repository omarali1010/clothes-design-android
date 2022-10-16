package com.example.designme.data.database

import androidx.room.*
import com.example.designme.data.database.entites.designsEntity
import com.example.designme.data.database.entites.favEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface Designsdao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDesigns(designsEntity: designsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteDesign(favoritesEntity: favEntity)

    @Query("SELECT * FROM DESIGNS_TABLE ORDER BY id ASC")
    fun readDesigns(): Flow<List<designsEntity>>

    @Query("SELECT * FROM FAVORITE_DESIGNS_TABLE ORDER BY title ASC")
    fun readFavDesign(): Flow<List<favEntity>>

    @Delete
    suspend fun deleteFavoriteDesign(favoritesEntity: favEntity)

    @Query("DELETE FROM FAVORITE_DESIGNS_TABLE")
    suspend fun deleteAllFavoriteDesign()
}