package com.devvault.app.domain.usecase

import javax.inject.Inject

class ValidarSnippetUseCase @Inject constructor() {
    operator fun invoke(titulo: String, contenido: String): String? {
        if (titulo.isBlank()) return "El título no puede estar vacío"
        if (contenido.isBlank()) return "El contenido no puede estar vacío"
        if (titulo.length > 100) return "El título no puede superar 100 caracteres"
        return null // válido
    }
}
