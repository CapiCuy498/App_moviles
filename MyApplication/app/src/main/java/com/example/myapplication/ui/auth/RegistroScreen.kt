package com.example.myapplication.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.util.Validadores

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: AuthViewModel,
    onVolver: () -> Unit,
    onRegistroExitoso: () -> Unit
) {
    val state by viewModel.registro.collectAsStateWithLifecycle()
    val focus = LocalFocusManager.current

    // Barra de fortaleza de contraseña (animada)
    val fuerza by animateFloatAsState(
        targetValue = Validadores.fuerzaContrasena(state.contrasena),
        label = "fuerza"
    )
    val colorFuerza by animateColorAsState(
        targetValue = when {
            fuerza < 0.4f -> MaterialTheme.colorScheme.error
            fuerza < 0.75f -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        },
        label = "colorFuerza"
    )
    val etiquetaFuerza = when {
        fuerza < 0.4f -> "Débil"
        fuerza < 0.75f -> "Media"
        else -> "Fuerte"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                "Completa tus datos. Usa información de prueba (ficticia).",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.nombre,
                onValueChange = viewModel::onRegNombreChange,
                label = { Text("Nombre completo") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                singleLine = true,
                isError = state.errorNombre != null,
                supportingText = state.errorNombre?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = state.correo,
                onValueChange = viewModel::onRegCorreoChange,
                label = { Text("Correo") },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                singleLine = true,
                isError = state.errorCorreo != null,
                supportingText = state.errorCorreo?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = state.contrasena,
                onValueChange = viewModel::onRegContrasenaChange,
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                trailingIcon = {
                    TextButton(onClick = viewModel::alternarVisibilidadRegistro) {
                        Text(if (state.mostrarContrasena) "Ocultar" else "Ver")
                    }
                },
                singleLine = true,
                visualTransformation = if (state.mostrarContrasena) VisualTransformation.None
                else PasswordVisualTransformation(),
                isError = state.errorContrasena != null,
                supportingText = {
                    Text(state.errorContrasena ?: "Mínimo 8 caracteres, con letras y números")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(visible = state.contrasena.isNotEmpty()) {
                Column(Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
                    LinearProgressIndicator(
                        progress = { fuerza },
                        color = colorFuerza,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "Seguridad: $etiquetaFuerza",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorFuerza,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = state.confirmacion,
                onValueChange = viewModel::onRegConfirmacionChange,
                label = { Text("Repetir contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                singleLine = true,
                visualTransformation = if (state.mostrarContrasena) VisualTransformation.None
                else PasswordVisualTransformation(),
                isError = state.errorConfirmacion != null,
                supportingText = state.errorConfirmacion?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.aceptaTerminos,
                    onCheckedChange = viewModel::onRegTerminosChange
                )
                Text(
                    "Entiendo que es un MVP académico y que usaré solo datos ficticios.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            AnimatedVisibility(visible = state.errorTerminos != null) {
                Text(
                    state.errorTerminos ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    focus.clearFocus()
                    viewModel.registrar(onRegistroExitoso)
                },
                enabled = !state.cargando,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (state.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Registrarme")
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
