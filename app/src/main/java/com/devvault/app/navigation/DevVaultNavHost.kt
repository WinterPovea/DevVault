package com.devvault.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.devvault.app.presentation.categoria.CategoriaDetailRoute
import com.devvault.app.presentation.categorias.CategoriasRoute

@Composable
fun DevVaultNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DevVaultRoute.Home,
        modifier = modifier
    ) {
        composable<DevVaultRoute.Home> {
            PlaceholderScreen("Home")
        }

        composable<DevVaultRoute.Categorias> {
            CategoriasRoute(
                onCategoriaClick = { navController.navigate(DevVaultRoute.CategoriaDetail(it)) }
            )
        }

        composable<DevVaultRoute.Agregar> {
            PlaceholderScreen("Agregar snippet")
        }

        composable<DevVaultRoute.Favoritos> {
            PlaceholderScreen("Favoritos")
        }

        composable<DevVaultRoute.CategoriaDetail> {
            CategoriaDetailRoute(
                onBack = { navController.popBackStack() },
                onSnippetClick = { navController.navigate(DevVaultRoute.SnippetDetail(it)) }
            )
        }

        composable<DevVaultRoute.SnippetDetail> { backStackEntry ->
            val ruta = backStackEntry.toRoute<DevVaultRoute.SnippetDetail>()
            PlaceholderScreen("Snippet #${ruta.snippetId}")
        }
    }
}

@Composable
private fun PlaceholderScreen(nombre: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Pantalla: $nombre (pendiente)")
    }
}