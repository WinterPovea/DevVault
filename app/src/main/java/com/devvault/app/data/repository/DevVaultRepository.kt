package com.devvault.app.data.repository

import com.devvault.app.data.local.entity.CategoriaEntity
import com.devvault.app.data.local.entity.SnippetEntity
import kotlinx.coroutines.flow.Flow

interface DevVaultRepository {

    // Categorías
    fun obtenerCategorias(): Flow<List<CategoriaEntity>>
    suspend fun obtenerCategoriaPorId(id: Long): CategoriaEntity?
    suspend fun insertarCategoria(categoria: CategoriaEntity): Long
    suspend fun actualizarCategoria(categoria: CategoriaEntity)
    suspend fun eliminarCategoria(categoria: CategoriaEntity)

    // Snippets
    fun obtenerSnippets(): Flow<List<SnippetEntity>>
    fun obtenerSnippetsPorCategoria(categoriaId: Long): Flow<List<SnippetEntity>>
    fun obtenerSnippetsFavoritos(): Flow<List<SnippetEntity>>
    suspend fun obtenerSnippetPorId(id: Long): SnippetEntity?
    fun buscarSnippets(query: String): Flow<List<SnippetEntity>>
    suspend fun insertarSnippet(snippet: SnippetEntity): Long
    suspend fun actualizarSnippet(snippet: SnippetEntity)
    suspend fun eliminarSnippet(snippet: SnippetEntity)
}