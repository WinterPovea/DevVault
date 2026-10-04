package com.devvault.app.presentation.categorias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvault.app.data.local.entity.CategoriaEntity
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.data.repository.DevVaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class CategoriasViewModel @Inject constructor(
    repository: DevVaultRepository
) : ViewModel() {

    val uiState: StateFlow<CategoriasUiState> =
        combine<List<CategoriaEntity>, List<SnippetEntity>, CategoriasUiState>(
            repository.obtenerCategorias(),
            repository.obtenerSnippets()
        ) { categorias, snippets ->
            val conteos = snippets.groupingBy { it.categoriaId }.eachCount()
            CategoriasUiState.Success(
                categorias.map {
                    CategoriaConConteo(it.id, it.nombre, it.color, conteos[it.id] ?: 0)
                }
            )
        }
            .catch { emit(CategoriasUiState.Error(it.message ?: "Error desconocido")) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoriasUiState.Loading)
}