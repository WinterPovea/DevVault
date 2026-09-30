package com.devvault.app.navigation

import kotlinx.serialization.Serializable

sealed interface DevVaultRoute {

    @Serializable
    data object Home : DevVaultRoute

    @Serializable
    data object Categorias : DevVaultRoute

    @Serializable
    data object Agregar : DevVaultRoute

    @Serializable
    data object Favoritos : DevVaultRoute

    @Serializable
    data class CategoriaDetail(val categoriaId: Long) : DevVaultRoute

    @Serializable
    data class SnippetDetail(val snippetId: Long) : DevVaultRoute
}