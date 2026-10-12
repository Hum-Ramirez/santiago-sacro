package cl.duoc.santiagosacro.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey

/**
 * Genera y valida los tokens JWT.
 * El token viaja en la cabecera Authorization con el prefijo Bearer.
 */
@Service
class JwtService(
    @Value("\${santiagosacro.jwt.secreto}") private val secreto: String,
    @Value("\${santiagosacro.jwt.expiracion-ms}") val expiracionMs: Long
) {

    private val clave: SecretKey by lazy {
        Keys.hmacShaKeyFor(secreto.toByteArray(StandardCharsets.UTF_8))
    }

    fun generarToken(correo: String, rol: String): String {
        val ahora = Date()
        val vence = Date(ahora.time + expiracionMs)
        return Jwts.builder()
            .subject(correo)
            .claim("rol", rol)
            .issuedAt(ahora)
            .expiration(vence)
            .signWith(clave)
            .compact()
    }

    fun extraerCorreo(token: String): String = leerClaims(token).subject

    fun extraerRol(token: String): String = leerClaims(token).get("rol", String::class.java)

    fun esValido(token: String, correoEsperado: String): Boolean = try {
        val claims = leerClaims(token)
        claims.subject == correoEsperado && claims.expiration.after(Date())
    } catch (e: Exception) {
        false
    }

    private fun leerClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(clave)
            .build()
            .parseSignedClaims(token)
            .payload
}
