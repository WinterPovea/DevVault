package com.devvault.app.data.repository

import com.devvault.app.data.local.dao.CategoriaDao
import com.devvault.app.data.local.dao.SnippetDao
import com.devvault.app.data.local.entity.CategoriaEntity
import com.devvault.app.data.local.entity.SnippetEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DevVaultRepositoryImpl @Inject constructor(
    private val categoriaDao: CategoriaDao,
    private val snippetDao: SnippetDao
) : DevVaultRepository {

    // Categorías
    override fun obtenerCategorias(): Flow<List<CategoriaEntity>> =
        categoriaDao.obtenerTodas()

    override suspend fun obtenerCategoriaPorId(id: Long): CategoriaEntity? =
        categoriaDao.obtenerPorId(id)

    override suspend fun insertarCategoria(categoria: CategoriaEntity): Long =
        categoriaDao.insertar(categoria)

    override suspend fun actualizarCategoria(categoria: CategoriaEntity) =
        categoriaDao.actualizar(categoria)

    override suspend fun eliminarCategoria(categoria: CategoriaEntity) =
        categoriaDao.eliminar(categoria)

    // Snippets
    override fun obtenerSnippets(): Flow<List<SnippetEntity>> =
        snippetDao.obtenerTodos()

    override fun obtenerSnippetsPorCategoria(categoriaId: Long): Flow<List<SnippetEntity>> =
        snippetDao.obtenerPorCategoria(categoriaId)

    override fun obtenerSnippetsFavoritos(): Flow<List<SnippetEntity>> =
        snippetDao.obtenerFavoritos()

    override suspend fun obtenerSnippetPorId(id: Long): SnippetEntity? =
        snippetDao.obtenerPorId(id)

    override fun buscarSnippets(query: String): Flow<List<SnippetEntity>> =
        snippetDao.buscar(query)

    override suspend fun insertarSnippet(snippet: SnippetEntity): Long =
        snippetDao.insertar(snippet)

    override suspend fun actualizarSnippet(snippet: SnippetEntity) =
        snippetDao.actualizar(snippet)

    override suspend fun eliminarSnippet(snippet: SnippetEntity) =
        snippetDao.eliminar(snippet)
}