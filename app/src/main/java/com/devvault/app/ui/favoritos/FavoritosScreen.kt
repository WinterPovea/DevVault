package com.devvault.app.ui.favoritos

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devvault.app.ui.model.SnippetUi
import com.devvault.app.ui.components.EmptyView
import com.devvault.app.ui.components.ErrorView
import com.devvault.app.ui.components.LoadingView
import com.devvault.app.ui.components.SearchBar
import com.devvault.app.ui.components.SnippetCard
import com.devvault.app.ui.theme.DevVaultTheme
import com.devvault.app.util.copiarAlPortapapeles

@Composable
fun FavoritosRoute(
    modifier: Modifier = Modifier,
    viewModel: FavoritosViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is FavoritosUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    FavoritosScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchChange,
        onCopyClick = { textoCodigo ->
            copiarAlPortapapeles(context, textoCodigo)
            viewModel.notificarCopiado()
        },
        onFavoriteClick = { snippet ->
            viewModel.alternarFavorito(snippet)
        },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    uiState: FavoritosUiState,
    onSearchQueryChange: (String) -> Unit,
    onCopyClick: (String) -> Unit,
    onFavoriteClick: (SnippetUi) -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val searchQuery = when (uiState) {
        is FavoritosUiState.Success -> uiState.searchQuery
        else -> ""
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text(text = "Favoritos", fontWeight = FontWeight.Bold, color = colors.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = colors.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background
                )
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onClearClick = { onSearchQueryChange("") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (uiState) {
                    is FavoritosUiState.Loading -> {
                        LoadingView()
                    }
                    is FavoritosUiState.Error -> {
                        ErrorView(mensaje = uiState.mensaje)
                    }
                    is FavoritosUiState.Success -> {
                        if (uiState.isEmpty) {
                            EmptyView(
                                mensaje = if (searchQuery.isEmpty()) "Aún no tienes comandos favoritos." else "No se encontraron resultados."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(items = uiState.favoritos, key = { it.id }) { snippet ->
                                    SnippetCard(
                                        snippet = snippet,
                                        onToggleFavorito = { onFavoriteClick(snippet) },
                                        onCopiar = { onCopyClick(snippet.contenido) },
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

@Preview(showBackground = true, backgroundColor = 0xFF141A2B)
@Composable
private fun FavoritosScreenPreview() {
    DevVaultTheme {
        FavoritosScreen(
            uiState = FavoritosUiState.Success(
                favoritos = listOf(
                    SnippetUi(
                        id = 1L,
                        categoriaId = 1L,
                        titulo = "Docker compose up",
                        contenido = "docker compose up -d",
                        lenguaje = "Docker",
                        esFavorito = true,
                        fechaCreacion = 0L,
                        fechaFavorito = 100L
                    ),
                    SnippetUi(
                        id = 2L,
                        categoriaId = 2L,
                        titulo = "List pods",
                        contenido = "kubectl get pods -A",
                        lenguaje = "Kubernetes",
                        esFavorito = true,
                        fechaCreacion = 0L,
                        fechaFavorito = 200L
                    )
                ),
                searchQuery = ""
            ),
            onSearchQueryChange = {},
            onCopyClick = {},
            onFavoriteClick = {}
        )
    }
}
