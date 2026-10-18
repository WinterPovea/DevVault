package com.devvault.app.ui.categoria

import com.devvault.app.ui.model.SnippetUi

sealed interface CategoriaDetailUiState {
    data object Loading : CategoriaDetailUiState
    data class Success(
        val nombreCategoria: String,
        val snippets: List<SnippetUi>
    ) : CategoriaDetailUiState
    data class Error(val mensaje: String) : CategoriaDetailUiState
}