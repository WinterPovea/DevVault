package com.devvault.app.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.data.repository.DevVaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FavoritosUiState {
    data object Loading : FavoritosUiState
    data object Empty : FavoritosUiState
    data class Success(val favoritos: List<SnippetEntity>) : FavoritosUiState
}

@HiltViewModel
class FavoritosViewModel @Inject constructor(
    private val repository: DevVaultRepository
) : ViewModel() {

    val uiState: StateFlow<FavoritosUiState> = repository.obtenerSnippetsFavoritos()
        .map { favoritos ->
            if (favoritos.isEmpty()) {
                FavoritosUiState.Empty
            } else {
                FavoritosUiState.Success(favoritos)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoritosUiState.Loading
        )

    fun alternarFavorito(snippet: SnippetEntity) {
        viewModelScope.launch {
            repository.actualizarSnippet(snippet.copy(esFavorito = !snippet.esFavorito))
        }
    }
}