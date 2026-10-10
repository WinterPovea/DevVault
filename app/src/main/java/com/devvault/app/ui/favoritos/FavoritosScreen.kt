package com.devvault.app.ui.favoritos

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@Composable
fun FavoritosRoute(
    modifier: Modifier = Modifier,
    viewModel: FavoritosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    FavoritosScreen(
        uiState = uiState,
        onCopyClick = { textoCodigo ->
            // Portapapeles nativo de Android (100% limpio y sin tachar)
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Código", textoCodigo)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(context, "Código copiado al portapapeles", Toast.LENGTH_SHORT).show()
        },
        onFavoriteClick = { snippet ->
            viewModel.alternarFavorito(snippet)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    uiState: FavoritosUiState,
    onCopyClick: (String) -> Unit,
    onFavoriteClick: (SnippetEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Favoritos", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            when (uiState) {
                is FavoritosUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is FavoritosUiState.Empty -> {
                    Text(
                        text = "Aún no tienes comandos favoritos.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is FavoritosUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(items = uiState.favoritos, key = { it.id }) { snippet ->
                            FavoritoSnippetCard(
                                snippet = snippet,
                                onCopyClick = onCopyClick,
                                onFavoriteClick = { onFavoriteClick(snippet) }
                            )
                        }
                    }
                }
            }
        }
    }
}