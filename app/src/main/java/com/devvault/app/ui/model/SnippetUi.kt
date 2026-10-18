package com.devvault.app.ui.model

data class SnippetUi(
    val id: Long = 0,
    val categoriaId: Long = 0,
    val titulo: String = "",
    val contenido: String = "",
    val lenguaje: String? = null,
    val categoriaNombre: String = "",
    val categoriaColor: Long = 0xFF7F52FF,
    val esFavorito: Boolean = false,
    val fechaCreacion: Long = 0L,
    val fechaFavorito: Long? = null
)
