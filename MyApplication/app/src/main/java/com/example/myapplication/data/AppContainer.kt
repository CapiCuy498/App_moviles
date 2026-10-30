package com.example.myapplication.data

import android.content.Context

/** Contenedor simple de dependencias (evita librerías de inyección para mantener el MVP liviano). */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database by lazy { AppDatabase.getDatabase(appContext) }

    val session = SessionManager(appContext)
    val authRepository by lazy { AuthRepository(database.usuarioDao(), session) }
    val documentoRepository by lazy { DocumentoRepository(appContext, database.documentoDao()) }
}
