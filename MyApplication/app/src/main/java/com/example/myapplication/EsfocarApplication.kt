package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.AppContainer

/** Crea una única vez los objetos compartidos de la app (BD, sesión, repositorios). */
class EsfocarApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
