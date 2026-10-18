package com.devvault.app.ui.categorias

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import com.devvault.app.ui.model.CategoriaUi
import com.devvault.app.ui.theme.DevVaultTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devvault.app.ui.components.CategoriaCard
import com.devvault.app.ui.components.DeleteConfirmDialog
import com.devvault.app.ui.components.EmptyView
import com.devvault.app.ui.components.ErrorView
import com.devvault.app.ui.components.LoadingView
import com.devvault.app.ui.components.SearchBar
import com.devvault.app.ui.theme.resolverColorCategoria

@Composable
fun CategoriasRoute(
    onCategoriaClick: (Long) -> Unit,
    viewModel: CategoriasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CategoriasUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    CategoriasScreen(
        uiState = uiState,
        onBusquedaChange = viewModel::onBusquedaChange,
        onCategoriaClick = onCategoriaClick,
        onToggleSeleccion = viewModel::onToggleSeleccion,
        onCancelarSeleccion = viewModel::onCancelarSeleccion,
        onEditarCategoria = viewModel::onEditarCategoria,
        onEliminarCategoria = viewModel::onEliminarCategoria
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    uiState: CategoriasUiState,
    onBusquedaChange: (String) -> Unit,
    onCategoriaClick: (Long) -> Unit,
    onToggleSeleccion: (Long) -> Unit,
    onCancelarSeleccion: () -> Unit,
    onEditarCategoria: (Long, String) -> Unit,
    onEliminarCategoria: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarDialogoEditar by rememberSaveable { mutableStateOf(false) }
    var mostrarDialogoEliminar by rememberSaveable { mutableStateOf(false) }

    val seleccionada = (uiState as? CategoriasUiState.Success)?.let { state ->
        state.categorias.find { it.id == state.seleccionadaId }
    }

    if (mostrarDialogoEditar && seleccionada != null) {
        EditarCategoriaDialog(
            categoria = seleccionada,
            onDismiss = { mostrarDialogoEditar = false },
            onGuardar = { nuevoNombre ->
                onEditarCategoria(seleccionada.id, nuevoNombre)
                mostrarDialogoEditar = false
            }
        )
    }

    if (mostrarDialogoEliminar && seleccionada != null) {
        val total = seleccionada.totalSnippets
        val mensajeSnippets = when (total) {
            0 -> "Esta categoría no contiene snippets."
            1 -> "Se eliminará 1 snippet asociado debido a la clave foránea en cascada (FK CASCADE)."
            else -> "Se eliminarán sus $total snippets asociados debido a la clave foránea en cascada (FK CASCADE)."
        }
        DeleteConfirmDialog(
            titulo = "¿Eliminar categoría?",
            mensaje = "¿Estás seguro de que deseas eliminar \"${seleccionada.nombre}\"?",
            mensajeAdvertencia = mensajeSnippets,
            onConfirmar = {
                onEliminarCategoria(seleccionada.id)
                mostrarDialogoEliminar = false
            },
            onDismiss = { mostrarDialogoEliminar = false }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (seleccionada != null) {
                TopAppBar(
                    title = {
                        Text(
                            text = seleccionada.nombre,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onCancelarSeleccion) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancelar selección"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { mostrarDialogoEditar = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar"
                            )
                        }
                        IconButton(onClick = { mostrarDialogoEliminar = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                )
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is CategoriasUiState.Loading -> LoadingView(Modifier.padding(paddingValues))

            is CategoriasUiState.Error -> ErrorView(uiState.mensaje, Modifier.padding(paddingValues))

            is CategoriasUiState.Success -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SearchBar(
                    query = uiState.busqueda,
                    onQueryChange = onBusquedaChange,
                    onClearClick = { onBusquedaChange("") },
                    placeholder = "Buscar categoría"
                )
                if (uiState.categorias.isEmpty()) {
                    EmptyView("No hay categorías")
                } else {
                    val screenWidthDp = LocalConfiguration.current.screenWidthDp
                    val columnCount = when {
                        screenWidthDp >= 840 -> 4
                        screenWidthDp >= 600 -> 3
                        else -> 2
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columnCount),
                        modifier = Modifier.padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.categorias, key = { it.id }) { categoria ->
                            CategoriaCard(
                                nombre = categoria.nombre,
                                totalSnippets = categoria.totalSnippets,
                                color = resolverColorCategoria(categoria.color),
                                estaSeleccionada = categoria.id == uiState.seleccionadaId,
                                haySeleccionActiva = uiState.seleccionadaId != null,
                                onToggleSeleccion = { onToggleSeleccion(categoria.id) },
                                onClick = { onCategoriaClick(categoria.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditarCategoriaDialog(
    categoria: CategoriaUi,
    onDismiss: () -> Unit,
    onGuardar: (String) -> Unit
) {
    var nombreText by rememberSaveable(categoria.id) { mutableStateOf(categoria.nombre) }
    val esValido = nombreText.trim().isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar categoría") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombreText,
                    onValueChange = { nombreText = it },
                    label = { Text("Nombre de la categoría") },
                    singleLine = true,
                    isError = !esValido && nombreText.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (nombreText.trim().isEmpty()) {
                    Text(
                        text = "El nombre no puede estar vacío",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (esValido) {
                        onGuardar(nombreText.trim())
                    }
                },
                enabled = esValido
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun CategoriasScreenPreview() {
    DevVaultTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CategoriasScreen(
                uiState = CategoriasUiState.Success(
                    categorias = listOf(
                        CategoriaUi(1, "Docker", 0xFF2496ED, 20),
                        CategoriaUi(2, "Git", 0xFFF1502F, 1),
                        CategoriaUi(3, "SQL", 0xFF4479A1, 15),
                        CategoriaUi(4, "Kotlin", 0xFF7F52FF, 7)
                    ),
                    busqueda = "",
                    seleccionadaId = 2L
                ),
                onBusquedaChange = {},
                onCategoriaClick = {},
                onToggleSeleccion = {},
                onCancelarSeleccion = {},
                onEditarCategoria = { _, _ -> },
                onEliminarCategoria = {}
            )
        }
    }
}