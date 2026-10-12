package cl.duoc.santiagosacro.controller

import cl.duoc.santiagosacro.dto.*
import cl.duoc.santiagosacro.service.AuthService
import cl.duoc.santiagosacro.service.SolicitudService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
class EncargadoController(
    private val authService: AuthService,
    private val solicitudService: SolicitudService
) {

    /** Recintos que administra el encargado conectado. */
    @GetMapping("/api/encargado/mis-recintos")
    fun misRecintos(
        @AuthenticationPrincipal usuario: UserDetails
    ): ResponseEntity<List<RecintoResumenResponse>> =
        ResponseEntity.ok(authService.perfil(usuario.username).recintosAdministrados)

    /** Bandeja de solicitudes de guia. */
    @GetMapping("/api/encargado/solicitudes")
    fun solicitudes(
        @AuthenticationPrincipal usuario: UserDetails
    ): ResponseEntity<List<SolicitudResponse>> =
        ResponseEntity.ok(solicitudService.bandejaEncargado(usuario.username))

    /** Nomina de guias de un recinto administrado. */
    @GetMapping("/api/encargado/recintos/{id}/guias")
    fun guias(@PathVariable id: Long): ResponseEntity<List<GuiaResponse>> =
        ResponseEntity.ok(solicitudService.guiasDelRecinto(id))

    /** Aceptar o rechazar una solicitud. */
    @PatchMapping("/api/solicitudes/{id}")
    fun gestionar(
        @PathVariable id: Long,
        @Valid @RequestBody peticion: GestionSolicitudRequest,
        @AuthenticationPrincipal usuario: UserDetails
    ): ResponseEntity<SolicitudResponse> =
        ResponseEntity.ok(solicitudService.gestionar(id, usuario.username, peticion))

    /** Solicitudes del visitante conectado. */
    @GetMapping("/api/solicitudes/mias")
    fun mias(
        @AuthenticationPrincipal usuario: UserDetails
    ): ResponseEntity<List<SolicitudResponse>> =
        ResponseEntity.ok(solicitudService.misSolicitudes(usuario.username))
}
