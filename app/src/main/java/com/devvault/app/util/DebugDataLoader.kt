package com.devvault.app.util

import android.content.Context
import android.content.pm.ApplicationInfo
import com.devvault.app.data.local.entity.CategoriaEntity
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.data.repository.DevVaultRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DebugDataLoader @Inject constructor(
    private val repository: DevVaultRepository,
    @ApplicationContext private val context: Context
) {
    suspend fun loadIfEmpty() {
        // 1. Verificamos si la app está corriendo en modo Debug (Emulador/Desarrollo)
        val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (!isDebug) return // Si está en producción real, no hacemos nada

        // 2. Verificamos si ya hay datos (para no duplicar cada vez que abres la app)
        val snippetsExistentes = repository.obtenerSnippets().first()
        if (snippetsExistentes.isNotEmpty()) return

        // 3. Insertar Categorías
        val categorias = listOf(
            CategoriaEntity(id = 1L, nombre = "Docker", color = 0xFF2D9CF0),
            CategoriaEntity(id = 2L, nombre = "Kotlin", color = 0xFF7C4DFF),
            CategoriaEntity(id = 3L, nombre = "SQL", color = 0xFF3E7CA8)
        )

        // Nota: Asumo que los métodos se llaman así. Si arroja error en rojo, me avisas.
        categorias.forEach { repository.insertarCategoria(it) }

        // 4. Insertar Snippets vinculados a las categorías
        val snippets = listOf(
            SnippetEntity(
                id = 0L, // 0L para que Room autogenere el ID
                categoriaId = 1L,
                titulo = "Delete DB Container",
                contenido = "docker rm -f db_container\ndocker volume prune",
                lenguaje = "Bash",
                esFavorito = true,
                fechaCreacion = System.currentTimeMillis()
            ),
            SnippetEntity(
                id = 0L,
                categoriaId = 2L,
                titulo = "Room Query Flow",
                contenido = "@Query(\"SELECT * FROM snippets\")\nfun obtenerTodos(): Flow<List<SnippetEntity>>",
                lenguaje = "Kotlin",
                esFavorito = false,
                fechaCreacion = System.currentTimeMillis() - 100000
            ),
            SnippetEntity(
                id = 0L,
                categoriaId = 3L,
                titulo = "Drop Table",
                contenido = "DROP TABLE IF EXISTS usuarios;",
                lenguaje = "SQL",
                esFavorito = false,
                fechaCreacion = System.currentTimeMillis() - 200000
            )
        )

        snippets.forEach { repository.insertarSnippet(it) }
    }
}