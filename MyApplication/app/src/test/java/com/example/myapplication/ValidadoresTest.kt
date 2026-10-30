package com.example.myapplication

import com.example.myapplication.util.Formatos
import com.example.myapplication.util.Validadores
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadoresTest {

    @Test
    fun correo_valido_no_da_error() {
        assertNull(Validadores.validarCorreo("alumno.prueba@correo.cl"))
    }

    @Test
    fun correo_invalido_da_error() {
        assertNotNull(Validadores.validarCorreo(""))
        assertNotNull(Validadores.validarCorreo("sin-arroba"))
        assertNotNull(Validadores.validarCorreo("a@b"))
    }

    @Test
    fun contrasena_debe_tener_8_caracteres_letra_y_numero() {
        assertNotNull(Validadores.validarContrasena("abc123"))
        assertNotNull(Validadores.validarContrasena("soloLetras"))
        assertNotNull(Validadores.validarContrasena("12345678"))
        assertNull(Validadores.validarContrasena("clave1234"))
    }

    @Test
    fun confirmacion_debe_coincidir() {
        assertNotNull(Validadores.validarConfirmacion("clave1234", "clave12345"))
        assertNull(Validadores.validarConfirmacion("clave1234", "clave1234"))
    }

    @Test
    fun nombre_solo_letras_y_largo_minimo() {
        assertNotNull(Validadores.validarNombre("ab"))
        assertNotNull(Validadores.validarNombre("Juan123"))
        assertNull(Validadores.validarNombre("María José"))
    }

    @Test
    fun nombre_documento_requerido() {
        assertNotNull(Validadores.validarNombreDocumento("   "))
        assertNull(Validadores.validarNombreDocumento("Apuntes unidad 1"))
    }

    @Test
    fun fuerza_aumenta_con_mejores_contrasenas() {
        assertEquals(0f, Validadores.fuerzaContrasena(""), 0.001f)
        assertTrue(Validadores.fuerzaContrasena("Clave12345678") > Validadores.fuerzaContrasena("abc"))
    }

    @Test
    fun formato_de_tamano() {
        assertEquals("500 B", Formatos.tamano(500))
    }
}
