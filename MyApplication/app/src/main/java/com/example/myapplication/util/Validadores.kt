package com.example.myapplication.util

/** Reglas de validación de formularios. Cada función devuelve el mensaje de error o null si es válido. */
object Validadores {
    private val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val REGEX_NOMBRE = Regex("^[\\p{L}][\\p{L} .'-]*$")

    fun validarNombre(nombre: String): String? {
        val n = nombre.trim()
        return when {
            n.isEmpty() -> "Ingresa tu nombre"
            n.length < 3 -> "Debe tener al menos 3 caracteres"
            n.length > 50 -> "Máximo 50 caracteres"
            !REGEX_NOMBRE.matches(n) -> "Solo letras y espacios"
            else -> null
        }
    }

    fun validarCorreo(correo: String): String? {
        val c = correo.trim()
        return when {
            c.isEmpty() -> "Ingresa tu correo"
            !REGEX_CORREO.matches(c) -> "Correo no válido (ej: nombre@correo.cl)"
            else -> null
        }
    }

    fun validarContrasena(contrasena: String): String? = when {
        contrasena.isEmpty() -> "Ingresa una contraseña"
        contrasena.length < 8 -> "Mínimo 8 caracteres"
        contrasena.none { it.isLetter() } -> "Debe incluir al menos una letra"
        contrasena.none { it.isDigit() } -> "Debe incluir al menos un número"
        else -> null
    }

    fun validarConfirmacion(contrasena: String, confirmacion: String): String? = when {
        confirmacion.isEmpty() -> "Repite la contraseña"
        contrasena != confirmacion -> "Las contraseñas no coinciden"
        else -> null
    }

    fun validarNombreDocumento(nombre: String): String? {
        val n = nombre.trim()
        return when {
            n.isEmpty() -> "Ingresa un nombre para el documento"
            n.length < 3 -> "Debe tener al menos 3 caracteres"
            n.length > 60 -> "Máximo 60 caracteres"
            else -> null
        }
    }

    /** Fortaleza de 0f a 1f (para la barra animada del registro). */
    fun fuerzaContrasena(contrasena: String): Float {
        if (contrasena.isEmpty()) return 0f
        var puntos = 0
        if (contrasena.length >= 8) puntos++
        if (contrasena.length >= 12) puntos++
        if (contrasena.any { it.isLetter() } && contrasena.any { it.isDigit() }) puntos++
        if (contrasena.any { it.isUpperCase() } && contrasena.any { it.isLowerCase() }) puntos++
        return puntos / 4f
    }
}
