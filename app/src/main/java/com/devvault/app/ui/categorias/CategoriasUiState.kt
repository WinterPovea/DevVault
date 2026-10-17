package com.devvault.app.ui.categorias

data class CategoriaConConteo(
    val id: Long,
    val nombre: String,
    val color: Long,
    val totalSnippets: Int
)

sealed interface CategoriasUiState {
    data object Loading : CategoriasUiState
    data class Success(
        val categorias: List<CategoriaConConteo>,
        val busqueda: String,
        val seleccionadaId: Long? = null
    ) : CategoriasUiState
    data class Error(val mensaje: String) : CategoriasUiState
}