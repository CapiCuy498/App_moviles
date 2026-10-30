package com.example.myapplication.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.auth.AuthViewModel
import com.example.myapplication.ui.auth.LoginScreen
import com.example.myapplication.ui.auth.RegistroScreen
import com.example.myapplication.ui.main.MainScreen

@Composable
fun AppNavigation(authViewModel: AuthViewModel, destinoInicial: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = destinoInicial,
        enterTransition = { slideInHorizontally(tween(350), initialOffsetX = { it / 4 }) + fadeIn(tween(350)) },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(350)) },
        popExitTransition = { slideOutHorizontally(tween(300), targetOffsetX = { it / 4 }) + fadeOut(tween(300)) }
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onIrARegistro = { navController.navigate(Rutas.REGISTRO) { launchSingleTop = true } },
                onLoginExitoso = {
                    navController.navigate(Rutas.MAIN) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                viewModel = authViewModel,
                onVolver = {
                    authViewModel.limpiarRegistro()
                    navController.popBackStack()
                },
                onRegistroExitoso = { navController.popBackStack() }
            )
        }
        composable(Rutas.MAIN) {
            MainScreen(
                authViewModel = authViewModel,
                onSesionCerrada = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.MAIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
