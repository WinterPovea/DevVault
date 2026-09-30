package com.devvault.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.devvault.app.navigation.DevVaultRoute

private data class BottomBarDestino(
    val ruta: DevVaultRoute,
    val icono: ImageVector,
    val etiqueta: String
)

private val destinosBottomBar = listOf(
    BottomBarDestino(DevVaultRoute.Agregar, Icons.Default.Add, "Agregar"),
    BottomBarDestino(DevVaultRoute.Categorias, Icons.Default.List, "Categorías"),
    BottomBarDestino(DevVaultRoute.Home, Icons.Default.Home, "Home"),
    BottomBarDestino(DevVaultRoute.Favoritos, Icons.Default.Star, "Favoritos")
)

@Composable
fun DevVaultBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (DevVaultRoute) -> Unit
) {
    NavigationBar {
        destinosBottomBar.forEach { destino ->
            val seleccionado = when (destino.ruta) {
                is DevVaultRoute.Home -> currentDestination?.hasRoute<DevVaultRoute.Home>() == true
                is DevVaultRoute.Categorias -> currentDestination?.hasRoute<DevVaultRoute.Categorias>() == true
                is DevVaultRoute.Agregar -> currentDestination?.hasRoute<DevVaultRoute.Agregar>() == true
                is DevVaultRoute.Favoritos -> currentDestination?.hasRoute<DevVaultRoute.Favoritos>() == true
                else -> false
            }

            NavigationBarItem(
                selected = seleccionado,
                onClick = { onNavigate(destino.ruta) },
                icon = { Icon(destino.icono, contentDescription = destino.etiqueta) },
                label = { Text(destino.etiqueta) }
            )
        }
    }
}