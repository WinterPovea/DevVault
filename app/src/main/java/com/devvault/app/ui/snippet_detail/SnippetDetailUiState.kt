package com.devvault.app.ui.snippet_detail

import com.devvault.app.ui.model.SnippetUi

sealed interface SnippetDetailUiState {
    data object Loading : SnippetDetailUiState
    data class Success(
        val snippet: SnippetUi,
        val categoriaNombre: String
    ) : SnippetDetailUiState
    data class Error(val mensaje: String) : SnippetDetailUiState
}
