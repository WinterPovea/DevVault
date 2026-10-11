package com.devvault.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.ui.home.components.HomeHeader
import com.devvault.app.ui.home.components.SearchBar
import com.devvault.app.ui.home.components.SnippetListItem
import com.devvault.app.ui.theme.DevVaultTheme
import com.devvault.app.ui.theme.TextSecondary

@Composable
fun HomeRoute(
    onSnippetClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onEvent = { event ->
            // Interceptamos el click de navegación para rutearlo hacia el NavHost,
            // los demás eventos (escribir, borrar) van directos al ViewModel.
            if (event is HomeEvent.OnSnippetClick) {
                onSnippetClick(event.snippetId)
            } else {
                viewModel.onEvent(event)
            }
        }
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            // imePadding asegura que la pantalla se desplace hacia arriba cuando el teclado aparece
            .imePadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        HomeHeader()

        Spacer(modifier = Modifier.height(24.dp))

        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = { onEvent(HomeEvent.OnSearchQueryChange(it)) },
            onClearClick = { onEvent(HomeEvent.OnClearSearch) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Etiqueta dinámica: RECIENTES o RESULTADOS
        Text(
            text = if (uiState.isSearching) "RESULTADOS" else "RECIENTES",
            color = TextSecondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Contenedor principal que maneja los 4 estados posibles de la lista
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            when {
                // Estado 1: Cargando datos de Room
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                // Estado 2: Base de datos vacía (Sin recientes)
                uiState.isEmpty && !uiState.isSearching -> {
                    Text(
                        text = "Aún no tienes snippets guardados.\n¡Agrega uno nuevo!",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                // Estado 3: Búsqueda sin resultados
                uiState.isEmpty && uiState.isSearching -> {
                    Text(
                        text = "No se encontraron resultados para\n\"${uiState.searchQuery}\"",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                // Estado 4: Con datos (Mostrar lista)
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.snippets,
                            key = { it.id } // Optimiza el rendimiento de la lista
                        ) { snippet ->
                            SnippetListItem(
                                snippet = snippet,
                                onClick = { onEvent(HomeEvent.OnSnippetClick(snippet.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- PREVIEWS (Obligatorios según el proyecto) ---

private val dummySnippets = listOf(
    SnippetEntity(1L, 1L, "Delete DB Container", "docker rm -f db", "Bash", false, 0L),
    SnippetEntity(2L, 2L, "Check K8s Pods", "kubectl get pods", "Bash", false, 0L),
    SnippetEntity(3L, 3L, "Room Query Flow", "@Query...", "Kotlin", true, 0L)
)

@Preview(showBackground = true, backgroundColor = 0xFF141A2B)
@Composable
private fun HomeScreenLoadingPreview() {
    DevVaultTheme {
        HomeScreen(
            uiState = HomeUiState(isLoading = true),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141A2B)
@Composable
private fun HomeScreenWithDataPreview() {
    DevVaultTheme {
        HomeScreen(
            uiState = HomeUiState(isLoading = false, snippets = dummySnippets),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141A2B)
@Composable
private fun HomeScreenEmptyPreview() {
    DevVaultTheme {
        HomeScreen(
            uiState = HomeUiState(isLoading = false, snippets = emptyList()),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141A2B)
@Composable
private fun HomeScreenNoResultsPreview() {
    DevVaultTheme {
        HomeScreen(
            uiState = HomeUiState(
                searchQuery = "Error 404",
                isLoading = false,
                snippets = emptyList()
            ),
            onEvent = {}
        )
    }
}