package com.example.myapplication.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatos {
    private val local = Locale("es", "CL")

    fun tamano(bytes: Long): String = when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> String.format(local, "%.1f KB", bytes / 1024.0)
        else -> String.format(local, "%.1f MB", bytes / (1024.0 * 1024.0))
    }

    fun fecha(millis: Long): String =
        SimpleDateFormat("dd/MM/yyyy HH:mm", local).format(Date(millis))

    fun fechaCorta(millis: Long): String =
        SimpleDateFormat("dd/MM/yyyy", local).format(Date(millis))
}
