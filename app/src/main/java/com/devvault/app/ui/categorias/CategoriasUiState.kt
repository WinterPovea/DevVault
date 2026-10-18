package com.devvault.app.ui.categorias

import com.devvault.app.ui.model.CategoriaUi

typealias CategoriaConConteo = CategoriaUi

sealed interface CategoriasUiState {
    data object Loading : CategoriasUiState
    data class Success(
        val categorias: List<CategoriaUi>,
        val busqueda: String,
        val seleccionadaId: Long? = null
    ) : CategoriasUiState
    data class Error(val mensaje: String) : CategoriasUiState
}