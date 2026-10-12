package cl.duoc.santiagosacro.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalTime

data class EventoResponse(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val fechaInicio: LocalDate,
    val horaInicio: LocalTime,
    val estado: String,
    val idRecinto: Long,
    val nombreRecinto: String,
    val latitud: Double,
    val longitud: Double
)

data class EventoRequest(
    @field:NotBlank(message = "El titulo es obligatorio")
    val titulo: String = "",

    val descripcion: String = "",

    @field:NotNull(message = "La categoria es obligatoria")
    val idCategoria: Long? = null,

    @field:NotNull(message = "La fecha es obligatoria")
    val fechaInicio: LocalDate? = null,

    @field:NotNull(message = "La hora es obligatoria")
    val horaInicio: LocalTime? = null
)

data class CategoriaResponse(
    val id: Long,
    val nombre: String
)
