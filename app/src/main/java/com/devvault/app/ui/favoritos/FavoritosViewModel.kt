package com.devvault.app.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.data.repository.DevVaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FavoritosUiState {
    data object Loading : FavoritosUiState
    data object Empty : FavoritosUiState
    data class Success(val favoritos: List<SnippetEntity>) : FavoritosUiState
}

@HiltViewModel
class FavoritosViewModel @Inject constructor(
    private val repository: DevVaultRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val uiState: StateFlow<FavoritosUiState> = combine(
        repository.obtenerSnippetsFavoritos(),
        _searchQuery
    ) { favoritos, query ->

        val filtrados = if (query.isBlank()) {
            favoritos
        } else {
            favoritos.filter {
                it.titulo.contains(query, ignoreCase = true) ||
                        it.contenido.contains(query, ignoreCase = true)
            }
        }

        val ordenados = filtrados.sortedByDescending { it.fechaFavorito }

        if (ordenados.isEmpty()) {
            FavoritosUiState.Empty
        } else {
            FavoritosUiState.Success(ordenados)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FavoritosUiState.Loading
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun alternarFavorito(snippet: SnippetEntity) {
        viewModelScope.launch {
            val nuevoEstadoFavorito = !snippet.esFavorito
            val tiempoActual = if (nuevoEstadoFavorito) System.currentTimeMillis() else 0L

            repository.actualizarSnippet(
                snippet.copy(
                    esFavorito = nuevoEstadoFavorito,
                    fechaFavorito = tiempoActual
                )
            )
        }
    }
}