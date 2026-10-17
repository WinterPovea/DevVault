package com.devvault.app.ui.add_snippet

import com.devvault.app.data.local.entity.CategoriaEntity

sealed interface AddSnippetUiState {
    data object Loading : AddSnippetUiState
    data class Success(
        val nombre: String = "",
        val contenido: String = "",
        val selectedCategoriaId: Long? = null,
        val categorias: List<CategoriaEntity> = emptyList(),
        val expandedDropdown: Boolean = false,
        val isSaved: Boolean = false,
        val showAddCategoriaDialog: Boolean = false,
        val nombreError: String? = null,
        val contenidoError: String? = null,
        val categoriaError: String? = null,
        val snackbarMessage: String? = null
    ) : AddSnippetUiState
    data class Error(val mensaje: String) : AddSnippetUiState
}