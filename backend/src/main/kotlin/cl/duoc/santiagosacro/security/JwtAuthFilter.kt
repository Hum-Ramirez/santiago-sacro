package cl.duoc.santiagosacro.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Se ejecuta una vez por peticion: lee el token, lo valida y deja
 * al usuario autenticado en el contexto de seguridad.
 */
@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val usuarioDetailsService: UsuarioDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val cabecera = request.getHeader("Authorization")

        if (cabecera == null || !cabecera.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = cabecera.substring(7)

        try {
            val correo = jwtService.extraerCorreo(token)

            if (SecurityContextHolder.getContext().authentication == null) {
                val detalles = usuarioDetailsService.loadUserByUsername(correo)

                if (jwtService.esValido(token, detalles.username)) {
                    val autenticacion = UsernamePasswordAuthenticationToken(
                        detalles,
                        null,
                        detalles.authorities
                    )
                    autenticacion.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = autenticacion
                }
            }
        } catch (e: Exception) {
            // Token invalido o expirado: la peticion sigue como anonima
            // y Spring Security respondera 401 si el endpoint lo requiere.
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }
}
