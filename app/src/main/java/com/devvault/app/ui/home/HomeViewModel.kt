package com.devvault.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvault.app.data.repository.DevVaultRepository
import com.devvault.app.util.DebugDataLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DevVaultRepository,
    private val debugDataLoader: DebugDataLoader
) : ViewModel() {

    init {
        viewModelScope.launch {
            debugDataLoader.loadIfEmpty()
        }
    }

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<HomeUiState> = combine(
        // 1. El texto de la búsqueda se actualiza INMEDIATAMENTE para que puedas escribir fluido
        _searchQuery,
        // 2. La consulta a la base de datos se desacopla con el debounce de 300ms
        _searchQuery
            .debounce(300.milliseconds)
            .flatMapLatest { query ->
                if (query.trim().length >= 2) {
                    repository.buscarSnippets(query)
                } else {
                    repository.obtenerSnippets().map { it.take(10) }
                }
            }
    ) { currentQuery, snippetsResult ->
        HomeUiState(
            searchQuery = currentQuery,
            isLoading = false,
            snippets = snippetsResult
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnSearchQueryChange -> {
                _searchQuery.value = event.query
            }
            HomeEvent.OnClearSearch -> {
                _searchQuery.value = ""
            }
            is HomeEvent.OnSnippetClick -> {
                // Manejado por la navegación
            }
        }
    }
}