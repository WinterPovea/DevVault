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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
        EliminarCategoriaDialog(
            categoria = seleccionada,
            onDismiss = { mostrarDialogoEliminar = false },
            onConfirmar = {
                onEliminarCategoria(seleccionada.id)
                mostrarDialogoEliminar = false
            }
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
            is CategoriasUiState.Loading -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is CategoriasUiState.Error -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                Alignment.Center
            ) {
                Text(uiState.mensaje, color = MaterialTheme.colorScheme.error)
            }

            is CategoriasUiState.Success -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
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
                            CategoriaItemCard(
                                categoria = categoria,
                                estaSeleccionada = categoria.id == uiState.seleccionadaId,
                                haySeleccionActiva = uiState.seleccionadaId != null,
                                onToggleSeleccion = onToggleSeleccion,
                                onClick = { onCategoriaClick(categoria.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoriaItemCard(
    categoria: CategoriaConConteo,
    estaSeleccionada: Boolean,
    haySeleccionActiva: Boolean,
    onToggleSeleccion: (Long) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorCategoria = resolverColorCategoria(categoria.color)
    val borde = if (estaSeleccionada) {
        BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, colorCategoria)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .combinedClickable(
                onClick = {
                    if (haySeleccionActiva) {
                        onToggleSeleccion(categoria.id)
                    } else {
                        onClick()
                    }
                },
                onLongClick = {
                    onToggleSeleccion(categoria.id)
                }
            ),
        shape = RoundedCornerShape(14.dp),
        border = borde,
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionada) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colorCategoria),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoria.nombre.take(1).uppercase(),
                        color = Color(0xFF05070D),
                        fontWeight = FontWeight.Bold
                    )
                }

                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                    Checkbox(
                        checked = estaSeleccionada,
                        onCheckedChange = { onToggleSeleccion(categoria.id) },
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Text(
                text = categoria.nombre,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 12.dp)
            )

            Text(
                text = if (categoria.totalSnippets == 1) "1 snippet" else "${categoria.totalSnippets} snippets",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EditarCategoriaDialog(
    categoria: CategoriaConConteo,
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

@Composable
private fun EliminarCategoriaDialog(
    categoria: CategoriaConConteo,
    onDismiss: () -> Unit,
    onConfirmar: () -> Unit
) {
    val total = categoria.totalSnippets
    val mensajeSnippets = when (total) {
        0 -> "Esta categoría no contiene snippets."
        1 -> "Se eliminará 1 snippet asociado debido a la clave foránea en cascada (FK CASCADE)."
        else -> "Se eliminarán sus $total snippets asociados debido a la clave foránea en cascada (FK CASCADE)."
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Eliminar categoría?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("¿Estás seguro de que deseas eliminar \"${categoria.nombre}\"?")
                Text(
                    text = mensajeSnippets,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (total > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text("Eliminar", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun resolverColorCategoria(colorLong: Long): Color {
    return try {
        if (colorLong == 0L) {
            Color.Gray
        } else {
            val color = Color(colorLong)
            if (color.alpha == 0f) Color.Gray else color
        }
    } catch (_: Exception) {
        Color.Gray
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
                        CategoriaConConteo(2, "Git", 0xFFF1502F, 1),
                        CategoriaConConteo(3, "SQL", 0xFF4479A1, 15),
                        CategoriaConConteo(4, "Kotlin", 0xFF7F52FF, 7)
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