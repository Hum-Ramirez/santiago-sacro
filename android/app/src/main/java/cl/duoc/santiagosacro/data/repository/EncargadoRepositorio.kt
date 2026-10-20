package cl.duoc.santiagosacro.data.repository

import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.core.SesionLocal
import cl.duoc.santiagosacro.data.local.EventoDao
import cl.duoc.santiagosacro.data.local.EventoLocal
import cl.duoc.santiagosacro.data.local.RecintoDao
import cl.duoc.santiagosacro.data.local.Sembrador
import cl.duoc.santiagosacro.data.local.UsuarioDao
import cl.duoc.santiagosacro.data.remote.dto.EventoRespuesta
import cl.duoc.santiagosacro.data.remote.dto.GuiaRespuesta
import cl.duoc.santiagosacro.data.remote.dto.RecintoResumen
import cl.duoc.santiagosacro.data.remote.dto.SolicitudRespuesta

/**
 * Panel del encargado, trabajando sobre la base local.
 *
 * Donde antes el backend sabia quien era el usuario por el JWT, aca se
 * identifica por el correo guardado en la sesion y se busca su fila en
 * usuario_local. De esa fila sale el recinto que administra, que es lo que
 * limita sobre que puede operar.
 */
class EncargadoRepositorio(
    private val recintoDao: RecintoDao,
    private val usuarioDao: UsuarioDao,
    private val eventoDao: EventoDao,
    private val sesion: SesionLocal,
    private val sembrador: Sembrador
) {

    /**
     * Los recintos del encargado de la sesion.
     *
     * En el backend un encargado puede administrar varios recintos, porque
     * la relacion usuario-recinto es N:M a traves de la tabla
     * usuario_recinto. Aca se simplifico a un campo nullable en
     * usuario_local, asi que la lista trae uno o ninguno.
     */
    suspend fun misRecintos(): Resultado<List<RecintoResumen>> {
        sembrador.sembrarSiHaceFalta()

        val idRecinto = recintoAdministrado()
            ?: return Resultado.Error("Su cuenta no tiene recintos asignados")

        val filas = recintoDao.porIds(listOf(idRecinto))
        return Resultado.Exito(filas.map { it.aResumen() })
    }

    /**
     * Cambia el estado de apertura de un recinto.
     *
     * Se usa un @Query con UPDATE en vez de la anotacion @Update porque solo
     * cambia una columna: @Update reescribe la fila completa a partir del
     * objeto, lo que obligaria a leerla antes para no perder los demas
     * campos. Despues del cambio se vuelve a leer la fila para devolver el
     * estado real que quedo guardado, no el que se pidio.
     *
     * La verificacion de que el encargado solo toque su propio recinto es la
     * misma idea del verificarQueAdministra del backend, pero resuelta aca.
     */
    suspend fun cambiarEstado(idRecinto: Long, estado: String): Resultado<RecintoResumen> {
        val propio = recintoAdministrado()
            ?: return Resultado.Error("Su cuenta no tiene recintos asignados")

        if (propio != idRecinto) {
            return Resultado.Error("No administra ese recinto")
        }

        recintoDao.cambiarEstado(idRecinto, estado)

        val fila = recintoDao.porId(idRecinto)
            ?: return Resultado.Error("No se encontro el recinto")

        return Resultado.Exito(fila.aResumen())
    }

    /**
     * Publica un evento nuevo en la cartelera local.
     *
     * El id no lo genera Room, porque evento_local conserva los ids del
     * servidor y su llave primaria no es autoGenerate. Se calcula a partir
     * del maximo actual, que es lo que hace el COALESCE(MAX(id), 0) del DAO.
     *
     * El nombre de la categoria se busca en la misma proyeccion DISTINCT que
     * alimenta los chips, para no duplicar en el codigo una lista que ya
     * existe en los datos.
     */
    suspend fun publicarEvento(
        idRecinto: Long,
        titulo: String,
        descripcion: String,
        idCategoria: Long,
        fecha: String,
        hora: String
    ): Resultado<EventoRespuesta> {
        val propio = recintoAdministrado()
            ?: return Resultado.Error("Su cuenta no tiene recintos asignados")

        if (propio != idRecinto) {
            return Resultado.Error("No administra ese recinto")
        }

        val recinto = recintoDao.porId(idRecinto)
            ?: return Resultado.Error("No se encontro el recinto")

        val nombreCategoria = eventoDao.categorias()
            .firstOrNull { it.idCategoria == idCategoria }
            ?.categoria
            ?: "Actividad"

        val nuevo = EventoLocal(
            id = eventoDao.ultimoId() + 1,
            titulo = titulo.trim(),
            fechaInicio = fecha,
            horaInicio = hora,
            idCategoria = idCategoria,
            categoria = nombreCategoria,
            idRecinto = idRecinto,
            nombreRecinto = recinto.nombre
        )

        eventoDao.insertar(nuevo)
        return Resultado.Exito(nuevo.aRespuesta())
    }

    /**
     * Las solicitudes de guia y los guias voluntarios no tienen tabla local.
     *
     * Es deliberado: la gestion de solicitudes es funcionalidad de la segunda
     * entrega y en el backend ya esta implementada y probada con Postman. Aca
     * se devuelve una lista vacia para que el panel se dibuje completo sin
     * inventar datos que no existen.
     */
    suspend fun solicitudes(): Resultado<List<SolicitudRespuesta>> =
        Resultado.Exito(emptyList<SolicitudRespuesta>())

    suspend fun guias(idRecinto: Long): Resultado<List<GuiaRespuesta>> =
        Resultado.Exito(emptyList<GuiaRespuesta>())

    suspend fun gestionar(
        idSolicitud: Long,
        aceptar: Boolean,
        idGuia: Long? = null,
        motivo: String? = null
    ): Resultado<SolicitudRespuesta> =
        Resultado.Error("La gestion de solicitudes requiere conexion con el servidor")

    private suspend fun recintoAdministrado(): Long? {
        val correo = sesion.correo ?: return null
        return usuarioDao.porCorreo(correo)?.idRecintoAdministrado
    }
}
