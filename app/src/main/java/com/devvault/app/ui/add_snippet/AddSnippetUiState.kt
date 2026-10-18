package com.devvault.app.ui.add_snippet

import com.devvault.app.ui.model.CategoriaUi

sealed interface AddSnippetUiState {
    data object Loading : AddSnippetUiState
    data class Success(
        val nombre: String = "",
        val contenido: String = "",
        val selectedCategoriaId: Long? = null,
        val categorias: List<CategoriaUi> = emptyList(),
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