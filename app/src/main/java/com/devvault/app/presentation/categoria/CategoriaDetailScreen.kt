package com.devvault.app.presentation.categoria

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.ui.theme.DevVaultTheme
import com.devvault.app.util.copiarAlPortapapeles

private val ColorEstrella = Color(0xFFF5C94B)

@Composable
fun CategoriaDetailRoute(
    onBack: () -> Unit,
    onSnippetClick: (Long) -> Unit,
    viewModel: CategoriaDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    CategoriaDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onSnippetClick = onSnippetClick,
        onToggleFavorito = viewModel::onToggleFavorito,
        onCopiar = { texto ->
            copiarAlPortapapeles(context, texto)
            Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaDetailScreen(
    uiState: CategoriaDetailUiState,
    onBack: () -> Unit,
    onSnippetClick: (Long) -> Unit,
    onToggleFavorito: (SnippetEntity) -> Unit,
    onCopiar: (String) -> Unit
) {
    val titulo = (uiState as? CategoriaDetailUiState.Success)
        ?.let { "Categoría ${it.nombreCategoria}" } ?: "Categoría"

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(titulo, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
        when (uiState) {
            is CategoriaDetailUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }

            is CategoriaDetailUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(uiState.mensaje, color = MaterialTheme.colorScheme.error)
            }

            is CategoriaDetailUiState.Success ->
                if (uiState.snippets.isEmpty()) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(
                            "No hay snippets en esta categoría",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.snippets, key = { it.id }) { snippet ->
                            SnippetCard(
                                snippet = snippet,
                                onClick = { onSnippetClick(snippet.id) },
                                onToggleFavorito = { onToggleFavorito(snippet) },
                                onCopiar = { onCopiar(snippet.contenido) }
                            )
                        }
                    }
                }
        }
    }
}

@Composable
private fun SnippetCard(
    snippet: SnippetEntity,
    onClick: () -> Unit,
    onToggleFavorito: () -> Unit,
    onCopiar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
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
                color = MaterialTheme.colorScheme.surfaceContainerLowest
            ) {
                Text(
                    snippet.contenido,
                    modifier = Modifier.padding(8.dp),
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            OutlinedButton(
                onClick = onCopiar,
                modifier = Modifier.padding(top = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("⧉ Copiar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoriaDetailScreenPreview() {
    DevVaultTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CategoriaDetailScreen(
                uiState = CategoriaDetailUiState.Success(
                    nombreCategoria = "Docker",
                    snippets = listOf(
                        SnippetEntity(1, 1, "Crear imagen", "docker build -t app .", null, true, 0L),
                        SnippetEntity(2, 1, "Levantar servicio", "docker-compose up -d", null, false, 0L)
                    )
                ),
                onBack = {},
                onSnippetClick = {},
                onToggleFavorito = {},
                onCopiar = {}
            )
        }
    }
}