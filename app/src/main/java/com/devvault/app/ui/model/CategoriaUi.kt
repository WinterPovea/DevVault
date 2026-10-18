package com.devvault.app.ui.model

data class CategoriaUi(
    val id: Long = 0,
    val nombre: String = "",
    val colorLong: Long = 0xFF7F52FF,
    val conteoSnippets: Int = 0
) {
    val color: Long get() = colorLong
    val totalSnippets: Int get() = conteoSnippets
}
