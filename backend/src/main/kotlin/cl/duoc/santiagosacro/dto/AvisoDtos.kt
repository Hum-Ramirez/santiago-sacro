package cl.duoc.santiagosacro.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class AvisoResponse(
    val id: Long,
    val texto: String,
    val vigenciaDesde: LocalDateTime,
    val vigenciaHasta: LocalDateTime,
    val idRecinto: Long,
    val nombreRecinto: String,
    val latitud: Double,
    val longitud: Double
)

data class AvisoRequest(
    @field:NotBlank(message = "El texto del aviso es obligatorio")
    val texto: String = "",

    @field:NotNull(message = "La vigencia es obligatoria")
    val vigenciaHasta: LocalDateTime? = null
)
