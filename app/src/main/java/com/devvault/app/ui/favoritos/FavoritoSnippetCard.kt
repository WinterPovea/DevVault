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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devvault.app.data.local.entity.SnippetEntity

@Composable
fun FavoritoSnippetCard(
    snippet: SnippetEntity,
    onCopyClick: (String) -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBackgroundColor = Color(0xFF1E2336)
    val codeBackgroundColor = Color(0xFF0F141E)
    val cyanAccent = Color(0xFF00E5FF)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFA0AABF)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
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
                        color = textPrimary
                    )
                    Text(
                        text = snippet.lenguaje ?: "",
                        fontSize = 14.sp,
                        color = textSecondary
                    )
                }

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Alternar favorito",
                        tint = if (snippet.esFavorito) Color(0xFFFFC107) else textSecondary.copy(alpha = 0.3f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(codeBackgroundColor, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = snippet.contenido,
                    color = cyanAccent,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { onCopyClick(snippet.contenido) },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                border = BorderStroke(1.dp, cyanAccent),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = cyanAccent
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = "Copiar", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B101A)
@Composable
private fun FavoritoSnippetCardPreview() {
    MaterialTheme {
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
            modifier = Modifier.padding(16.dp)
        )
    }
}