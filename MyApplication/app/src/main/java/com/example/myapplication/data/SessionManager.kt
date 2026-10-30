package com.example.myapplication.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Guarda en SharedPreferences el id del usuario con sesión activa (la sesión sobrevive al cerrar la app). */
class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    private val _userId = MutableStateFlow(prefs.getInt(KEY_USER_ID, SIN_SESION))
    val userIdFlow: StateFlow<Int> = _userId.asStateFlow()
    val userId: Int get() = _userId.value

    fun iniciar(id: Int) {
        prefs.edit().putInt(KEY_USER_ID, id).apply()
        _userId.value = id
    }

    fun cerrar() {
        prefs.edit().remove(KEY_USER_ID).apply()
        _userId.value = SIN_SESION
    }

    companion object {
        const val SIN_SESION = -1
        private const val KEY_USER_ID = "user_id"
    }
}
