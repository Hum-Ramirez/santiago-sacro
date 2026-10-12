package cl.duoc.santiagosacro.data.repository

import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.core.llamadaSegura
import cl.duoc.santiagosacro.data.remote.ApiSantiagoSacro
import cl.duoc.santiagosacro.data.remote.dto.*

class EncargadoRepositorio(
    private val api: ApiSantiagoSacro
) {

    suspend fun misRecintos(): Resultado<List<RecintoResumen>> =
        llamadaSegura { api.misRecintos() }

    suspend fun cambiarEstado(idRecinto: Long, estado: String): Resultado<RecintoResumen> =
        llamadaSegura { api.cambiarEstado(idRecinto, CambioEstadoPeticion(estado)) }

    suspend fun publicarEvento(
        idRecinto: Long,
        titulo: String,
        descripcion: String,
        idCategoria: Long,
        fecha: String,
        hora: String
    ): Resultado<EventoRespuesta> = llamadaSegura {
        api.publicarEvento(
            idRecinto,
            EventoPeticion(titulo, descripcion, idCategoria, fecha, hora)
        )
    }

    suspend fun solicitudes(): Resultado<List<SolicitudRespuesta>> =
        llamadaSegura { api.solicitudesDelEncargado() }

    suspend fun guias(idRecinto: Long): Resultado<List<GuiaRespuesta>> =
        llamadaSegura { api.guiasDelRecinto(idRecinto) }

    suspend fun gestionar(
        idSolicitud: Long,
        aceptar: Boolean,
        idGuia: Long? = null,
        motivo: String? = null
    ): Resultado<SolicitudRespuesta> = llamadaSegura {
        api.gestionarSolicitud(idSolicitud, GestionSolicitudPeticion(aceptar, idGuia, motivo))
    }
}
