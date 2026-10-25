package cl.duoc.santiagosacro.data.repository

import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.core.SesionLocal
import cl.duoc.santiagosacro.data.local.SolicitudDao
import cl.duoc.santiagosacro.data.local.SolicitudLocal
import cl.duoc.santiagosacro.data.remote.dto.SolicitudRespuesta

/**
 * Solicitudes de guia voluntario, guardadas en la base local.
 *
 * Es el unico lugar de la aplicacion donde el visitante escribe datos que
 * no son suyos solamente: una solicitud la crea el visitante y la responde
 * el encargado del recinto. Por eso exige sesion iniciada, igual que el
 * endpoint POST /api/recintos/{id}/solicitudes-guia del backend, que esta
 * protegido por el filtro JWT.
 */
class SolicitudRepositorio(
    private val solicitudDao: SolicitudDao,
    private val sesion: SesionLocal
) {

    /** Crea una solicitud en estado PENDIENTE a nombre del usuario de la sesion. */
    suspend fun solicitar(
        idRecinto: Long,
        nombreRecinto: String,
        fechaVisita: String,
        horaVisita: String,
        tamanoGrupo: Int,
        contacto: String
    ): Resultado<Unit> {

        val correo = sesion.correo
            ?: return Resultado.Error("Debe iniciar sesion para solicitar un guia")

        if (fechaVisita.isBlank() || horaVisita.isBlank()) {
            return Resultado.Error("Indique la fecha y la hora de la visita")
        }

        if (tamanoGrupo <= 0) {
            return Resultado.Error("El grupo debe tener al menos una persona")
        }

        if (contacto.isBlank()) {
            return Resultado.Error("Indique un telefono o correo de contacto")
        }

        solicitudDao.insertar(
            SolicitudLocal(
                idRecinto = idRecinto,
                nombreRecinto = nombreRecinto,
                correoSolicitante = correo,
                fechaVisita = fechaVisita.trim(),
                horaVisita = horaVisita.trim(),
                tamanoGrupo = tamanoGrupo,
                contacto = contacto.trim(),
                estado = PENDIENTE,
                fechaSolicitud = System.currentTimeMillis()
            )
        )

        return Resultado.Exito(Unit)
    }

    /** Las solicitudes del usuario de la sesion, con su estado actual. */
    suspend fun misSolicitudes(): Resultado<List<SolicitudRespuesta>> {
        val correo = sesion.correo
            ?: return Resultado.Error("Debe iniciar sesion para ver sus solicitudes")

        return Resultado.Exito(
            solicitudDao.porSolicitante(correo).map { it.aRespuesta() }
        )
    }

    fun haySesion(): Boolean = sesion.haySesion

    companion object {
        const val PENDIENTE = "PENDIENTE"
        const val ACEPTADA = "ACEPTADA"
        const val RECHAZADA = "RECHAZADA"
    }
}

/** Traduce la fila local al mismo DTO que devolvia el backend. */
internal fun SolicitudLocal.aRespuesta() = SolicitudRespuesta(
    id = id,
    idRecinto = idRecinto,
    nombreRecinto = nombreRecinto,
    fechaVisita = fechaVisita,
    horaVisita = horaVisita,
    tamanoGrupo = tamanoGrupo,
    contacto = contacto,
    estado = estado,
    guiaAsignado = guiaAsignado,
    motivoRechazo = motivoRechazo
)
