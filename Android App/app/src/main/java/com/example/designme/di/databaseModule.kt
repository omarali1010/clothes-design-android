package com.example.designme.di

import android.content.Context
import androidx.room.Room
import com.example.designme.data.database.DesignsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * database Hilt dependency Injection
 */
@Module
@InstallIn(SingletonComponent::class)
object databaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        DesignsDatabase::class.java,
        "DesignsDatabase"
    ).build()

    @Singleton
    @Provides
    fun provideDao(database: DesignsDatabase) = database.designsdao()

}