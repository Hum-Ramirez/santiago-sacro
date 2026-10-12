package cl.duoc.santiagosacro.controller

import cl.duoc.santiagosacro.dto.EventoRequest
import cl.duoc.santiagosacro.dto.EventoResponse
import cl.duoc.santiagosacro.service.EventoService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/eventos")
class EventoController(
    private val eventoService: EventoService
) {

    /** Publico: cartelera cultural de la red. */
    @GetMapping
    fun cartelera(
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) desde: LocalDate?,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) hasta: LocalDate?,
        @RequestParam(required = false) idCategoria: Long?
    ): ResponseEntity<List<EventoResponse>> =
        ResponseEntity.ok(eventoService.cartelera(desde, hasta, idCategoria))

    @GetMapping("/{id}")
    fun detalle(@PathVariable id: Long): ResponseEntity<EventoResponse> =
        ResponseEntity.ok(eventoService.detalle(id))

    @PutMapping("/{id}")
    fun actualizar(
        @PathVariable id: Long,
        @Valid @RequestBody peticion: EventoRequest,
        @AuthenticationPrincipal usuario: UserDetails
    ): ResponseEntity<EventoResponse> =
        ResponseEntity.ok(eventoService.actualizar(id, usuario.username, peticion))

    @DeleteMapping("/{id}")
    fun cancelar(
        @PathVariable id: Long,
        @AuthenticationPrincipal usuario: UserDetails
    ): ResponseEntity<EventoResponse> =
        ResponseEntity.ok(eventoService.cancelar(id, usuario.username))
}
