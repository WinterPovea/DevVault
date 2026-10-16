package com.devvault.app.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devvault.app.ui.theme.DevVaultTheme
import com.devvault.app.ui.theme.PrimaryCyan

@Composable
fun HomeHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Fila para el Logo y el nombre de la app
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Cuadrado redondeado con la "D"
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryCyan),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "D",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "DevVault",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Título principal de dos líneas
        Text(
            text = "¿Qué buscamos\nhoy?",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

// Preview para ver el diseño sin tener que correr la app en el emulador
@Preview(showBackground = true, backgroundColor = 0xFF141A2B) // Color de fondo del boceto
@Composable
private fun HomeHeaderPreview() {
    DevVaultTheme {
        HomeHeader(modifier = Modifier.padding(16.dp))
    }
}