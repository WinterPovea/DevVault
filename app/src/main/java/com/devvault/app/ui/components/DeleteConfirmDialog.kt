package com.devvault.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.devvault.app.ui.theme.SurfaceDark
import com.devvault.app.ui.theme.TextSecondary

@Composable
fun DeleteConfirmDialog(
    titulo: String,
    mensaje: String,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit,
    mensajeAdvertencia: String? = null,
    textoConfirmar: String = "Eliminar",
    textoCancelar: String = "Cancelar"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = mensaje,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (!mensajeAdvertencia.isNullOrBlank()) {
                    Text(
                        text = mensajeAdvertencia,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                )
            ) {
                Text(textoConfirmar)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(textoCancelar, color = TextSecondary)
            }
        },
        containerColor = SurfaceDark
    )
}
