package com.devvault.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.ui.theme.DevVaultTheme
import com.devvault.app.ui.add_snippet.CodeSyntaxVisualTransformation

private val ColorEstrella = Color(0xFFF5C94B)
private val CodeBackground = Color(0xFF05070B)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoriaCard(
    nombre: String,
    totalSnippets: Int,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    estaSeleccionada: Boolean = false,
    haySeleccionActiva: Boolean = false,
    onToggleSeleccion: (() -> Unit)? = null
) {
    val borde = if (estaSeleccionada) {
        BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, color)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .combinedClickable(
                onClick = {
                    if (haySeleccionActiva && onToggleSeleccion != null) {
                        onToggleSeleccion()
                    } else {
                        onClick()
                    }
                },
                onLongClick = onToggleSeleccion
            ),
        shape = RoundedCornerShape(14.dp),
        border = borde,
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionada) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = nombre.take(1).uppercase(),
                        color = Color(0xFF05070D),
                        fontWeight = FontWeight.Bold
                    )
                }

                if (onToggleSeleccion != null) {
                    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                        Checkbox(
                            checked = estaSeleccionada,
                            onCheckedChange = { onToggleSeleccion() },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Text(
                text = nombre,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 12.dp)
            )

            Text(
                text = if (totalSnippets == 1) "1 snippet" else "$totalSnippets snippets",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SnippetCard(
    snippet: SnippetEntity,
    onToggleFavorito: () -> Unit,
    onCopiar: () -> Unit,
    modifier: Modifier = Modifier,
    onEditar: (() -> Unit)? = null,
    onEliminar: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    snippet.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onToggleFavorito) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Favorito",
                        tint = if (snippet.esFavorito) ColorEstrella else MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(8.dp),
                color = CodeBackground
            ) {
                val transformation = CodeSyntaxVisualTransformation(snippet.lenguaje ?: "")
                val textoResaltado = transformation.filter(AnnotatedString(snippet.contenido)).text

                Text(
                    text = textoResaltado,
                    modifier = Modifier.padding(8.dp),
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onCopiar, shape = RoundedCornerShape(8.dp)) {
                    Text("⧉ Copiar")
                }
                onEditar?.let { TextButton(onClick = it) { Text("Editar") } }
                onEliminar?.let {
                    TextButton(onClick = it) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TarjetasPreview() {
    DevVaultTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CategoriaCard("Docker", 20, Color(0xFF2496ED), onClick = {})
            SnippetCard(
                snippet = SnippetEntity(1, 1, "Crear imagen", "docker build -t app .", null, true, 0L),
                onToggleFavorito = {},
                onCopiar = {},
                onEditar = {},
                onEliminar = {}
            )
        }
    }
}