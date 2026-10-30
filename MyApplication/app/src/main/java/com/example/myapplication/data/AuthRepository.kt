package com.example.myapplication.data

import android.database.sqlite.SQLiteConstraintException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

enum class ResultadoRegistro { EXITO, CORREO_EXISTENTE }

class AuthRepository(
    private val dao: UsuarioDao,
    private val session: SessionManager
) {
    /** Usuario con sesión activa (null si no hay sesión). */
    @OptIn(ExperimentalCoroutinesApi::class)
    val usuarioActual: Flow<Usuario?> = session.userIdFlow.flatMapLatest { id ->
        if (id == SessionManager.SIN_SESION) flowOf(null) else dao.observarPorId(id)
    }

    suspend fun registrar(nombre: String, correo: String, contrasena: String): ResultadoRegistro {
        val correoNormalizado = correo.trim().lowercase()
        if (dao.buscarPorCorreo(correoNormalizado) != null) return ResultadoRegistro.CORREO_EXISTENTE
        val salt = Seguridad.generarSalt()
        val hash = withContext(Dispatchers.Default) { Seguridad.hash(contrasena, salt) }
        return try {
            dao.insertar(
                Usuario(
                    nombre = nombre.trim(),
                    correo = correoNormalizado,
                    salt = salt,
                    contrasenaHash = hash
                )
            )
            ResultadoRegistro.EXITO
        } catch (e: SQLiteConstraintException) {
            ResultadoRegistro.CORREO_EXISTENTE
        }
    }

    /** @return true si las credenciales son correctas (y deja la sesión iniciada). */
    suspend fun iniciarSesion(correo: String, contrasena: String): Boolean {
        val usuario = dao.buscarPorCorreo(correo.trim().lowercase()) ?: return false
        val hash = withContext(Dispatchers.Default) { Seguridad.hash(contrasena, usuario.salt) }
        if (hash != usuario.contrasenaHash) return false
        session.iniciar(usuario.id)
        return true
    }

    fun cerrarSesion() = session.cerrar()
}
