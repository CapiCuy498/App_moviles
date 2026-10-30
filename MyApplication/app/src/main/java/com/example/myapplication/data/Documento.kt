package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Documento (PDF/Word) cargado por un usuario. El archivo se copia al almacenamiento interno. */
@Entity(
    tableName = "documentos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioId")]
)
data class Documento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val nombre: String,        // nombre que el usuario le asigna
    val nombreArchivo: String, // nombre original del archivo
    val tipo: String,          // pdf | doc | docx
    val mimeType: String,
    val tamano: Long,
    val rutaInterna: String,   // ruta absoluta de la copia dentro de filesDir
    val fechaCarga: Long
)
