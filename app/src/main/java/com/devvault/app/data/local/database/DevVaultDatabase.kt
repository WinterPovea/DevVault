package com.devvault.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devvault.app.data.local.dao.CategoriaDao
import com.devvault.app.data.local.dao.SnippetDao
import com.devvault.app.data.local.entity.CategoriaEntity
import com.devvault.app.data.local.entity.SnippetEntity

@Database(
    entities = [
        CategoriaEntity::class,
        SnippetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DevVaultDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao

    abstract fun snippetDao(): SnippetDao
}