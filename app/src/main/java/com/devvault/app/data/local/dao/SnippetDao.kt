package com.devvault.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.devvault.app.data.local.entity.SnippetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SnippetDao {

    @Query("SELECT * FROM snippets ORDER BY fechaCreacion DESC")
    fun obtenerTodos(): Flow<List<SnippetEntity>>

    @Query("SELECT * FROM snippets WHERE categoriaId = :categoriaId ORDER BY titulo ASC")
    fun obtenerPorCategoria(categoriaId: Long): Flow<List<SnippetEntity>>

    @Query("SELECT * FROM snippets WHERE esFavorito = 1")
    fun obtenerFavoritos(): Flow<List<SnippetEntity>>

    @Query("SELECT * FROM snippets WHERE id = :id")
    suspend fun obtenerPorId(id: Long): SnippetEntity?

    @Query("""
        SELECT * FROM snippets 
        WHERE titulo LIKE '%' || :query || '%' 
           OR contenido LIKE '%' || :query || '%'
    """)
    fun buscar(query: String): Flow<List<SnippetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(snippet: SnippetEntity): Long

    @Update
    suspend fun actualizar(snippet: SnippetEntity)

    @Delete
    suspend fun eliminar(snippet: SnippetEntity)
}