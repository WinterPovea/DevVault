package com.devvault.app.ui.favoritos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.ui.theme.CodeBackground
import com.devvault.app.ui.theme.CodeText
import com.devvault.app.ui.theme.DevVaultTheme
import com.devvault.app.ui.theme.StarFavorite

@Composable
fun FavoritoSnippetCard(
    snippet: SnippetEntity,
    onCopyClick: (String) -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = snippet.titulo,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        text = snippet.lenguaje ?: "",
                        fontSize = 14.sp,
                        color = colors.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Alternar favorito",
                        tint = if (snippet.esFavorito) StarFavorite else colors.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CodeBackground, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = snippet.contenido,
                    color = CodeText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { onCopyClick(snippet.contenido) },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                border = BorderStroke(1.dp, colors.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colors.primary
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = "Copiar", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritoSnippetCardPreview() {
    DevVaultTheme {
        FavoritoSnippetCard(
            snippet = SnippetEntity(
                id = 1,
                categoriaId = 1,
                titulo = "Crear ENV",
                contenido = "docker run --env-file .env app",
                lenguaje = "Crear ENV container",
                esFavorito = true,
                fechaCreacion = System.currentTimeMillis()
            ),
            onCopyClick = {},
            onFavoriteClick = {},
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        )
    }
}