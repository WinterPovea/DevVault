package com.devvault.app.presentation.add_snippet

import com.devvault.app.data.local.entity.CategoriaEntity

data class AddSnippetUiState(
    val nombre: String = "",
    val contenido: String = "",
    val selectedCategoriaId: Long? = null,
    val categorias: List<CategoriaEntity> = emptyList(),
    val expandedDropdown: Boolean = false,
    val isSaved: Boolean = false
)