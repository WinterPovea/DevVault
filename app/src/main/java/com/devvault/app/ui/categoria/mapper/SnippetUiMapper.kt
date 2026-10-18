package com.devvault.app.ui.categoria.mapper

import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.ui.model.SnippetUi

fun SnippetEntity.toUi(
    categoriaNombre: String = "",
    categoriaColor: Long = 0xFF7F52FF
): SnippetUi = SnippetUi(
    id = id,
    categoriaId = categoriaId,
    titulo = titulo,
    contenido = contenido,
    lenguaje = lenguaje,
    categoriaNombre = categoriaNombre.ifBlank { lenguaje ?: "Snippet" },
    categoriaColor = categoriaColor,
    esFavorito = esFavorito,
    fechaCreacion = fechaCreacion,
    fechaFavorito = fechaFavorito
)

fun SnippetUi.toEntity(): SnippetEntity = SnippetEntity(
    id = id,
    categoriaId = categoriaId,
    titulo = titulo,
    contenido = contenido,
    lenguaje = lenguaje ?: categoriaNombre,
    esFavorito = esFavorito,
    fechaCreacion = fechaCreacion,
    fechaFavorito = fechaFavorito
)

fun List<SnippetEntity>.toUiList(categoriaNombre: String = "", categoriaColor: Long = 0xFF7F52FF): List<SnippetUi> =
    map { it.toUi(categoriaNombre, categoriaColor) }
