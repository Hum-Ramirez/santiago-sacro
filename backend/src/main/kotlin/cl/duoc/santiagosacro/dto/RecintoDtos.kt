package cl.duoc.santiagosacro.dto

import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalTime

data class RecintoResumenResponse(
    val id: Long,
    val nombreOficial: String,
    val tipo: String,
    val comuna: String,
    val latitud: Double,
    val longitud: Double,
    val estadoApertura: String,
    val imagenPrincipal: String?,
    val distanciaKm: Double? = null
)

data class RecintoDetalleResponse(
    val id: Long,
    val nombreOficial: String,
    val tipo: String,
    val comuna: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val descripcionHistorica: String,
    val estadoApertura: String,
    val horarios: List<HorarioResponse>,
    val bloquesExcepcionales: List<BloqueResponse>,
    val galeria: List<RecursoResponse>,
    val eventosProximos: List<EventoResponse>
)

data class HorarioResponse(
    val diaSemana: Int,
    val nombreDia: String,
    val horaApertura: LocalTime,
    val horaCierre: LocalTime
)

data class BloqueResponse(
    val id: Long,
    val fecha: LocalDate,
    val horaInicio: LocalTime,
    val horaTermino: LocalTime,
    val motivo: String
)

data class RecursoResponse(
    val id: Long,
    val tipo: String,
    val url: String,
    val descripcion: String
)

data class CambioEstadoRequest(
    @field:NotNull(message = "El estado es obligatorio")
    val estado: String = ""
)

data class BloqueRequest(
    @field:NotNull(message = "La fecha es obligatoria")
    val fecha: LocalDate? = null,

    @field:NotNull(message = "La hora de inicio es obligatoria")
    val horaInicio: LocalTime? = null,

    @field:NotNull(message = "La hora de termino es obligatoria")
    val horaTermino: LocalTime? = null,

    val motivo: String = ""
)
