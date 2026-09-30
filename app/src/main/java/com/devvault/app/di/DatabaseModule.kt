package com.devvault.app.di

import android.content.Context
import androidx.room.Room
import com.devvault.app.data.local.dao.CategoriaDao
import com.devvault.app.data.local.dao.SnippetDao
import com.devvault.app.data.local.database.DevVaultDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDevVaultDatabase(
        @ApplicationContext context: Context
    ): DevVaultDatabase {
        return Room.databaseBuilder(
            context,
            DevVaultDatabase::class.java,
            "devvault_database"
        ).build()
    }

    @Provides
    fun provideCategoriaDao(database: DevVaultDatabase): CategoriaDao {
        return database.categoriaDao()
    }

    @Provides
    fun provideSnippetDao(database: DevVaultDatabase): SnippetDao {
        return database.snippetDao()
    }
}