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
        viewModelScope.launch {
            // Si "getCategorias()" está en rojo, cámbialo por la sugerencia del IDE
            repository.getCategorias().collect { categorias ->
                _uiState.update { it.copy(categorias = categorias) }
            }
        }
    }

    fun onNombreChange(nombre: String) = _uiState.update { it.copy(nombre = nombre) }

    fun onContenidoChange(contenido: String) = _uiState.update { it.copy(contenido = contenido) }

    fun onCategoriaSelected(categoriaId: Long) = _uiState.update { it.copy(selectedCategoriaId = categoriaId, expandedDropdown = false) }

    fun toggleDropdown(expanded: Boolean) = _uiState.update { it.copy(expandedDropdown = expanded) }

    fun saveSnippet() {
        val state = _uiState.value
        if (state.nombre.isNotBlank() && state.contenido.isNotBlank() && state.selectedCategoriaId != null) {
            viewModelScope.launch {
                // Parámetros actualizados según la entidad de tu compañero
                val newSnippet = SnippetEntity(
                    titulo = state.nombre,
                    contenido = state.contenido,
                    categoriaId = state.selectedCategoriaId,
                    lenguaje = "Bash", // Valor por defecto
                    fechaCreacion = System.currentTimeMillis()
                )
                // Si "addSnippet" está en rojo, cámbialo por la sugerencia del IDE
                repository.addSnippet(newSnippet)
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }
}