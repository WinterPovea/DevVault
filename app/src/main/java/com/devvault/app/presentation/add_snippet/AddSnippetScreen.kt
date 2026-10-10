package com.devvault.app.presentation.add_snippet

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import kotlinx.coroutines.delay

@Composable
fun AddSnippetScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCategorias: () -> Unit = {},
    viewModel: AddSnippetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Regresar automáticamente después de que el usuario vea la animación de éxito
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            delay(1000) // 1 segundo de pausa para disfrutar el botón verde
            onNavigateBack()
        }
    }

    AddSnippetContent(
        uiState = uiState,
        onNombreChange = viewModel::onNombreChange,
        onContenidoChange = viewModel::onContenidoChange,
        onCategoriaSelected = viewModel::onCategoriaSelected,
        toggleDropdown = viewModel::toggleDropdown,
        onSave = viewModel::saveSnippet,
        onNavigateBack = onNavigateBack,
        onAddCategoryClick = onNavigateToCategorias
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
    onNavigateBack: () -> Unit,
    onAddCategoryClick: () -> Unit
) {
    val backgroundColor = Color(0xFF1B1E26)
    val cardColor = Color(0xFF252A34)
    val accentCyan = Color(0xFF00E5FF)
    val successGreen = Color(0xFF00E676) // Verde brillante para el éxito

    // Animación de color para el botón de guardado
    val saveButtonColor by animateColorAsState(
        targetValue = if (uiState.isSaved) successGreen else accentCyan,
        animationSpec = tween(durationMillis = 300),
        label = "buttonColor"
    )

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
                    val selectedCategoryName = uiState.categorias.find { it.id == uiState.selectedCategoriaId }?.nombre
                        ?: if (uiState.categorias.isEmpty()) "No hay categorías (Crea una)" else "Elegir"

                    OutlinedTextField(
                        value = selectedCategoryName,
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
                        if (uiState.categorias.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Sin categorías registradas") },
                                onClick = { toggleDropdown(false) }
                            )
                        } else {
                            uiState.categorias.forEach { categoria ->
                                DropdownMenuItem(
                                    text = { Text(categoria.nombre) },
                                    onClick = { onCategoriaSelected(categoria.id) }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = onAddCategoryClick,
                    modifier = Modifier
                        .background(cardColor, RoundedCornerShape(12.dp))
                        .size(56.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Añadir Categoría",
                        tint = accentCyan
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
                    onClick = {
                        // Solo permite guardar si no se está guardando ya
                        if (!uiState.isSaved) onSave()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .height(48.dp)
                        .animateContentSize(), // Anima el cambio de tamaño del contenido suavemente
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = saveButtonColor,
                        contentColor = Color.Black
                    )
                ) {
                    // Si se guardó, mostramos un Check; si no, el texto "Save"
                    if (uiState.isSaved) {
                        Icon(Icons.Default.Check, contentDescription = "Éxito")
                    } else {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}