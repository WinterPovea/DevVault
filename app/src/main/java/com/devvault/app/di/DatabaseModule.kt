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
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.devvault.app.data.local.database.DatabaseSeeder
import android.util.Log

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
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .addCallback(object : RoomDatabase.Callback(){
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Log.d("DevVaultSeed", "onCreate called, seeding database")
                    DatabaseSeeder.poblar(db)
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    val cursor = db.query("SELECT COUNT(*) FROM categorias")
                    var count = 0
                    if (cursor.moveToFirst()) {
                        count = cursor.getInt(0)
                    }
                    cursor.close()
                    Log.d("DevVaultSeed", "onOpen: categorias count = $count")
                    if (count == 0) {
                        Log.d("DevVaultSeed", "onOpen: no categories, seeding database")
                        DatabaseSeeder.poblar(db)
                    }
                }
            })
            .build()
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