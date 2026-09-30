package com.devvault.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "snippets",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("categoriaId")
    ]
)
data class SnippetEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val categoriaId: Long,

    val titulo: String,

    val contenido: String,

    val lenguaje: String?,

    val esFavorito: Boolean = false,

    val fechaCreacion: Long
)