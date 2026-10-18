package com.devvault.app.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ValidarSnippetUseCaseTest {

    private lateinit var useCase: ValidarSnippetUseCase

    @Before
    fun setUp() {
        useCase = ValidarSnippetUseCase()
    }

    @Test
    fun `titulo vacio retorna error correspondiente`() {
        val error = useCase(titulo = "", contenido = "val x = 10")
        assertEquals("El título no puede estar vacío", error)
    }

    @Test
    fun `contenido vacio retorna error correspondiente`() {
        val error = useCase(titulo = "Mi Snippet", contenido = "")
        assertEquals("El contenido no puede estar vacío", error)
    }

    @Test
    fun `titulo que supera 100 caracteres retorna error`() {
        val tituloLargo = "A".repeat(101)
        val error = useCase(titulo = tituloLargo, contenido = "val x = 10")
        assertEquals("El título no puede superar 100 caracteres", error)
    }

    @Test
    fun `snippet valido retorna null`() {
        val error = useCase(titulo = "Snippet válido", contenido = "println(\"Hola\")")
        assertNull(error)
    }
}
