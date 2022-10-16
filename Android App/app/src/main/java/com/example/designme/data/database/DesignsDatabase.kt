package com.example.designme.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.designme.data.database.entites.designsEntity
import com.example.designme.data.database.entites.favEntity


@Database(
    entities = [designsEntity::class, favEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DesignsTypeConverter::class)
abstract class DesignsDatabase :RoomDatabase(){

    abstract fun designsdao(): Designsdao

}


