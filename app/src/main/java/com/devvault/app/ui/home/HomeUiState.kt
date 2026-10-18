package com.devvault.app.ui.home

import com.devvault.app.ui.model.SnippetUi

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val searchQuery: String = "",
        val snippets: List<SnippetUi> = emptyList()
    ) : HomeUiState {
        val isSearching: Boolean
            get() = searchQuery.isNotBlank()

        val isEmpty: Boolean
            get() = snippets.isEmpty()
    }
    data class Error(val mensaje: String) : HomeUiState
}

sealed interface HomeEvent {
    data class OnSearchQueryChange(val query: String) : HomeEvent
    data class OnSnippetClick(val snippetId: Long) : HomeEvent
    data object OnClearSearch : HomeEvent
}