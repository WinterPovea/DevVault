package com.devvault.app.ui.add_snippet

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devvault.app.data.local.entity.CategoriaEntity
import com.devvault.app.ui.theme.BackgroundDark
import com.devvault.app.ui.theme.CodeBackground
import com.devvault.app.ui.theme.CodeText
import com.devvault.app.ui.theme.DevVaultTheme
import com.devvault.app.ui.theme.OutlineDark
import com.devvault.app.ui.theme.PrimaryCyan
import com.devvault.app.ui.theme.SurfaceDark
import com.devvault.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun AddSnippetRoute(
    onNavigateBack: () -> Unit,
    onNavigateToCategorias: () -> Unit = {},
    viewModel: AddSnippetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            viewModel.clearSnackbarMessage()
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            delay(1000)
            onNavigateBack()
        }
    }

    if (uiState.showAddCategoriaDialog) {
        CrearCategoriaDialog(
            onDismiss = { viewModel.setShowAddCategoriaDialog(false) },
            onCrear = { nombre, color ->
                viewModel.crearCategoria(nombre, color)
            }
        )
    }

    AddSnippetScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNombreChange = viewModel::onNombreChange,
        onContenidoChange = viewModel::onContenidoChange,
        onCategoriaSelected = viewModel::onCategoriaSelected,
        toggleDropdown = viewModel::toggleDropdown,
        onOpenAddCategoriaDialog = { viewModel.setShowAddCategoriaDialog(true) },
        onSave = viewModel::saveSnippet,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSnippetScreen(
    uiState: AddSnippetUiState,
    snackbarHostState: SnackbarHostState,
    onNombreChange: (String) -> Unit,
    onContenidoChange: (String) -> Unit,
    onCategoriaSelected: (Long) -> Unit,
    toggleDropdown: (Boolean) -> Unit,
    onOpenAddCategoriaDialog: () -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val successGreen = Color(0xFF00E676)

    val saveButtonColor by animateColorAsState(
        targetValue = if (uiState.isSaved) successGreen else PrimaryCyan,
        animationSpec = tween(durationMillis = 300),
        label = "buttonColor"
    )

    val categoriaSeleccionada = uiState.categorias.find { it.id == uiState.selectedCategoriaId }
    val visualTransformation = remember(categoriaSeleccionada?.nombre) {
        CodeSyntaxVisualTransformation(categoriaSeleccionada?.nombre ?: "")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Snippet", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = SurfaceDark,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- CAMPO NOMBRE ---
            Column {
                Text("Nombre", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.nombre,
                    onValueChange = onNombreChange,
                    isError = uiState.nombreError != null,
                    supportingText = uiState.nombreError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    placeholder = { Text("Snippet Name", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = OutlineDark,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            // --- SELECTOR DE CATEGORÍA ---
            Column {
                Text("Categoría", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = uiState.expandedDropdown,
                        onExpandedChange = toggleDropdown,
                        modifier = Modifier.weight(1f)
                    ) {
                        val selectedCategoryName = categoriaSeleccionada?.nombre
                            ?: if (uiState.categorias.isEmpty()) "No hay categorías (Crea una con +)" else "Selecciona una categoría"

                        OutlinedTextField(
                            value = selectedCategoryName,
                            onValueChange = {},
                            readOnly = true,
                            isError = uiState.categoriaError != null,
                            supportingText = uiState.categoriaError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.expandedDropdown) },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceDark,
                                unfocusedContainerColor = SurfaceDark,
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = OutlineDark,
                                errorBorderColor = MaterialTheme.colorScheme.error,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = uiState.expandedDropdown,
                            onDismissRequest = { toggleDropdown(false) },
                            modifier = Modifier.background(SurfaceDark)
                        ) {
                            if (uiState.categorias.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Sin categorías. Pulsa '+' para añadir una", color = TextSecondary) },
                                    onClick = { toggleDropdown(false) }
                                )
                            } else {
                                uiState.categorias.forEach { categoria ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(12.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(categoria.color))
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(categoria.nombre, color = Color.White)
                                            }
                                        },
                                        onClick = { onCategoriaSelected(categoria.id) }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = onOpenAddCategoriaDialog,
                        modifier = Modifier
                            .background(SurfaceDark, RoundedCornerShape(12.dp))
                            .border(1.dp, OutlineDark, RoundedCornerShape(12.dp))
                            .size(56.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Añadir Categoría",
                            tint = PrimaryCyan
                        )
                    }
                }
            }

            // --- EDITOR DE CÓDIGO (MONOSPACE + SYNTAX HIGHLIGHTING + RESPETO DE INDENTACIÓN) ---
            Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Text("Ingresa el Snippet", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxSize()) {
                    OutlinedTextField(
                        value = uiState.contenido,
                        onValueChange = onContenidoChange,
                        visualTransformation = visualTransformation,
                        isError = uiState.contenidoError != null,
                        supportingText = uiState.contenidoError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = CodeText
                        ),
                        placeholder = {
                            Text(
                                "Escribe o pega aquí tu código...\n(Se respetarán sangrías, espacios y saltos de línea)",
                                fontFamily = FontFamily.Monospace,
                                color = TextSecondary
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CodeBackground,
                            unfocusedContainerColor = CodeBackground,
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = OutlineDark,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            focusedTextColor = CodeText,
                            unfocusedTextColor = CodeText
                        )
                    )

                    Button(
                        onClick = {
                            if (!uiState.isSaved) onSave()
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .height(48.dp)
                            .animateContentSize(),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = saveButtonColor,
                            contentColor = Color.Black
                        )
                    ) {
                        if (uiState.isSaved) {
                            Icon(Icons.Default.Check, contentDescription = "Éxito")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guardado", fontWeight = FontWeight.Bold)
                        } else {
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CrearCategoriaDialog(
    onDismiss: () -> Unit,
    onCrear: (String, Long) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    val coloresDisponibles = remember {
        listOf(
            0xFF33C8F2, // PrimaryCyan
            0xFF2496ED, // Docker Blue
            0xFF7F52FF, // Kotlin Purple
            0xFFF1502F, // Git Orange
            0xFF4479A1, // SQL Blue
            0xFFFCC624, // Linux Yellow
            0xFF00ADD8, // Go Cyan
            0xFF00E676  // Success Green
        )
    }
    var colorSeleccionado by remember { mutableStateOf(coloresDisponibles.first()) }
    var errorNombre by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Nueva Categoría", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Crea una categoría para organizar tus snippets:",
                    color = TextSecondary,
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        if (errorNombre && it.isNotBlank()) errorNombre = false
                    },
                    isError = errorNombre,
                    supportingText = if (errorNombre) {
                        { Text("El nombre no puede estar vacío", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    label = { Text("Nombre de la categoría") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BackgroundDark,
                        unfocusedContainerColor = BackgroundDark,
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = OutlineDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    Text("Color representativo:", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(coloresDisponibles) { colorVal ->
                            val isSelected = colorVal == colorSeleccionado
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorVal))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { colorSeleccionado = colorVal },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Seleccionado",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.trim().isBlank()) {
                        errorNombre = true
                    } else {
                        onCrear(nombre.trim(), colorSeleccionado)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Crear", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF141A2B)
@Composable
private fun AddSnippetScreenPreview() {
    DevVaultTheme {
        AddSnippetScreen(
            uiState = AddSnippetUiState(
                nombre = "Imprimir en consola",
                contenido = "fun main() {\n    println(\"Hello DevVault!\")\n}",
                selectedCategoriaId = 1L,
                categorias = listOf(
                    CategoriaEntity(id = 1L, nombre = "Kotlin", color = 0xFF7F52FF)
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onNombreChange = {},
            onContenidoChange = {},
            onCategoriaSelected = {},
            toggleDropdown = {},
            onOpenAddCategoriaDialog = {},
            onSave = {},
            onNavigateBack = {}
        )
    }
}