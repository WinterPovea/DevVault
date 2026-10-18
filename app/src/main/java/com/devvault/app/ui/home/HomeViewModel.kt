package com.devvault.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvault.app.data.repository.DevVaultRepository
import com.devvault.app.ui.home.mapper.toUiList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DevVaultRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<HomeUiState> = combine(
        _searchQuery,
        _searchQuery
            .debounce(300.milliseconds)
            .flatMapLatest { query ->
                if (query.trim().isNotEmpty()) {
                    repository.buscarSnippets(query.trim())
                } else {
                    // Recolecta todos los recientes directamente desde la BD (Flow)
                    repository.obtenerSnippets().map { it.take(5) }
                }
            }
    ) { currentQuery, snippetsResult ->
        HomeUiState.Success(
            searchQuery = currentQuery,
            snippets = snippetsResult.toUiList()
        ) as HomeUiState
    }.catch { e ->
        emit(HomeUiState.Error(e.message ?: "Error al cargar snippets"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HomeUiState.Loading // Estado inicial de carga
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