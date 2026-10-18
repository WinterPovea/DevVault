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
import com.devvault.app.ui.components.EmptyView
import com.devvault.app.ui.components.ErrorView
import com.devvault.app.ui.components.LoadingView
import com.devvault.app.ui.components.SearchBar
import com.devvault.app.ui.home.components.HomeHeader
import com.devvault.app.ui.home.components.SnippetListItem
import com.devvault.app.ui.theme.DevVaultTheme
import com.devvault.app.ui.theme.TextSecondary


@Composable
fun HomeRoute(
    onCategoriaClick: (Long) -> Unit,
    onSnippetClick: (Long) -> Unit, // AGREGAMOS ESTE PARÁMETRO
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onEvent = { event ->
            if (event is HomeEvent.OnSnippetClick) {
                // Ahora navegamos directo al detalle del snippet usando su ID
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

        val searchQuery = when (uiState) {
            is HomeUiState.Success -> uiState.searchQuery
            else -> ""
        }

        SearchBar(
            query = searchQuery,
            onQueryChange = { onEvent(HomeEvent.OnSearchQueryChange(it)) },
            onClearClick = { onEvent(HomeEvent.OnClearSearch) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        val isSearching = (uiState as? HomeUiState.Success)?.isSearching == true

        // Etiqueta dinámica: RECIENTES o RESULTADOS
        Text(
            text = if (isSearching) "RESULTADOS" else "RECIENTES",
            color = TextSecondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Contenedor principal que maneja los estados posibles de la lista
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (uiState) {
                // Estado 1: Cargando datos de Room
                is HomeUiState.Loading -> LoadingView()

                // Estado 2: Error
                is HomeUiState.Error -> ErrorView(uiState.mensaje)

                // Estado 3: Éxito (con datos, vacío o sin resultados de búsqueda)
                is HomeUiState.Success -> {
                    when {
                        uiState.isEmpty && !uiState.isSearching -> {
                            EmptyView("Aún no tienes snippets guardados.\n¡Agrega uno nuevo!")
                        }
                        uiState.isEmpty && uiState.isSearching -> {
                            EmptyView("No se encontraron resultados para\n\"${uiState.searchQuery}\"")
                        }
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
            uiState = HomeUiState.Loading,
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141A2B)
@Composable
private fun HomeScreenWithDataPreview() {
    DevVaultTheme {
        HomeScreen(
            uiState = HomeUiState.Success(snippets = dummySnippets),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141A2B)
@Composable
private fun HomeScreenEmptyPreview() {
    DevVaultTheme {
        HomeScreen(
            uiState = HomeUiState.Success(snippets = emptyList()),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141A2B)
@Composable
private fun HomeScreenNoResultsPreview() {
    DevVaultTheme {
        HomeScreen(
            uiState = HomeUiState.Success(
                searchQuery = "Error 404",
                snippets = emptyList()
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141A2B)
@Composable
private fun HomeScreenErrorPreview() {
    DevVaultTheme {
        HomeScreen(
            uiState = HomeUiState.Error("Error al cargar snippets"),
            onEvent = {}
        )
    }
}