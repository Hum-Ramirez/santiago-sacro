package cl.duoc.santiagosacro.security

import cl.duoc.santiagosacro.repository.UsuarioRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Traduce el Usuario de la base de datos al UserDetails que entiende Spring Security.
 * El rol se expone con el prefijo ROLE_ porque es lo que espera hasRole().
 */
@Service
class UsuarioDetailsService(
    private val usuarioRepository: UsuarioRepository
) : UserDetailsService {

    override fun loadUserByUsername(correo: String): UserDetails {
        val usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
            ?: throw UsernameNotFoundException("No existe un usuario con el correo " + correo)

        val autoridad = SimpleGrantedAuthority("ROLE_" + (usuario.rol?.nombre ?: "VISITANTE"))

        return User.builder()
            .username(usuario.correo)
            .password(usuario.claveHash)
            .authorities(listOf(autoridad))
            .disabled(!usuario.activo)
            .build()
    }
}
