package com.devvault.app.presentation.categoria

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.devvault.app.data.local.entity.CategoriaEntity
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.data.repository.DevVaultRepository
import com.devvault.app.navigation.DevVaultRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CategoriaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DevVaultRepository
) : ViewModel() {

    private val categoriaId = savedStateHandle.toRoute<DevVaultRoute.CategoriaDetail>().categoriaId

    val uiState: StateFlow<CategoriaDetailUiState> =
        combine<List<CategoriaEntity>, List<SnippetEntity>, CategoriaDetailUiState>(
            repository.obtenerCategorias(),
            repository.obtenerSnippetsPorCategoria(categoriaId)
        ) { categorias, snippets ->
            val categoria = categorias.firstOrNull { it.id == categoriaId }
            if (categoria == null) {
                CategoriaDetailUiState.Error("Categoría no encontrada")
            } else {
                CategoriaDetailUiState.Success(categoria.nombre, snippets)
            }
        }
            .catch { emit(CategoriaDetailUiState.Error(it.message ?: "Error desconocido")) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoriaDetailUiState.Loading)

    fun onToggleFavorito(snippet: SnippetEntity) {
        viewModelScope.launch {
            repository.actualizarSnippet(snippet.copy(esFavorito = !snippet.esFavorito))
        }
    }
}