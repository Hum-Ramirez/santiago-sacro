package cl.duoc.santiagosacro.data.repository

import cl.duoc.santiagosacro.data.local.EventoLocal
import cl.duoc.santiagosacro.data.local.HorarioLocal
import cl.duoc.santiagosacro.data.local.RecintoLocal
import cl.duoc.santiagosacro.data.remote.dto.EventoRespuesta
import cl.duoc.santiagosacro.data.remote.dto.HorarioRespuesta
import cl.duoc.santiagosacro.data.remote.dto.RecintoResumen

/**
 * Traduce las filas de Room a los mismos DTO que antes llegaban del backend.
 *
 * Esta es la pieza que permite cambiar la fuente de datos sin tocar ni los
 * ViewModels ni las pantallas: el contrato de los repositorios sigue siendo
 * el DTO, y solo cambia de donde se construye.
 *
 * Algunos campos del DTO quedan vacios porque la copia local no los guarda
 * (la galeria de imagenes, la descripcion larga de un evento). Se diseno asi
 * a proposito: la tabla local almacena lo que las pantallas leen, no todo lo
 * que el backend es capaz de entregar.
 */

internal fun RecintoLocal.aResumen(distanciaKm: Double? = null) = RecintoResumen(
    id = idRecinto,
    nombreOficial = nombre,
    tipo = tipo,
    comuna = comuna,
    latitud = latitud,
    longitud = longitud,
    estadoApertura = estadoApertura,
    imagenPrincipal = null,
    distanciaKm = distanciaKm
)

internal fun HorarioLocal.aRespuesta() = HorarioRespuesta(
    diaSemana = diaSemana,
    nombreDia = nombreDia,
    horaApertura = horaApertura,
    horaCierre = horaCierre
)

internal fun EventoLocal.aRespuesta() = EventoRespuesta(
    id = id,
    titulo = titulo,
    descripcion = "",
    categoria = categoria,
    fechaInicio = fechaInicio,
    horaInicio = horaInicio,
    estado = "PUBLICADO",
    idRecinto = idRecinto,
    nombreRecinto = nombreRecinto,
    latitud = 0.0,
    longitud = 0.0
)
