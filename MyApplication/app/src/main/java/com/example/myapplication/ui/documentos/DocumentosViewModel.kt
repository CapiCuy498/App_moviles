package com.example.myapplication.ui.documentos

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ArchivoPendiente
import com.example.myapplication.data.Documento
import com.example.myapplication.data.DocumentoRepository
import com.example.myapplication.util.Validadores
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DocumentosUiState(
    val pendiente: ArchivoPendiente? = null,     // archivo elegido, esperando nombre
    val nombreEditado: String = "",
    val errorNombre: String? = null,
    val guardando: Boolean = false,
    val documentoAEliminar: Documento? = null    // pide confirmación antes de borrar
)

class DocumentosViewModel(
    private val repo: DocumentoRepository,
    private val usuarioId: Int
) : ViewModel() {

    val documentos: StateFlow<List<Documento>> = repo.observar(usuarioId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _ui = MutableStateFlow(DocumentosUiState())
    val ui: StateFlow<DocumentosUiState> = _ui.asStateFlow()

    private val _mensajes = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val mensajes: SharedFlow<String> = _mensajes.asSharedFlow()

    fun notificar(mensaje: String) {
        _mensajes.tryEmit(mensaje)
    }

    // ---------- Cargar ----------
    fun onArchivoSeleccionado(uri: Uri) {
        viewModelScope.launch {
            try {
                val archivo = repo.leerMetadatos(uri)
                _ui.update {
                    it.copy(
                        pendiente = archivo,
                        nombreEditado = archivo.nombreArchivo.substringBeforeLast('.'),
                        errorNombre = null
                    )
                }
            } catch (e: IllegalArgumentException) {
                notificar(e.message ?: "Archivo no válido")
            } catch (e: Exception) {
                notificar("No se pudo leer el archivo")
            }
        }
    }

    fun onNombreChange(valor: String) =
        _ui.update { it.copy(nombreEditado = valor, errorNombre = null) }

    fun cancelarCarga() = _ui.update {
        it.copy(pendiente = null, nombreEditado = "", errorNombre = null, guardando = false)
    }

    fun confirmarCarga() {
        val estado = _ui.value
        val archivo = estado.pendiente ?: return
        if (estado.guardando) return
        val error = Validadores.validarNombreDocumento(estado.nombreEditado)
        if (error != null) {
            _ui.update { it.copy(errorNombre = error) }
            return
        }
        viewModelScope.launch {
            _ui.update { it.copy(guardando = true) }
            try {
                repo.guardar(usuarioId, archivo, estado.nombreEditado)
                cancelarCarga()
                notificar("Documento guardado")
            } catch (e: Exception) {
                _ui.update { it.copy(guardando = false) }
                notificar("No se pudo guardar el documento")
            }
        }
    }

    // ---------- Eliminar ----------
    fun pedirEliminar(documento: Documento) = _ui.update { it.copy(documentoAEliminar = documento) }

    fun cancelarEliminar() = _ui.update { it.copy(documentoAEliminar = null) }

    fun confirmarEliminar() {
        val doc = _ui.value.documentoAEliminar ?: return
        _ui.update { it.copy(documentoAEliminar = null) }
        viewModelScope.launch {
            try {
                repo.eliminar(doc)
                notificar("Documento eliminado")
            } catch (e: Exception) {
                notificar("No se pudo eliminar el documento")
            }
        }
    }
}
