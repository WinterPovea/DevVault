package com.devvault.app.ui.add_snippet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvault.app.data.local.entity.CategoriaEntity
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.data.repository.DevVaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSnippetViewModel @Inject constructor(
    private val repository: DevVaultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddSnippetUiState())
    val uiState: StateFlow<AddSnippetUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.obtenerCategorias().collect { categorias ->
                _uiState.update { current ->
                    current.copy(
                        categorias = categorias,
                        selectedCategoriaId = current.selectedCategoriaId ?: categorias.firstOrNull()?.id
                    )
                }
            }
        }
    }

    fun onNombreChange(nombre: String) {
        _uiState.update {
            it.copy(
                nombre = nombre,
                nombreError = if (nombre.isNotBlank()) null else it.nombreError
            )
        }
    }

    fun onContenidoChange(contenido: String) {
        _uiState.update {
            it.copy(
                contenido = contenido,
                contenidoError = if (contenido.isNotBlank()) null else it.contenidoError
            )
        }
    }

    fun onCategoriaSelected(categoriaId: Long) {
        _uiState.update {
            it.copy(
                selectedCategoriaId = categoriaId,
                expandedDropdown = false,
                categoriaError = null
            )
        }
    }

    fun toggleDropdown(expanded: Boolean) = _uiState.update { it.copy(expandedDropdown = expanded) }

    fun setShowAddCategoriaDialog(show: Boolean) {
        _uiState.update { it.copy(showAddCategoriaDialog = show) }
    }

    fun crearCategoria(nombre: String, color: Long) {
        if (nombre.isBlank()) return
        viewModelScope.launch {
            val nueva = CategoriaEntity(
                nombre = nombre.trim(),
                color = color
            )
            val idGenerado = repository.insertarCategoria(nueva)
            _uiState.update {
                it.copy(
                    showAddCategoriaDialog = false,
                    selectedCategoriaId = idGenerado,
                    categoriaError = null,
                    snackbarMessage = "Categoría \"${nombre.trim()}\" creada exitosamente"
                )
            }
        }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun saveSnippet() {
        val state = _uiState.value

        val nombreError = if (state.nombre.isBlank()) "Ingresa un nombre para el snippet" else null
        val contenidoError = if (state.contenido.isBlank()) "Ingresa el código del snippet" else null
        val categoriaError = if (state.selectedCategoriaId == null) "Se necesita ingresar una categoría" else null

        if (nombreError != null || contenidoError != null || categoriaError != null) {
            val primerMensaje = categoriaError ?: nombreError ?: contenidoError
            _uiState.update {
                it.copy(
                    nombreError = nombreError,
                    contenidoError = contenidoError,
                    categoriaError = categoriaError,
                    snackbarMessage = primerMensaje
                )
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

            _uiState.update { it.copy(isSaved = true) }
        }
    }
}