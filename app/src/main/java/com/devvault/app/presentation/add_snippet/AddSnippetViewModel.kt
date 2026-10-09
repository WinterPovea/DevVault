package com.devvault.app.presentation.add_snippet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        // Carga las categorías creadas para el menú desplegable
        viewModelScope.launch {
            repository.getAllCategorias().collect { categorias ->
                _uiState.update { it.copy(categorias = categorias) }
            }
        }
    }

    fun onNombreChange(nombre: String) = _uiState.update { it.copy(nombre = nombre) }

    fun onContenidoChange(contenido: String) = _uiState.update { it.copy(contenido = contenido) }

    fun onCategoriaSelected(categoriaId: Int) = _uiState.update { it.copy(selectedCategoriaId = categoriaId, expandedDropdown = false) }

    fun toggleDropdown(expanded: Boolean) = _uiState.update { it.copy(expandedDropdown = expanded) }

    fun saveSnippet() {
        val state = _uiState.value
        // Verifica que los campos no estén vacíos
        if (state.nombre.isNotBlank() && state.contenido.isNotBlank() && state.selectedCategoriaId != null) {
            viewModelScope.launch {
                val newSnippet = SnippetEntity(
                    nombre = state.nombre,
                    codigo = state.contenido,
                    categoriaId = state.selectedCategoriaId,
                    isFavorite = false
                )
                repository.insertSnippet(newSnippet)
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }
}