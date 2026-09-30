package com.devvault.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.devvault.app.navigation.DevVaultNavHost
import com.devvault.app.navigation.DevVaultRoute
import com.devvault.app.ui.components.DevVaultBottomBar

@Composable
fun DevVaultApp() {
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val mostrarBottomBar = currentDestination?.let { destino ->
        destino.hasRoute<DevVaultRoute.Home>() ||
                destino.hasRoute<DevVaultRoute.Categorias>() ||
                destino.hasRoute<DevVaultRoute.Agregar>() ||
                destino.hasRoute<DevVaultRoute.Favoritos>()
    } ?: true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (mostrarBottomBar) {
                DevVaultBottomBar(
                    currentDestination = currentDestination,
                    onNavigate = { ruta ->
                        navController.navigate(ruta) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        DevVaultNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DevVaultBottomBarPreview() {
    DevVaultBottomBar(
        currentDestination = null,
        onNavigate = {}
    )
}