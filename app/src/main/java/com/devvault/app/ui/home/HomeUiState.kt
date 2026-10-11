package com.devvault.app.ui.home

import com.devvault.app.data.local.entity.SnippetEntity

data class HomeUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val snippets: List<SnippetEntity> = emptyList(),
    val errorMessage: String? = null
) {
    val isSearching: Boolean
        get() = searchQuery.isNotBlank()

    val isEmpty: Boolean
        get() = !isLoading && snippets.isEmpty()
}

sealed interface HomeEvent {
    data class OnSearchQueryChange(val query: String) : HomeEvent
    data class OnSnippetClick(val snippetId: Long) : HomeEvent
    data object OnClearSearch : HomeEvent
}