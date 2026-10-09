package com.devvault.app.presentation.categoria

import com.devvault.app.data.local.entity.SnippetEntity

sealed interface CategoriaDetailUiState {
    data object Loading : CategoriaDetailUiState
    data class Success(
        val nombreCategoria: String,
        val snippets: List<SnippetEntity>
    ) : CategoriaDetailUiState
    data class Error(val mensaje: String) : CategoriaDetailUiState
}