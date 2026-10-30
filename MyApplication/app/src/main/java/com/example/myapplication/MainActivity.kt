package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.data.SessionManager
import com.example.myapplication.ui.auth.AuthViewModel
import com.example.myapplication.ui.navigation.AppNavigation
import com.example.myapplication.ui.navigation.Rutas
import com.example.myapplication.ui.theme.EsfocarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as EsfocarApplication).container

        setContent {
            EsfocarTheme {
                val authViewModel: AuthViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { AuthViewModel(container.authRepository) }
                    }
                )
                // Si quedó una sesión guardada, se entra directo a la app
                val destinoInicial = remember {
                    if (container.session.userId != SessionManager.SIN_SESION) Rutas.MAIN else Rutas.LOGIN
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(authViewModel, destinoInicial)
                }
            }
        }
    }
}
