package com.devvault.app.presentation.categoria

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devvault.app.data.local.entity.SnippetEntity

@Composable
fun CategoriaDetailRoute(
    onBack: () -> Unit,
    onSnippetClick: (Long) -> Unit,
    viewModel: CategoriaDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CategoriaDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onSnippetClick = onSnippetClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaDetailScreen(
    uiState: CategoriaDetailUiState,
    onBack: () -> Unit,
    onSnippetClick: (Long) -> Unit
) {
    val titulo = (uiState as? CategoriaDetailUiState.Success)?.nombreCategoria ?: "Categoría"

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(titulo) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
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
                        Text("No hay snippets en esta categoría")
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.snippets, key = { it.id }) { snippet ->
                            SnippetCard(snippet, onClick = { onSnippetClick(snippet.id) })
                        }
                    }
                }
        }
    }
}

@Composable
private fun SnippetCard(snippet: SnippetEntity, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp)) {
            Text(snippet.titulo, style = MaterialTheme.typography.titleMedium)
            snippet.lenguaje?.let {
                Text(it, style = MaterialTheme.typography.labelSmall)
            }
            Text(
                snippet.contenido,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoriaDetailScreenPreview() {
    MaterialTheme {
        CategoriaDetailScreen(
            uiState = CategoriaDetailUiState.Success(
                nombreCategoria = "Docker",
                snippets = listOf(
                    SnippetEntity(1, 1, "Listar contenedores", "docker ps -a", "bash", false, 0L),
                    SnippetEntity(2, 1, "Levantar compose", "docker compose up -d", "bash", true, 0L)
                )
            ),
            onBack = {},
            onSnippetClick = {}
        )
    }
}