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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devvault.app.ui.theme.DevVaultTheme

@Composable
fun CategoriasRoute(
    onCategoriaClick: (Long) -> Unit,
    viewModel: CategoriasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CategoriasScreen(
        uiState = uiState,
        onBusquedaChange = viewModel::onBusquedaChange,
        onCategoriaClick = onCategoriaClick
    )
}

@Composable
fun CategoriasScreen(
    uiState: CategoriasUiState,
    onBusquedaChange: (String) -> Unit,
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
            OutlinedTextField(
                value = uiState.busqueda,
                onValueChange = onBusquedaChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar categoría") },
                trailingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            if (uiState.categorias.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("No hay categorías", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(categoria.color)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    categoria.nombre.take(1).uppercase(),
                    color = Color(0xFF05070D),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                categoria.nombre,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                "${categoria.totalSnippets} snippets",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoriasScreenPreview() {
    DevVaultTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CategoriasScreen(
                uiState = CategoriasUiState.Success(
                    categorias = listOf(
                        CategoriaConConteo(1, "Docker", 0xFF2496ED, 20),
                        CategoriaConConteo(2, "Git", 0xFFF1502F, 10),
                        CategoriaConConteo(3, "SQL", 0xFF4479A1, 15),
                        CategoriaConConteo(4, "Kotlin", 0xFF7F52FF, 7)
                    ),
                    busqueda = ""
                ),
                onBusquedaChange = {},
                onCategoriaClick = {}
            )
        }
    }
}