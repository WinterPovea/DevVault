package com.devvault.app.ui.add_snippet.mapper

import com.devvault.app.data.local.entity.CategoriaEntity
import com.devvault.app.ui.model.CategoriaUi

fun CategoriaEntity.toUi(): CategoriaUi = CategoriaUi(
    id = id,
    nombre = nombre,
    colorLong = color,
    conteoSnippets = 0
)

fun List<CategoriaEntity>.toUiList(): List<CategoriaUi> = map { it.toUi() }
