package com.devvault.app.ui.categoria

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.devvault.app.data.local.entity.CategoriaEntity
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.data.repository.DevVaultRepository
import com.devvault.app.navigation.DevVaultRoute
import com.devvault.app.ui.categoria.mapper.toEntity
import com.devvault.app.ui.categoria.mapper.toUiList
import com.devvault.app.ui.model.SnippetUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CategoriaDetailUiEvent {
    data class ShowMessage(val message: String) : CategoriaDetailUiEvent
}

@HiltViewModel
class CategoriaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DevVaultRepository
) : ViewModel() {

    private val categoriaId = savedStateHandle.toRoute<DevVaultRoute.CategoriaDetail>().categoriaId

    private val _events = Channel<CategoriaDetailUiEvent>(Channel.BUFFERED)
    val events: Flow<CategoriaDetailUiEvent> = _events.receiveAsFlow()

    fun notificarCopiado() {
        viewModelScope.launch {
            _events.send(CategoriaDetailUiEvent.ShowMessage("Código copiado al portapapeles"))
        }
    }

    val uiState: StateFlow<CategoriaDetailUiState> =
        combine<List<CategoriaEntity>, List<SnippetEntity>, CategoriaDetailUiState>(
            repository.obtenerCategorias(),
            repository.obtenerSnippetsPorCategoria(categoriaId)
        ) { categorias, snippets ->
            val categoria = categorias.firstOrNull { it.id == categoriaId }
            if (categoria == null) {
                CategoriaDetailUiState.Error("Categoría no encontrada")
            } else {
                CategoriaDetailUiState.Success(
                    categoria.nombre,
                    snippets.toUiList(categoria.nombre, categoria.color)
                )
            }
        }
            .catch { emit(CategoriaDetailUiState.Error(it.message ?: "Error desconocido")) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoriaDetailUiState.Loading)

    fun onToggleFavorito(snippet: SnippetUi) {
        viewModelScope.launch {
            val nuevoEstado = !snippet.esFavorito
            val nuevaFecha = if (nuevoEstado) System.currentTimeMillis() else null
            repository.actualizarSnippet(
                snippet.toEntity().copy(
                    esFavorito = nuevoEstado,
                    fechaFavorito = nuevaFecha
                )
            )
        }
    }

    fun onEditarSnippet(snippet: SnippetUi, titulo: String, contenido: String) {
        viewModelScope.launch {
            repository.actualizarSnippet(snippet.toEntity().copy(titulo = titulo.trim(), contenido = contenido))
        }
    }

    fun onEliminarSnippet(snippet: SnippetUi) {
        viewModelScope.launch {
            repository.eliminarSnippet(snippet.toEntity())
        }
    }
}