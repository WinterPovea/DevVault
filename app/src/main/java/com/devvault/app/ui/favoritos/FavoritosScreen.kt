package com.devvault.app.ui.favoritos

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devvault.app.data.local.entity.SnippetEntity
import com.devvault.app.ui.components.SnippetCard
import com.devvault.app.ui.home.components.SearchBar

@Composable
fun FavoritosRoute(
    modifier: Modifier = Modifier,
    viewModel: FavoritosViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val context = LocalContext.current

    FavoritosScreen(
        uiState = uiState,
        searchQuery = searchQuery,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onCopyClick = { textoCodigo ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Código", textoCodigo)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Código copiado al portapapeles", Toast.LENGTH_SHORT).show()
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
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCopyClick: (String) -> Unit,
    onFavoriteClick: (SnippetEntity) -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

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
                        CircularProgressIndicator(
                            color = colors.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is FavoritosUiState.Empty -> {
                        Text(
                            text = if (searchQuery.isEmpty()) "Aún no tienes comandos favoritos." else "No se encontraron resultados.",
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is FavoritosUiState.Success -> {
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
