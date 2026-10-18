package com.devvault.app.ui.snippet_detail

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devvault.app.ui.model.SnippetUi
import com.devvault.app.ui.add_snippet.CodeSyntaxVisualTransformation
import com.devvault.app.ui.components.DeleteConfirmDialog
import com.devvault.app.ui.components.ErrorView
import com.devvault.app.ui.components.LoadingView
import com.devvault.app.ui.theme.*
import com.devvault.app.util.copiarAlPortapapeles

@Composable
fun SnippetDetailRoute(
    onBackClick: () -> Unit,
    viewModel: SnippetDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SnippetDetailUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                SnippetDetailUiEvent.NavigateBack -> {
                    onBackClick()
                }
            }
        }
    }

    when (val state = uiState) {
        is SnippetDetailUiState.Loading -> {
            LoadingView()
        }
        is SnippetDetailUiState.Error -> {
            ErrorView(mensaje = state.mensaje)
        }
        is SnippetDetailUiState.Success -> {
            SnippetDetailScreen(
                uiState = state,
                onBackClick = onBackClick,
                onCopyClick = { texto ->
                    copiarAlPortapapeles(context, texto)
                    viewModel.notificarCopiado()
                },
                onToggleFavorito = viewModel::alternarFavorito,
                onDeleteClick = viewModel::eliminarSnippet,
                onEditClick = viewModel::editarSnippet
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnippetDetailScreen(
    uiState: SnippetDetailUiState.Success,
    onBackClick: () -> Unit,
    onCopyClick: (String) -> Unit,
    onToggleFavorito: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: (String, String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    val visualTransformation = remember(uiState.categoriaNombre) {
        CodeSyntaxVisualTransformation(uiState.categoriaNombre)
    }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            titulo = "Eliminar snippet",
            mensaje = "¿Estás seguro de que deseas eliminar este snippet? Esta acción no se puede deshacer.",
            onConfirmar = {
                showDeleteDialog = false
                onDeleteClick()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showEditDialog) {
        EditarSnippetDialog(
            snippet = uiState.snippet,
            onDismiss = { showEditDialog = false },
            onGuardar = { nuevoTitulo, nuevoContenido ->
                showEditDialog = false
                onEditClick(nuevoTitulo, nuevoContenido)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Snippet", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar snippet",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onToggleFavorito) {
                        Icon(
                            imageVector = if (uiState.snippet.esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (uiState.snippet.esFavorito) "Quitar de favoritos" else "Marcar como favorito",
                            tint = if (uiState.snippet.esFavorito) PrimaryCyan else Color.White
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar snippet",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = uiState.snippet.titulo,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        color = PrimaryCyan.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = uiState.categoriaNombre,
                            color = PrimaryCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CodeBackground),
                        border = BorderStroke(1.dp, OutlineDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.padding(16.dp)) {
                            val transformedText = visualTransformation.filter(
                                androidx.compose.ui.text.AnnotatedString(uiState.snippet.contenido)
                            ).text

                            Text(
                                text = transformedText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Button(
                        onClick = { onCopyClick(uiState.snippet.contenido) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Copiar código", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditarSnippetDialog(
    snippet: SnippetUi,
    onDismiss: () -> Unit,
    onGuardar: (String, String) -> Unit
) {
    var titulo by remember(snippet.id) { mutableStateOf(snippet.titulo) }
    var contenido by remember(snippet.id) { mutableStateOf(snippet.contenido) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar snippet", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        focusedLabelColor = PrimaryCyan,
                        cursorColor = PrimaryCyan,
                        unfocusedBorderColor = OutlineDark,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contenido,
                    onValueChange = { contenido = it },
                    label = { Text("Snippet") },
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        focusedLabelColor = PrimaryCyan,
                        cursorColor = PrimaryCyan,
                        unfocusedBorderColor = OutlineDark,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onGuardar(titulo, contenido) },
                enabled = titulo.isNotBlank() && contenido.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryCyan,
                    contentColor = Color.Black
                )
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.White.copy(alpha = 0.7f))
            }
        },
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(16.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF141A2B)
@Composable
private fun SnippetDetailScreenPreview() {
    DevVaultTheme {
        SnippetDetailScreen(
            uiState = SnippetDetailUiState.Success(
                snippet = SnippetUi(
                    id = 1L,
                    categoriaId = 1L,
                    titulo = "Configuración Room DB",
                    contenido = "@Database(entities = [Snippet::class], version = 1)\nabstract class AppDatabase : RoomDatabase()",
                    lenguaje = "Kotlin",
                    categoriaNombre = "Kotlin",
                    esFavorito = true,
                    fechaCreacion = 0L,
                    fechaFavorito = 100L
                ),
                categoriaNombre = "Kotlin"
            ),
            onBackClick = {},
            onCopyClick = {},
            onToggleFavorito = {},
            onDeleteClick = {},
            onEditClick = { _, _ -> }
        )
    }
}
