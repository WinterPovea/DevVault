package com.devvault.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.devvault.app.ui.home.HomeRoute
import com.devvault.app.ui.add_snippet.AddSnippetRoute
import com.devvault.app.ui.categoria.CategoriaDetailRoute
import com.devvault.app.ui.categorias.CategoriasRoute
import com.devvault.app.ui.favoritos.FavoritosRoute
import com.devvault.app.ui.snippet_detail.SnippetDetailRoute

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
            HomeRoute(
                onCategoriaClick = { navController.navigate(DevVaultRoute.CategoriaDetail(it)) },
                onSnippetClick = { navController.navigate(DevVaultRoute.SnippetDetail(it)) } // NUEVO
            )
        }

        composable<DevVaultRoute.Categorias> {
            CategoriasRoute(
                onCategoriaClick = { navController.navigate(DevVaultRoute.CategoriaDetail(it)) }
            )
        }

        composable<DevVaultRoute.Agregar> {
            AddSnippetRoute(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategorias = {
                    navController.navigate(DevVaultRoute.Categorias) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable<DevVaultRoute.Favoritos> {
            FavoritosRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<DevVaultRoute.CategoriaDetail> {
            CategoriaDetailRoute(
                onBack = { navController.popBackStack() }
            )
        }

        composable<DevVaultRoute.SnippetDetail> {
            SnippetDetailRoute(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}