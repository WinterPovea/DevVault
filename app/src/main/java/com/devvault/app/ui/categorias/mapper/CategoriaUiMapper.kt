package com.devvault.app.ui.categorias.mapper

import com.devvault.app.data.local.entity.CategoriaEntity
import com.devvault.app.ui.model.CategoriaUi

fun CategoriaEntity.toUi(conteoSnippets: Int = 0): CategoriaUi = CategoriaUi(
    id = id,
    nombre = nombre,
    colorLong = color,
    conteoSnippets = conteoSnippets
)

fun CategoriaUi.toEntity(): CategoriaEntity = CategoriaEntity(
    id = id,
    nombre = nombre,
    color = colorLong
)
