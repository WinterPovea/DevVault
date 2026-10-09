package com.devvault.app.presentation.add_snippet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devvault.app.data.local.entity.CategoriaEntity

@Composable
fun AddSnippetScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddSnippetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    AddSnippetContent(
        uiState = uiState,
        onNombreChange = viewModel::onNombreChange,
        onContenidoChange = viewModel::onContenidoChange,
        onCategoriaSelected = viewModel::onCategoriaSelected,
        toggleDropdown = viewModel::toggleDropdown,
        onSave = viewModel::saveSnippet,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSnippetContent(
    uiState: AddSnippetUiState,
    onNombreChange: (String) -> Unit,
    onContenidoChange: (String) -> Unit,
    onCategoriaSelected: (Long) -> Unit,
    toggleDropdown: (Boolean) -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val backgroundColor = Color(0xFF1B1E26)
    val cardColor = Color(0xFF252A34)
    val accentCyan = Color(0xFF00E5FF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Snippet", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Nombre", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = onNombreChange,
                placeholder = { Text("Snippet Name", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardColor,
                    unfocusedContainerColor = cardColor,
                    focusedBorderColor = accentCyan,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Text("Categoría", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = uiState.expandedDropdown,
                    onExpandedChange = toggleDropdown,
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = uiState.categorias.find { it.id == uiState.selectedCategoriaId }?.nombre ?: "Elegir",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.expandedDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardColor,
                            unfocusedContainerColor = cardColor,
                            focusedBorderColor = accentCyan,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = uiState.expandedDropdown,
                        onDismissRequest = { toggleDropdown(false) }
                    ) {
                        uiState.categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre) },
                                onClick = { onCategoriaSelected(categoria.id) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = { /* Acción para agregar categoría rápida */ },
                    modifier = Modifier
                        .background(cardColor, RoundedCornerShape(12.dp))
                        .size(56.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Añadir Categoría",
                        tint = Color.Gray
                    )
                }
            }

            Text("Ingresa el Snippet", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                OutlinedTextField(
                    value = uiState.contenido,
                    onValueChange = onContenidoChange,
                    placeholder = { Text("Code goes here...", color = Color.Gray) },
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardColor,
                        unfocusedContainerColor = cardColor,
                        focusedBorderColor = accentCyan,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentCyan,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Agregar Snippet Screen")
@Composable
fun AddSnippetPreview() {
    MaterialTheme {
        AddSnippetContent(
            uiState = AddSnippetUiState(
                nombre = "Levantar Contenedor",
                contenido = "docker-compose up -d",
                categorias = listOf(CategoriaEntity(id = 1L, nombre = "Docker", color = 0)),
                selectedCategoriaId = 1L
            ),
            onNombreChange = {},
            onContenidoChange = {},
            onCategoriaSelected = {},
            toggleDropdown = {},
            onSave = {},
            onNavigateBack = {}
        )
    }
}