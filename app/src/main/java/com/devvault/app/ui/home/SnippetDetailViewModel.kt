package com.devvault.app.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.data.repository.DevVaultRepository
import com.devvault.app.navigation.DevVaultRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SnippetDetailUiState(
    val snippet: SnippetEntity? = null,
    val categoriaNombre: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class SnippetDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DevVaultRepository
) : ViewModel() {

    private val ruta = savedStateHandle.toRoute<DevVaultRoute.SnippetDetail>()

    private val _uiState = MutableStateFlow(SnippetDetailUiState())
    val uiState: StateFlow<SnippetDetailUiState> = _uiState.asStateFlow()

    init {
        cargarSnippet(ruta.snippetId)
    }

    private fun cargarSnippet(id: Long) {
        viewModelScope.launch {
            val snippet = repository.obtenerSnippetPorId(id)
            if (snippet != null) {
                // Obtenemos la categoría para pasar su nombre al resaltador de sintaxis
                val categoria = repository.obtenerCategoriaPorId(snippet.categoriaId)
                _uiState.value = SnippetDetailUiState(
                    snippet = snippet,
                    categoriaNombre = categoria?.nombre ?: snippet.lenguaje ?: "",
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}