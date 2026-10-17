package com.devvault.app.ui.add_snippet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvault.app.data.local.entity.CategoriaEntity
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.data.repository.DevVaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AddSnippetUiEvent {
    data class ShowSnackbar(val message: String) : AddSnippetUiEvent
    data object NavigateBack : AddSnippetUiEvent
}

@HiltViewModel
class AddSnippetViewModel @Inject constructor(
    private val repository: DevVaultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddSnippetUiState>(AddSnippetUiState.Loading)
    val uiState: StateFlow<AddSnippetUiState> = _uiState.asStateFlow()

    private val _events = Channel<AddSnippetUiEvent>(Channel.BUFFERED)
    val events: Flow<AddSnippetUiEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.obtenerCategorias().collect { categorias ->
                _uiState.update { current ->
                    val existing = (current as? AddSnippetUiState.Success) ?: AddSnippetUiState.Success()
                    existing.copy(
                        categorias = categorias,
                        selectedCategoriaId = existing.selectedCategoriaId ?: categorias.firstOrNull()?.id
                    )
                }
            }
        }
    }

    private fun updateSuccess(transform: (AddSnippetUiState.Success) -> AddSnippetUiState.Success) {
        _uiState.update { current ->
            if (current is AddSnippetUiState.Success) transform(current) else current
        }
    }

    fun onNombreChange(nombre: String) {
        updateSuccess {
            it.copy(
                nombre = nombre,
                nombreError = if (nombre.isNotBlank()) null else it.nombreError
            )
        }
    }

    fun onContenidoChange(contenido: String) {
        updateSuccess {
            it.copy(
                contenido = contenido,
                contenidoError = if (contenido.isNotBlank()) null else it.contenidoError
            )
        }
    }

    fun onCategoriaSelected(categoriaId: Long) {
        updateSuccess {
            it.copy(
                selectedCategoriaId = categoriaId,
                expandedDropdown = false,
                categoriaError = null
            )
        }
    }

    fun toggleDropdown(expanded: Boolean) = updateSuccess { it.copy(expandedDropdown = expanded) }

    fun setShowAddCategoriaDialog(show: Boolean) {
        updateSuccess { it.copy(showAddCategoriaDialog = show) }
    }

    fun crearCategoria(nombre: String, color: Long) {
        if (nombre.isBlank()) return
        viewModelScope.launch {
            val nueva = CategoriaEntity(
                nombre = nombre.trim(),
                color = color
            )
            val idGenerado = repository.insertarCategoria(nueva)
            updateSuccess {
                it.copy(
                    showAddCategoriaDialog = false,
                    selectedCategoriaId = idGenerado,
                    categoriaError = null
                )
            }
            _events.send(AddSnippetUiEvent.ShowSnackbar("Categoría \"${nombre.trim()}\" creada exitosamente"))
        }
    }

    fun clearSnackbarMessage() {
        updateSuccess { it.copy(snackbarMessage = null) }
    }

    fun saveSnippet() {
        val state = _uiState.value as? AddSnippetUiState.Success ?: return

        val nombreError = if (state.nombre.isBlank()) "Ingresa un nombre para el snippet" else null
        val contenidoError = if (state.contenido.isBlank()) "Ingresa el código del snippet" else null
        val categoriaError = if (state.selectedCategoriaId == null) "Se necesita ingresar una categoría" else null

        if (nombreError != null || contenidoError != null || categoriaError != null) {
            val primerMensaje = categoriaError ?: nombreError ?: contenidoError
            updateSuccess {
                it.copy(
                    nombreError = nombreError,
                    contenidoError = contenidoError,
                    categoriaError = categoriaError
                )
            }
            primerMensaje?.let { msg ->
                viewModelScope.launch {
                    _events.send(AddSnippetUiEvent.ShowSnackbar(msg))
                }
            }
            return
        }

        val categoriaSeleccionada = state.categorias.find { it.id == state.selectedCategoriaId }
        val lenguaje = categoriaSeleccionada?.nombre ?: "Snippet"

        viewModelScope.launch {
            // Se respeta la arquitectura completa del contenido (espacios, saltos de línea \n, tabulaciones)
            val newSnippet = SnippetEntity(
                titulo = state.nombre.trim(),
                contenido = state.contenido,
                categoriaId = state.selectedCategoriaId!!,
                lenguaje = lenguaje,
                fechaCreacion = System.currentTimeMillis()
            )
            repository.insertarSnippet(newSnippet)

            updateSuccess { it.copy(isSaved = true) }
            _events.send(AddSnippetUiEvent.NavigateBack)
        }
    }
}