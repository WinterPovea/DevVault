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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devvault.app.data.local.entity.SnippetEntity

@Composable
fun FavoritosRoute(
    modifier: Modifier = Modifier,
    viewModel: FavoritosViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    FavoritosScreen(
        uiState = uiState,
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
    onCopyClick: (String) -> Unit,
    onFavoriteClick: (SnippetEntity) -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Colores del diseño
    val backgroundColor = Color(0xFF0B101A)
    val cyanAccent = Color(0xFF00E5FF)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text(text = "Favoritos", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = cyanAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
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
                    CircularProgressIndicator(
                        color = cyanAccent,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is FavoritosUiState.Empty -> {
                    Text(
                        text = "Aún no tienes comandos favoritos.",
                        color = Color(0xFFA0AABF),
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

@Preview(showSystemUi = true)
@Composable
private fun FavoritosScreenPreview() {
    MaterialTheme {
        FavoritosScreen(
            uiState = FavoritosUiState.Success(
                favoritos = listOf(
                    SnippetEntity(
                        id = 1,
                        categoriaId = 1,
                        titulo = "Crear ENV",
                        contenido = "docker run --env-file .env app",
                        lenguaje = "Crear ENV container",
                        esFavorito = true,
                        fechaCreacion = System.currentTimeMillis()
                    ),
                    SnippetEntity(
                        id = 2,
                        categoriaId = 1,
                        titulo = "Borrar DB",
                        contenido = "docker rm -f db_service",
                        lenguaje = "Borrar DB servicio",
                        esFavorito = true,
                        fechaCreacion = System.currentTimeMillis()
                    )
                )
            ),
            onCopyClick = {},
            onFavoriteClick = {}
        )
    }
}