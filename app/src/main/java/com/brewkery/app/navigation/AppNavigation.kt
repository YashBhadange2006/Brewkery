package com.brewkery.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.brewkery.app.ui.screens.CartScreen
import com.brewkery.app.ui.screens.HomeScreen
import com.brewkery.app.ui.screens.ItemDetailScreen
import com.brewkery.app.ui.screens.OrderStatusScreen
import com.brewkery.app.viewmodel.CartViewModel
import com.brewkery.app.viewmodel.MenuViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val menuViewModel: MenuViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(menuViewModel, cartViewModel,
                onOpenItem = { id -> navController.navigate("item/$id") },
                onOpenCart = { navController.navigate("cart") },
                onOpenOrder = { navController.navigate("order") })
        }
        composable("item/{itemId}", arguments = listOf(navArgument("itemId") { type = NavType.IntType })) { entry ->
            val id = entry.arguments?.getInt("itemId") ?: return@composable
            ItemDetailScreen(id, menuViewModel, cartViewModel,
                onBack = { navController.popBackStack() },
                onOpenCart = { navController.navigate("cart") { launchSingleTop = true } })
        }
        composable("cart") {
            CartScreen(menuViewModel, cartViewModel,
                onBack = { navController.popBackStack() },
                onPlaceOrder = {
                    navController.navigate("order") {
                        popUpTo("cart") { inclusive = true }
                        launchSingleTop = true
                    }
                })
        }
        composable("order") {
            OrderStatusScreen(cartViewModel, onBackToMenu = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
            })
        }
    }
}
