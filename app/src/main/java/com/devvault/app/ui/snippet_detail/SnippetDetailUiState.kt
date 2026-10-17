package com.devvault.app.ui.snippet_detail

import com.devvault.app.data.local.entity.SnippetEntity

sealed interface SnippetDetailUiState {
    data object Loading : SnippetDetailUiState
    data class Success(
        val snippet: SnippetEntity,
        val categoriaNombre: String
    ) : SnippetDetailUiState
    data class Error(val mensaje: String) : SnippetDetailUiState
}
