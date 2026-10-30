package com.example.myapplication.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AuthRepository
import com.example.myapplication.data.ResultadoRegistro
import com.example.myapplication.data.Usuario
import com.example.myapplication.util.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val correo: String = "",
    val contrasena: String = "",
    val mostrarContrasena: Boolean = false,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorGeneral: String? = null,
    val mensajeExito: String? = null,
    val cargando: Boolean = false
)

data class RegistroState(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmacion: String = "",
    val aceptaTerminos: Boolean = false,
    val mostrarContrasena: Boolean = false,
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmacion: String? = null,
    val errorTerminos: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false
)

/** Gestiona el estado de los formularios de login/registro y la sesión del usuario. */
class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _login = MutableStateFlow(LoginState())
    val login: StateFlow<LoginState> = _login.asStateFlow()

    private val _registro = MutableStateFlow(RegistroState())
    val registro: StateFlow<RegistroState> = _registro.asStateFlow()

    val usuarioActual: StateFlow<Usuario?> =
        repo.usuarioActual.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // ---------- Login ----------
    fun onLoginCorreoChange(valor: String) =
        _login.update { it.copy(correo = valor, errorCorreo = null, errorGeneral = null) }

    fun onLoginContrasenaChange(valor: String) =
        _login.update { it.copy(contrasena = valor, errorContrasena = null, errorGeneral = null) }

    fun alternarVisibilidadLogin() =
        _login.update { it.copy(mostrarContrasena = !it.mostrarContrasena) }

    fun iniciarSesion(onExito: () -> Unit) {
        val s = _login.value
        if (s.cargando) return
        val errorCorreo = Validadores.validarCorreo(s.correo)
        val errorContrasena = if (s.contrasena.isEmpty()) "Ingresa tu contraseña" else null
        if (errorCorreo != null || errorContrasena != null) {
            _login.update { it.copy(errorCorreo = errorCorreo, errorContrasena = errorContrasena) }
            return
        }
        viewModelScope.launch {
            _login.update { it.copy(cargando = true, errorGeneral = null, mensajeExito = null) }
            val ok = repo.iniciarSesion(s.correo, s.contrasena)
            if (ok) {
                _login.value = LoginState()
                onExito()
            } else {
                _login.update {
                    it.copy(cargando = false, errorGeneral = "Correo o contraseña incorrectos")
                }
            }
        }
    }

    // ---------- Registro ----------
    fun onRegNombreChange(valor: String) =
        _registro.update { it.copy(nombre = valor, errorNombre = null, errorGeneral = null) }

    fun onRegCorreoChange(valor: String) =
        _registro.update { it.copy(correo = valor, errorCorreo = null, errorGeneral = null) }

    fun onRegContrasenaChange(valor: String) =
        _registro.update { it.copy(contrasena = valor, errorContrasena = null, errorGeneral = null) }

    fun onRegConfirmacionChange(valor: String) =
        _registro.update { it.copy(confirmacion = valor, errorConfirmacion = null, errorGeneral = null) }

    fun onRegTerminosChange(valor: Boolean) =
        _registro.update { it.copy(aceptaTerminos = valor, errorTerminos = null) }

    fun alternarVisibilidadRegistro() =
        _registro.update { it.copy(mostrarContrasena = !it.mostrarContrasena) }

    fun limpiarRegistro() {
        _registro.value = RegistroState()
    }

    fun registrar(onExito: () -> Unit) {
        val s = _registro.value
        if (s.cargando) return
        val eNombre = Validadores.validarNombre(s.nombre)
        val eCorreo = Validadores.validarCorreo(s.correo)
        val eContrasena = Validadores.validarContrasena(s.contrasena)
        val eConfirmacion = Validadores.validarConfirmacion(s.contrasena, s.confirmacion)
        val eTerminos = if (!s.aceptaTerminos) "Debes aceptar para continuar" else null
        if (listOf(eNombre, eCorreo, eContrasena, eConfirmacion, eTerminos).any { it != null }) {
            _registro.update {
                it.copy(
                    errorNombre = eNombre,
                    errorCorreo = eCorreo,
                    errorContrasena = eContrasena,
                    errorConfirmacion = eConfirmacion,
                    errorTerminos = eTerminos
                )
            }
            return
        }
        viewModelScope.launch {
            _registro.update { it.copy(cargando = true, errorGeneral = null) }
            when (repo.registrar(s.nombre, s.correo, s.contrasena)) {
                ResultadoRegistro.EXITO -> {
                    _login.value = LoginState(
                        correo = s.correo.trim().lowercase(),
                        mensajeExito = "Cuenta creada correctamente. Ahora inicia sesión."
                    )
                    _registro.value = RegistroState()
                    onExito()
                }
                ResultadoRegistro.CORREO_EXISTENTE -> _registro.update {
                    it.copy(cargando = false, errorCorreo = "Este correo ya está registrado")
                }
            }
        }
    }

    // ---------- Sesión ----------
    fun cerrarSesion() {
        repo.cerrarSesion()
        _login.value = LoginState()
    }
}
