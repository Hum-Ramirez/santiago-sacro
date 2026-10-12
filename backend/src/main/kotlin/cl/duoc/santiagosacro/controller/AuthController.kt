package cl.duoc.santiagosacro.controller

import cl.duoc.santiagosacro.dto.*
import cl.duoc.santiagosacro.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/registro")
    fun registro(@Valid @RequestBody peticion: RegistroRequest): ResponseEntity<AuthResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(peticion))

    @PostMapping("/login")
    fun login(@Valid @RequestBody peticion: LoginRequest): ResponseEntity<AuthResponse> =
        ResponseEntity.ok(authService.login(peticion))

    @GetMapping("/perfil")
    fun perfil(@AuthenticationPrincipal usuario: UserDetails): ResponseEntity<PerfilResponse> =
        ResponseEntity.ok(authService.perfil(usuario.username))
}
