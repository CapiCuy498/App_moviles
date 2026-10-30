package com.example.myapplication.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID

/** Datos del archivo elegido por el usuario, antes de guardarlo. */
data class ArchivoPendiente(
    val uri: Uri,
    val nombreArchivo: String,
    val tipo: TipoDocumento,
    val tamano: Long
)

/**
 * Maneja los archivos (recurso nativo): el documento elegido con el selector del sistema
 * se COPIA al almacenamiento interno de la app, así se puede ver sin internet y se borra de verdad.
 */
class DocumentoRepository(
    private val context: Context,
    private val dao: DocumentoDao
) {
    fun observar(usuarioId: Int): Flow<List<Documento>> = dao.observarPorUsuario(usuarioId)

    /** Lee nombre, tamaño y tipo del archivo. Lanza IllegalArgumentException si no es válido. */
    suspend fun leerMetadatos(uri: Uri): ArchivoPendiente = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        var nombre = "documento"
        var tamano = 0L
        resolver.query(uri, null, null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                val iNombre = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val iTamano = c.getColumnIndex(OpenableColumns.SIZE)
                if (iNombre >= 0) nombre = c.getString(iNombre) ?: nombre
                if (iTamano >= 0 && !c.isNull(iTamano)) tamano = c.getLong(iTamano)
            }
        }
        val tipo = TipoDocumento.desdeExtension(nombre.substringAfterLast('.', ""))
            ?: TipoDocumento.desdeMime(resolver.getType(uri))
            ?: throw IllegalArgumentException("Formato no permitido. Solo PDF, DOC o DOCX.")
        require(tamano <= TAMANO_MAXIMO) { "El archivo supera el máximo de 20 MB." }
        ArchivoPendiente(uri, nombre, tipo, tamano)
    }

    suspend fun guardar(usuarioId: Int, archivo: ArchivoPendiente, nombre: String) {
        withContext(Dispatchers.IO) {
            val carpeta = File(context.filesDir, "documentos/$usuarioId").apply { mkdirs() }
            val destino = File(carpeta, "${UUID.randomUUID()}.${archivo.tipo.extension}")
            val entrada = context.contentResolver.openInputStream(archivo.uri)
                ?: throw IOException("No se pudo leer el archivo seleccionado.")
            entrada.use { input -> destino.outputStream().use { output -> input.copyTo(output) } }
            dao.insertar(
                Documento(
                    usuarioId = usuarioId,
                    nombre = nombre.trim(),
                    nombreArchivo = archivo.nombreArchivo,
                    tipo = archivo.tipo.extension,
                    mimeType = archivo.tipo.mimeType,
                    tamano = destino.length(),
                    rutaInterna = destino.absolutePath,
                    fechaCarga = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun eliminar(documento: Documento) {
        withContext(Dispatchers.IO) {
            File(documento.rutaInterna).delete()
            dao.eliminar(documento)
        }
    }

    companion object {
        const val TAMANO_MAXIMO = 20L * 1024 * 1024
    }
}
