package com.devvault.app.ui.snippet_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.data.repository.DevVaultRepository
import com.devvault.app.navigation.DevVaultRoute
import com.devvault.app.ui.snippet_detail.mapper.toEntity
import com.devvault.app.ui.snippet_detail.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SnippetDetailUiEvent {
    data class ShowToast(val message: String) : SnippetDetailUiEvent
    data object NavigateBack : SnippetDetailUiEvent
}

@HiltViewModel
class SnippetDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DevVaultRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<DevVaultRoute.SnippetDetail>()

    private val _uiState = MutableStateFlow<SnippetDetailUiState>(SnippetDetailUiState.Loading)
    val uiState: StateFlow<SnippetDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<SnippetDetailUiEvent>(Channel.BUFFERED)
    val events: Flow<SnippetDetailUiEvent> = _events.receiveAsFlow()

    init {
        cargarSnippet(route.snippetId)
    }

    private fun cargarSnippet(id: Long) {
        viewModelScope.launch {
            _uiState.value = SnippetDetailUiState.Loading
            val snippet = repository.obtenerSnippetPorId(id)
            if (snippet != null) {
                val categoria = repository.obtenerCategoriaPorId(snippet.categoriaId)
                val catNombre = categoria?.nombre ?: snippet.lenguaje ?: "Snippet"
                val catColor = categoria?.color ?: 0xFF7F52FF
                _uiState.value = SnippetDetailUiState.Success(
                    snippet = snippet.toUi(catNombre, catColor),
                    categoriaNombre = catNombre
                )
            } else {
                _uiState.value = SnippetDetailUiState.Error("Snippet no encontrado")
            }
        }
    }

    fun alternarFavorito() {
        val current = _uiState.value as? SnippetDetailUiState.Success ?: return
        viewModelScope.launch {
            val nuevoFavorito = !current.snippet.esFavorito
            val fechaFavorito = if (nuevoFavorito) System.currentTimeMillis() else null
            val updated = current.snippet.copy(
                esFavorito = nuevoFavorito,
                fechaFavorito = fechaFavorito
            )
            repository.actualizarSnippet(updated.toEntity())
            _uiState.value = current.copy(snippet = updated)
            _events.send(
                SnippetDetailUiEvent.ShowToast(
                    if (nuevoFavorito) "Añadido a favoritos" else "Eliminado de favoritos"
                )
            )
        }
    }

    fun eliminarSnippet() {
        val current = _uiState.value as? SnippetDetailUiState.Success ?: return
        viewModelScope.launch {
            repository.eliminarSnippet(current.snippet.toEntity())
            _events.send(SnippetDetailUiEvent.ShowToast("Snippet eliminado"))
            _events.send(SnippetDetailUiEvent.NavigateBack)
        }
    }

    fun notificarCopiado() {
        viewModelScope.launch {
            _events.send(SnippetDetailUiEvent.ShowToast("Código copiado al portapapeles"))
        }
    }
}
