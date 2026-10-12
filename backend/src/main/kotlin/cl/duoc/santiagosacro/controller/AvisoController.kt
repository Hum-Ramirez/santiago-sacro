package cl.duoc.santiagosacro.controller

import cl.duoc.santiagosacro.dto.AvisoRequest
import cl.duoc.santiagosacro.dto.AvisoResponse
import cl.duoc.santiagosacro.service.AvisoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
class AvisoController(
    private val avisoService: AvisoService
) {

    /** Publico: avisos vigentes para pintar el mapa. */
    @GetMapping("/api/avisos/vigentes")
    fun vigentes(): ResponseEntity<List<AvisoResponse>> =
        ResponseEntity.ok(avisoService.vigentes())

    /** Encargado: levanta un aviso en su recinto. */
    @PostMapping("/api/recintos/{id}/avisos")
    fun crear(
        @PathVariable id: Long,
        @Valid @RequestBody peticion: AvisoRequest,
        @AuthenticationPrincipal usuario: UserDetails
    ): ResponseEntity<AvisoResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(avisoService.crear(id, usuario.username, peticion))
}
