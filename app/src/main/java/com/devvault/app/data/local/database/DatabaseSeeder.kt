package com.devvault.app.data.local.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseSeeder {

    private data class CategoriaSeed(val id: Long, val nombre: String, val color: Long)

    private data class SnippetSeed(
        val categoriaId: Long,
        val titulo: String,
        val contenido: String,
        val lenguaje: String?,
        val esFavorito: Boolean
    )

    private val categorias = listOf(
        CategoriaSeed(1, "Docker", 0xFF2496ED),
        CategoriaSeed(2, "Git", 0xFFF1502F),
        CategoriaSeed(3, "SQL", 0xFF4479A1),
        CategoriaSeed(4, "Kotlin", 0xFF7F52FF),
        CategoriaSeed(5, "Go", 0xFF00ADD8),
        CategoriaSeed(6, "Kubernetes", 0xFF326CE5)
    )

    private val snippets = listOf(
        // Docker
        SnippetSeed(1, "Crear imagen", "docker build -t app .", "bash", true),
        SnippetSeed(1, "Levantar servicio", "docker-compose up -d", "bash", false),
        SnippetSeed(1, "Borrar contenedor", "docker rm -f db_service", "bash", true),
        // Git
        SnippetSeed(2, "Nueva rama", "git checkout -b feature/nombre", "bash", false),
        SnippetSeed(2, "Deshacer último commit", "git reset --soft HEAD~1", "bash", false),
        // SQL (multilínea, sirve para probar el botón Copiar)
        SnippetSeed(
            3, "Contar por categoría",
            """
            SELECT categoriaId, COUNT(*) AS total
            FROM snippets
            GROUP BY categoriaId
            ORDER BY total DESC;
            """.trimIndent(),
            "sql", true
        ),
        // Kotlin (multilínea con indentación)
        SnippetSeed(
            4, "Data class",
            """
            data class Usuario(
                val id: Long,
                val nombre: String
            )
            """.trimIndent(),
            "kotlin", false
        ),
        // Go
        SnippetSeed(
            5, "Handler HTTP",
            """
            func hola(w http.ResponseWriter, r *http.Request) {
                fmt.Fprintln(w, "hola")
            }
            """.trimIndent(),
            "go", false
        ),
        // Kubernetes
        SnippetSeed(6, "Ver pods", "kubectl get pods -A", "bash", true)
    )

    fun poblar(db: SupportSQLiteDatabase) {
        val ahora = System.currentTimeMillis()

        categorias.forEach { c ->
            val valores = ContentValues().apply {
                put("id", c.id)
                put("nombre", c.nombre)
                put("color", c.color)
            }
            db.insert("categorias", SQLiteDatabase.CONFLICT_REPLACE, valores)
        }

        snippets.forEachIndexed { indice, s ->
            val valores = ContentValues().apply {
                put("categoriaId", s.categoriaId)
                put("titulo", s.titulo)
                put("contenido", s.contenido)
                put("lenguaje", s.lenguaje)
                put("esFavorito", if (s.esFavorito) 1 else 0)
                put("fechaCreacion", ahora - indice * 60_000L)
            }
            db.insert("snippets", SQLiteDatabase.CONFLICT_REPLACE, valores)
        }
    }
}