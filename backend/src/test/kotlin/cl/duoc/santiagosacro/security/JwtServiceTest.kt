package cl.duoc.santiagosacro.security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JwtServiceTest {

    private val servicio = JwtService(
        secreto = "clave-de-pruebas-unitarias-santiago-sacro-dsy1105-2026",
        expiracionMs = 60_000
    )

    @Test
    fun `el token generado conserva el correo y el rol`() {
        val token = servicio.generarToken("encargado@iglesiadesantiago.cl", "ENCARGADO")

        assertEquals("encargado@iglesiadesantiago.cl", servicio.extraerCorreo(token))
        assertEquals("ENCARGADO", servicio.extraerRol(token))
    }

    @Test
    fun `el token es valido para su propio dueno`() {
        val token = servicio.generarToken("visitante@correo.cl", "VISITANTE")

        assertTrue(servicio.esValido(token, "visitante@correo.cl"))
    }

    @Test
    fun `el token no sirve para otro usuario`() {
        val token = servicio.generarToken("visitante@correo.cl", "VISITANTE")

        assertFalse(servicio.esValido(token, "otro@correo.cl"))
    }

    @Test
    fun `un token adulterado se rechaza`() {
        val token = servicio.generarToken("visitante@correo.cl", "VISITANTE") + "xyz"

        assertFalse(servicio.esValido(token, "visitante@correo.cl"))
    }
}
