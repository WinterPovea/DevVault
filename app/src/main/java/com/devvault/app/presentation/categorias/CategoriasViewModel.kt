package com.devvault.app.presentation.categorias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvault.app.data.local.entity.CategoriaEntity
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.data.repository.DevVaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class CategoriasViewModel @Inject constructor(
    repository: DevVaultRepository
) : ViewModel() {

    private val busqueda = MutableStateFlow("")

    val uiState: StateFlow<CategoriasUiState> =
        combine<List<CategoriaEntity>, List<SnippetEntity>, String, CategoriasUiState>(
            repository.obtenerCategorias(),
            repository.obtenerSnippets(),
            busqueda
        ) { categorias, snippets, texto ->
            val conteos = snippets.groupingBy { it.categoriaId }.eachCount()
            val filtradas = categorias
                .filter { it.nombre.contains(texto.trim(), ignoreCase = true) }
                .map { CategoriaConConteo(it.id, it.nombre, it.color, conteos[it.id] ?: 0) }
            CategoriasUiState.Success(filtradas, texto)
        }
            .catch { emit(CategoriasUiState.Error(it.message ?: "Error desconocido")) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoriasUiState.Loading)

    fun onBusquedaChange(texto: String) {
        busqueda.value = texto
    }
}