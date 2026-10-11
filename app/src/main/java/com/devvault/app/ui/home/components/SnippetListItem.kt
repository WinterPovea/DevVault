package com.devvault.app.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.ui.theme.DevVaultTheme

@Composable
fun SnippetListItem(
    snippet: SnippetEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() } // Hace que toda la fila sea clickeable
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Padding vertical para darle espacio al texto, horizontal más pequeño
                .padding(vertical = 16.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = snippet.titulo,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1, // Si el título es muy largo, no rompe el diseño
                overflow = TextOverflow.Ellipsis, // Agrega "..." si es muy largo
                modifier = Modifier.weight(1f)
            )
        }

        // El divisor fino que pide el boceto
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 1.dp
        )
    }
}

// Preview para ver el diseño con un dato de prueba
@Preview(showBackground = true, backgroundColor = 0xFF141A2B)
@Composable
private fun SnippetListItemPreview() {
    DevVaultTheme {
        val dummySnippet = SnippetEntity(
            id = 1L,
            categoriaId = 1L,
            titulo = "Delete DB Container",
            contenido = "docker rm -f db_container",
            lenguaje = "Bash",
            esFavorito = false,
            fechaCreacion = System.currentTimeMillis()
        )

        SnippetListItem(
            snippet = dummySnippet,
            onClick = {}
        )
    }
}