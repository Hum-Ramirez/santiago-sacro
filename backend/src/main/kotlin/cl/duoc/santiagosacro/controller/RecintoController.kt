package cl.duoc.santiagosacro.controller

import cl.duoc.santiagosacro.dto.*
import cl.duoc.santiagosacro.service.EventoService
import cl.duoc.santiagosacro.service.RecintoService
import cl.duoc.santiagosacro.service.SolicitudService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recintos")
class RecintoController(
    private val recintoService: RecintoService,
    private val eventoService: EventoService,
    private val solicitudService: SolicitudService
) {

    /** Publico: listado con filtros opcionales. */
    @GetMapping
    fun listar(
        @RequestParam(required = false) q: String?,
        @RequestParam(required = false) comuna: String?,
        @RequestParam(required = false) estado: String?
    ): ResponseEntity<List<RecintoResumenResponse>> =
        ResponseEntity.ok(recintoService.listar(q, comuna, estado))

    /** Publico: recintos cercanos a la posicion GPS del telefono. */
    @GetMapping("/cercanos")
    fun cercanos(
        @RequestParam latitud: Double,
        @RequestParam longitud: Double,
        @RequestParam(defaultValue = "5.0") radioKm: Double
    ): ResponseEntity<List<RecintoResumenResponse>> =
        ResponseEntity.ok(recintoService.cercanos(latitud, longitud, radioKm))

    /** Publico: ficha patrimonial completa. */
    @GetMapping("/{id}")
    fun detalle(@PathVariable id: Long): ResponseEntity<RecintoDetalleResponse> =
        ResponseEntity.ok(recintoService.detalle(id))

    /** Encargado: cambia el estado de apertura. */
    @PatchMapping("/{id}/estado")
    fun cambiarEstado(
        @PathVariable id: Long,
        @Valid @RequestBody peticion: CambioEstadoRequest,
        @AuthenticationPrincipal usuario: UserDetails
    ): ResponseEntity<RecintoResumenResponse> =
        ResponseEntity.ok(recintoService.cambiarEstado(id, usuario.username, peticion))

    /** Encargado: registra un bloque horario excepcional. */
    @PostMapping("/{id}/bloques")
    fun registrarBloque(
        @PathVariable id: Long,
        @Valid @RequestBody peticion: BloqueRequest,
        @AuthenticationPrincipal usuario: UserDetails
    ): ResponseEntity<BloqueResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(recintoService.registrarBloque(id, usuario.username, peticion))

    /** Encargado: publica un evento en su recinto. */
    @PostMapping("/{id}/eventos")
    fun publicarEvento(
        @PathVariable id: Long,
        @Valid @RequestBody peticion: EventoRequest,
        @AuthenticationPrincipal usuario: UserDetails
    ): ResponseEntity<EventoResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(eventoService.crear(id, usuario.username, peticion))

    /** Visitante autenticado: solicita acompanamiento de guia. */
    @PostMapping("/{id}/solicitudes-guia")
    fun solicitarGuia(
        @PathVariable id: Long,
        @Valid @RequestBody peticion: SolicitudRequest,
        @AuthenticationPrincipal usuario: UserDetails
    ): ResponseEntity<SolicitudResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(solicitudService.crear(id, usuario.username, peticion))
}
