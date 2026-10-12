package com.devvault.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvault.app.data.repository.DevVaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DevVaultRepository
    // ❌ Eliminamos DebugDataLoader. La BD ya se siembra sola desde el DatabaseModule.
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<HomeUiState> = combine(
        // 1. El texto de la búsqueda se actualiza INMEDIATAMENTE para la UI
        _searchQuery,
        // 2. La consulta a la BD reacciona al texto de búsqueda
        _searchQuery
            .debounce(300.milliseconds)
            .flatMapLatest { query ->
                if (query.trim().length >= 2) {
                    repository.buscarSnippets(query)
                } else {
                    // Recolecta todos los recientes directamente desde la BD (Flow)
                    repository.obtenerSnippets()
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
        initialValue = HomeUiState(isLoading = true) // Estado inicial de carga
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
                // El clic es manejado limpiamente a nivel de UI / Navegación
            }
        }
    }
}