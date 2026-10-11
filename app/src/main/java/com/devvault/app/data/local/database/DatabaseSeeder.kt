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
        CategoriaSeed(1, "PuTTY / Redes", 0xFF008080),
        CategoriaSeed(2, "Git", 0xFFF1502F),
        CategoriaSeed(3, "SQL", 0xFF4479A1),
        CategoriaSeed(4, "Linux CLI", 0xFFFCC624),
        CategoriaSeed(5, "Docker", 0xFF2496ED),
        CategoriaSeed(6, "Kotlin", 0xFF7F52FF),
        CategoriaSeed(7, "Go", 0xFF00ADD8),
        CategoriaSeed(8, "Kubernetes", 0xFF326CE5)
    )

    private val snippets = listOf(
        // ----------------------------------------------------------------
        // 1. PuTTY / Redes (Comandos CLI para Switches, Routers y Terminal)
        // ----------------------------------------------------------------
        SnippetSeed(
            1, "Configurar VLAN y asignación de puertos (Cisco CLI)",
            """
            enable
            configure terminal
            vlan 10
             name Red_Infraestructura
            exit
            interface range fastEthernet 0/1 - 12
             switchport mode access
             switchport access vlan 10
            exit
            """.trimIndent(),
            "bash", true
        ),
        SnippetSeed(
            1, "Guardar configuración activa a memoria no volátil",
            "copy running-config startup-config",
            "bash", false
        ),
        SnippetSeed(
            1, "Verificar estado de interfaces de red",
            "show ip interface brief",
            "bash", true
        ),
        SnippetSeed(
            1, "Sesión SSH directa desde consola",
            "ssh admin@192.168.1.1 -p 22",
            "bash", false
        ),

        // ----------------------------------------------------------------
        // 2. Git
        // ----------------------------------------------------------------
        SnippetSeed(2, "Nueva rama desde develop", "git checkout -b feature/database-seed", "bash", false),
        SnippetSeed(2, "Deshacer último commit manteniendo cambios locales", "git reset --soft HEAD~1", "bash", true),
        SnippetSeed(2, "Limpiar referencias locales a ramas borradas en remoto", "git fetch -p", "bash", false),
        SnippetSeed(2, "Descartar todos los cambios no guardados en el directorio", "git restore .", "bash", false),

        // ----------------------------------------------------------------
        // 3. SQL
        // ----------------------------------------------------------------
        SnippetSeed(
            3, "Conteo de snippets por categoría con JOIN",
            """
            SELECT c.nombre AS categoria, COUNT(s.id) AS total_snippets
            FROM categorias c
            LEFT JOIN snippets s ON c.id = s.categoriaId
            GROUP BY c.id, c.nombre
            ORDER BY total_snippets DESC;
            """.trimIndent(),
            "sql", true
        ),
        SnippetSeed(
            3, "Búsqueda insensible a mayúsculas con LIKE",
            """
            SELECT * FROM snippets
            WHERE LOWER(titulo) LIKE '%docker%' OR LOWER(contenido) LIKE '%docker%'
            ORDER BY fechaCreacion DESC;
            """.trimIndent(),
            "sql", false
        ),

        // ----------------------------------------------------------------
        // 4. Linux CLI
        // ----------------------------------------------------------------
        SnippetSeed(4, "Verificar puertos de red y procesos escuchando", "sudo ss -tulpn | grep LISTEN", "bash", true),
        SnippetSeed(4, "Buscar texto recursivamente en archivos de log", "grep -rnw '/var/log/' -e \"ERROR\"", "bash", false),
        SnippetSeed(4, "Ver uso de memoria y CPU en tiempo real", "htop", "bash", false),

        // ----------------------------------------------------------------
        // 5. Docker
        // ----------------------------------------------------------------
        SnippetSeed(5, "Construir y etiquetar imagen", "docker build -t app-devvault:latest .", "bash", true),
        SnippetSeed(5, "Levantar servicios en segundo plano", "docker compose up -d", "bash", false),
        SnippetSeed(5, "Limpiar recursos huérfanos y volúmenes sin uso", "docker system prune -a --volumes", "bash", false),

        // ----------------------------------------------------------------
        // 6. Kotlin
        // ----------------------------------------------------------------
        SnippetSeed(
            6, "Estructura de Entidad Room con Clave Primaria",
            """
            @Entity(tableName = "snippets")
            data class SnippetEntity(
                @PrimaryKey(autoGenerate = true) val id: Long = 0,
                val categoriaId: Long,
                val titulo: String,
                val contenido: String,
                val lenguaje: String?,
                val esFavorito: Boolean,
                val fechaCreacion: Long
            )
            """.trimIndent(),
            "kotlin", true
        ),

        // ----------------------------------------------------------------
        // 7. Go
        // ----------------------------------------------------------------
        SnippetSeed(
            7, "Servidor de estado/Health Check en Go",
            """
            package main

            import (
                "fmt"
                "net/http"
            )

            func main() {
                http.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
                    fmt.Fprintln(w, "OK")
                })
                http.ListenAndServe(":8080", nil)
            }
            """.trimIndent(),
            "go", false
        ),

        // ----------------------------------------------------------------
        // 8. Kubernetes
        // ----------------------------------------------------------------
        SnippetSeed(8, "Ver logs de un Pod en tiempo real", "kubectl logs -f nombre-pod -n namespace-prod", "bash", true)
    )

    fun poblar(db: SupportSQLiteDatabase) {
        val ahora = System.currentTimeMillis()

        db.beginTransaction()
        try {
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

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}