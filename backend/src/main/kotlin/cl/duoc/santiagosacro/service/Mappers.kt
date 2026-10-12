package cl.duoc.santiagosacro.service

import cl.duoc.santiagosacro.domain.*
import cl.duoc.santiagosacro.dto.*
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

/**
 * Conversiones de entidad a DTO.
 * Se separan para que los controladores nunca expongan entidades JPA.
 */

fun Recinto.aResumen(distanciaKm: Double? = null) = RecintoResumenResponse(
    id = this.id ?: 0,
    nombreOficial = this.nombreOficial,
    tipo = this.tipo?.nombre ?: "",
    comuna = this.comuna,
    latitud = this.latitud,
    longitud = this.longitud,
    estadoApertura = this.estadoApertura.name,
    imagenPrincipal = this.recursos.minByOrNull { it.orden }?.url,
    distanciaKm = distanciaKm
)

fun HorarioRegular.aDto(): HorarioResponse {
    val dia = DayOfWeek.of(this.diaSemana)
    return HorarioResponse(
        diaSemana = this.diaSemana,
        nombreDia = dia.getDisplayName(TextStyle.FULL, Locale("es", "CL")),
        horaApertura = this.horaApertura,
        horaCierre = this.horaCierre
    )
}

fun BloqueExcepcional.aDto() = BloqueResponse(
    id = this.id ?: 0,
    fecha = this.fecha,
    horaInicio = this.horaInicio,
    horaTermino = this.horaTermino,
    motivo = this.motivo
)

fun RecursoMultimedia.aDto() = RecursoResponse(
    id = this.id ?: 0,
    tipo = this.tipo,
    url = this.url,
    descripcion = this.descripcion
)

fun Evento.aDto() = EventoResponse(
    id = this.id ?: 0,
    titulo = this.titulo,
    descripcion = this.descripcion,
    categoria = this.categoria?.nombre ?: "",
    fechaInicio = this.fechaInicio,
    horaInicio = this.horaInicio,
    estado = this.estado.name,
    idRecinto = this.recinto?.id ?: 0,
    nombreRecinto = this.recinto?.nombreOficial ?: "",
    latitud = this.recinto?.latitud ?: 0.0,
    longitud = this.recinto?.longitud ?: 0.0
)

fun Aviso.aDto() = AvisoResponse(
    id = this.id ?: 0,
    texto = this.texto,
    vigenciaDesde = this.vigenciaDesde,
    vigenciaHasta = this.vigenciaHasta,
    idRecinto = this.recinto?.id ?: 0,
    nombreRecinto = this.recinto?.nombreOficial ?: "",
    latitud = this.recinto?.latitud ?: 0.0,
    longitud = this.recinto?.longitud ?: 0.0
)

fun SolicitudGuia.aDto() = SolicitudResponse(
    id = this.id ?: 0,
    idRecinto = this.recinto?.id ?: 0,
    nombreRecinto = this.recinto?.nombreOficial ?: "",
    fechaVisita = this.fechaVisita,
    horaVisita = this.horaVisita,
    tamanoGrupo = this.tamanoGrupo,
    contacto = this.contacto,
    estado = this.estado?.nombre ?: "",
    guiaAsignado = this.guia?.nombre,
    motivoRechazo = this.motivoRechazo
)

fun GuiaVoluntario.aDto() = GuiaResponse(
    id = this.id ?: 0,
    nombre = this.nombre,
    idioma = this.idioma,
    disponible = this.disponible
)
