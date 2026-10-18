package com.devvault.app.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvault.app.data.repository.DevVaultRepository
import com.devvault.app.ui.favoritos.mapper.toEntity
import com.devvault.app.ui.favoritos.mapper.toUi
import com.devvault.app.ui.model.SnippetUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FavoritosUiState {
    data object Loading : FavoritosUiState
    data class Success(
        val favoritos: List<SnippetUi> = emptyList(),
        val searchQuery: String = ""
    ) : FavoritosUiState {
        val isEmpty: Boolean
            get() = favoritos.isEmpty()
    }
    data class Error(val mensaje: String) : FavoritosUiState
}

sealed interface FavoritosUiEvent {
    data class ShowToast(val message: String) : FavoritosUiEvent
}

@HiltViewModel
class FavoritosViewModel @Inject constructor(
    private val repository: DevVaultRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    private val _events = Channel<FavoritosUiEvent>(Channel.BUFFERED)
    val events: Flow<FavoritosUiEvent> = _events.receiveAsFlow()

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

        val ordenados = filtrados.sortedByDescending { it.fechaFavorito ?: 0L }

        FavoritosUiState.Success(
            favoritos = ordenados.map { it.toUi() },
            searchQuery = query
        ) as FavoritosUiState
    }.catch { e ->
        emit(FavoritosUiState.Error(e.message ?: "Error al cargar favoritos"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FavoritosUiState.Loading
    )

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    fun alternarFavorito(snippet: SnippetUi) {
        viewModelScope.launch {
            val nuevoEstadoFavorito = !snippet.esFavorito
            val tiempoActual = if (nuevoEstadoFavorito) System.currentTimeMillis() else null

            repository.actualizarSnippet(
                snippet.toEntity().copy(
                    esFavorito = nuevoEstadoFavorito,
                    fechaFavorito = tiempoActual
                )
            )
        }
    }

    fun notificarCopiado() {
        viewModelScope.launch {
            _events.send(FavoritosUiEvent.ShowToast("Código copiado al portapapeles"))
        }
    }
}