package com.example.myapplication.data

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Las contraseñas nunca se guardan en texto plano: se almacena salt + hash PBKDF2. */
object Seguridad {
    fun generarSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.toHex()
    }

    fun hash(contrasena: String, salt: String): String {
        val spec = PBEKeySpec(contrasena.toCharArray(), salt.hexToBytes(), 10_000, 256)
        val clave = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec)
        return clave.encoded.toHex()
    }

    private fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }
    private fun String.hexToBytes() = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
