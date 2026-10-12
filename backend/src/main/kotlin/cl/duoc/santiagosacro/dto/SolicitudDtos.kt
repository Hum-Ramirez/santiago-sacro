package cl.duoc.santiagosacro.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalTime

data class SolicitudRequest(
    @field:NotNull(message = "La fecha de visita es obligatoria")
    val fechaVisita: LocalDate? = null,

    @field:NotNull(message = "La hora de visita es obligatoria")
    val horaVisita: LocalTime? = null,

    @field:Min(value = 1, message = "El grupo debe tener al menos una persona")
    val tamanoGrupo: Int = 1,

    @field:NotBlank(message = "El contacto es obligatorio")
    val contacto: String = ""
)

data class GestionSolicitudRequest(
    @field:NotNull(message = "Debe indicar si acepta o rechaza")
    val aceptar: Boolean? = null,
    val idGuia: Long? = null,
    val motivoRechazo: String? = null
)

data class SolicitudResponse(
    val id: Long,
    val idRecinto: Long,
    val nombreRecinto: String,
    val fechaVisita: LocalDate,
    val horaVisita: LocalTime,
    val tamanoGrupo: Int,
    val contacto: String,
    val estado: String,
    val guiaAsignado: String?,
    val motivoRechazo: String?
)

data class GuiaResponse(
    val id: Long,
    val nombre: String,
    val idioma: String,
    val disponible: Boolean
)
