package com.devvault.app.ui.categorias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvault.app.data.repository.DevVaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface CategoriasUiEvent {
    data class ShowMessage(val message: String) : CategoriasUiEvent
}

@HiltViewModel
class CategoriasViewModel @Inject constructor(
    private val repository: DevVaultRepository
) : ViewModel() {

    private val busqueda = MutableStateFlow("")
    private val seleccionadaId = MutableStateFlow<Long?>(null)

    private val _events = Channel<CategoriasUiEvent>(Channel.BUFFERED)
    val events: Flow<CategoriasUiEvent> = _events.receiveAsFlow()

    val uiState: StateFlow<CategoriasUiState> =
        combine(
            repository.obtenerCategorias(),
            repository.obtenerSnippets(),
            busqueda,
            seleccionadaId
        ) { categorias, snippets, texto, seleccionada ->
            val conteos = snippets.groupingBy { it.categoriaId }.eachCount()
            val filtradas = categorias
                .filter { it.nombre.contains(texto.trim(), ignoreCase = true) }
                .map { CategoriaConConteo(it.id, it.nombre, it.color, conteos[it.id] ?: 0) }
            CategoriasUiState.Success(
                categorias = filtradas,
                busqueda = texto,
                seleccionadaId = seleccionada
            ) as CategoriasUiState
        }
            .catch { emit(CategoriasUiState.Error(it.message ?: "Error desconocido")) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoriasUiState.Loading)

    fun onBusquedaChange(texto: String) {
        busqueda.value = texto
    }

    fun onToggleSeleccion(id: Long) {
        seleccionadaId.update { if (it == id) null else id }
    }

    fun onCancelarSeleccion() {
        seleccionadaId.value = null
    }

    fun onEditarCategoria(id: Long, nuevoNombre: String) {
        val nombreLimpio = nuevoNombre.trim()
        if (nombreLimpio.isBlank()) return
        viewModelScope.launch {
            val categoriaActual = repository.obtenerCategoriaPorId(id)
            if (categoriaActual != null) {
                repository.actualizarCategoria(categoriaActual.copy(nombre = nombreLimpio))
                seleccionadaId.value = null
                _events.send(CategoriasUiEvent.ShowMessage("Categoría actualizada"))
            }
        }
    }

    fun onEliminarCategoria(id: Long) {
        viewModelScope.launch {
            val categoria = repository.obtenerCategoriaPorId(id)
            if (categoria != null) {
                repository.eliminarCategoria(categoria)
                if (seleccionadaId.value == id) {
                    seleccionadaId.value = null
                }
                _events.send(CategoriasUiEvent.ShowMessage("Categoría eliminada"))
            }
        }
    }
}