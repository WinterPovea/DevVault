package com.devvault.app.presentation.categorias

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CategoriasRoute(
    onCategoriaClick: (Long) -> Unit,
    viewModel: CategoriasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CategoriasScreen(uiState = uiState, onCategoriaClick = onCategoriaClick)
}

@Composable
fun CategoriasScreen(
    uiState: CategoriasUiState,
    onCategoriaClick: (Long) -> Unit
) {
    when (uiState) {
        is CategoriasUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }

        is CategoriasUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(uiState.mensaje, color = MaterialTheme.colorScheme.error)
        }

        is CategoriasUiState.Success -> Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Categorías", style = MaterialTheme.typography.headlineMedium)
            if (uiState.categorias.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Aún no hay categorías")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.categorias, key = { it.id }) { categoria ->
                        CategoriaCard(categoria, onClick = { onCategoriaClick(categoria.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoriaCard(categoria: CategoriaConConteo, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp)) {
            Box(
                Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(categoria.color))
            )
            Text(
                categoria.nombre,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                "${categoria.totalSnippets} snippets",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoriasScreenPreview() {
    MaterialTheme {
        CategoriasScreen(
            uiState = CategoriasUiState.Success(
                listOf(
                    CategoriaConConteo(1, "Docker", 0xFF2496ED, 5),
                    CategoriaConConteo(2, "Git", 0xFFF05032, 3),
                    CategoriaConConteo(3, "SQL", 0xFF4479A1, 8)
                )
            ),
            onCategoriaClick = {}
        )
    }
}