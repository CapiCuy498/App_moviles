package com.example.myapplication.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class Modulo(
    val titulo: String,
    val descripcion: String,
    val disponible: Boolean
)

private val modulos = listOf(
    Modulo("Mis documentos", "Sube y consulta tus apuntes en PDF o Word, incluso sin internet.", true),
    Modulo("Trivias y autoevaluación", "Preguntas con retroalimentación inmediata.", false),
    Modulo("Análisis de casos", "Situaciones simuladas con alternativas de actuación.", false),
    Modulo("Rúbricas", "Consulta de rúbricas de evaluación.", false)
)

@Composable
fun InicioScreen(
    nombre: String,
    totalDocumentos: Int,
    onIrADocumentos: () -> Unit
) {
    // Aparición escalonada de las tarjetas
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "Hola, ${nombre.substringBefore(' ')}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Bienvenido/a a tu espacio de estudio y repaso.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // El número cambia con animación al subir o eliminar documentos
                    AnimatedContent(
                        targetState = totalDocumentos,
                        transitionSpec = {
                            (slideInVertically { it } + fadeIn()) togetherWith
                                (slideOutVertically { -it } + fadeOut())
                        },
                        label = "contadorDocs"
                    ) { total ->
                        Text(
                            "$total",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        if (totalDocumentos == 1) "  documento guardado" else "  documentos guardados",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Módulos", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        modulos.forEachIndexed { indice, modulo ->
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = indice * 120)) +
                    slideInVertically(tween(400, delayMillis = indice * 120)) { it / 3 }
            ) {
                ModuloCard(
                    modulo = modulo,
                    onClick = onIrADocumentos,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun ModuloCard(modulo: Modulo, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(
        onClick = onClick,
        enabled = modulo.disponible,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(modulo.titulo, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    modulo.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!modulo.disponible) {
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = { Text("Próximamente") },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
