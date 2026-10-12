package cl.duoc.santiagosacro.service

import cl.duoc.santiagosacro.domain.Rol
import cl.duoc.santiagosacro.domain.Usuario
import cl.duoc.santiagosacro.dto.*
import cl.duoc.santiagosacro.exception.RecursoNoEncontradoException
import cl.duoc.santiagosacro.exception.ReglaNegocioException
import cl.duoc.santiagosacro.repository.RolRepository
import cl.duoc.santiagosacro.repository.UsuarioRepository
import cl.duoc.santiagosacro.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val usuarioRepository: UsuarioRepository,
    private val rolRepository: RolRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    /** Registro publico: siempre crea usuarios con rol VISITANTE. */
    @Transactional
    fun registrar(peticion: RegistroRequest): AuthResponse {
        if (usuarioRepository.existsByCorreoIgnoreCase(peticion.correo)) {
            throw ReglaNegocioException("Ya existe una cuenta registrada con ese correo")
        }

        val rolVisitante = rolRepository.findByNombre(Rol.VISITANTE)
            ?: throw RecursoNoEncontradoException("No esta configurado el rol VISITANTE")

        val usuario = Usuario(
            correo = peticion.correo.trim().lowercase(),
            claveHash = passwordEncoder.encode(peticion.clave),
            nombre = peticion.nombre.trim(),
            rol = rolVisitante
        )

        val guardado = usuarioRepository.save(usuario)
        return construirRespuesta(guardado)
    }

    /** Login: delega la validacion de la clave en Spring Security. */
    @Transactional(readOnly = true)
    fun login(peticion: LoginRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(peticion.correo, peticion.clave)
        )

        val usuario = usuarioRepository.findByCorreoIgnoreCase(peticion.correo)
            ?: throw RecursoNoEncontradoException("Usuario no encontrado")

        return construirRespuesta(usuario)
    }

    @Transactional(readOnly = true)
    fun perfil(correo: String): PerfilResponse {
        val usuario = buscarPorCorreo(correo)
        return PerfilResponse(
            id = usuario.id ?: 0,
            correo = usuario.correo,
            nombre = usuario.nombre,
            rol = usuario.rol?.nombre ?: Rol.VISITANTE,
            recintosAdministrados = usuario.recintos.map { it.aResumen() }
        )
    }

    @Transactional(readOnly = true)
    fun buscarPorCorreo(correo: String): Usuario =
        usuarioRepository.findByCorreoIgnoreCase(correo)
            ?: throw RecursoNoEncontradoException("Usuario no encontrado")

    private fun construirRespuesta(usuario: Usuario): AuthResponse {
        val rol = usuario.rol?.nombre ?: Rol.VISITANTE
        return AuthResponse(
            token = jwtService.generarToken(usuario.correo, rol),
            correo = usuario.correo,
            nombre = usuario.nombre,
            rol = rol,
            expiraEnMs = jwtService.expiracionMs
        )
    }
}
