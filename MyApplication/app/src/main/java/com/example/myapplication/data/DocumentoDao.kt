package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentoDao {
    @Insert
    suspend fun insertar(documento: Documento): Long

    @Query("SELECT * FROM documentos WHERE usuarioId = :usuarioId ORDER BY fechaCarga DESC")
    fun observarPorUsuario(usuarioId: Int): Flow<List<Documento>>

    @Delete
    suspend fun eliminar(documento: Documento)
}
