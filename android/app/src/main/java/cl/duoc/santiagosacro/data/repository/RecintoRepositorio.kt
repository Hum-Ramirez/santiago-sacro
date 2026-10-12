package cl.duoc.santiagosacro.data.repository

import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.core.llamadaSegura
import cl.duoc.santiagosacro.data.local.RecintoDao
import cl.duoc.santiagosacro.data.local.RecintoLocal
import cl.duoc.santiagosacro.data.remote.ApiSantiagoSacro
import cl.duoc.santiagosacro.data.remote.dto.RecintoDetalle
import cl.duoc.santiagosacro.data.remote.dto.RecintoResumen

/**
 * Unica fuente de datos de recintos para la interfaz.
 * Pide al backend y, si responde, guarda la copia en Room.
 * Si no hay conexion, devuelve lo ultimo sincronizado.
 */
class RecintoRepositorio(
    private val api: ApiSantiagoSacro,
    private val dao: RecintoDao
) {

    suspend fun listar(texto: String? = null): Resultado<List<RecintoResumen>> {
        val remoto = llamadaSegura { api.listarRecintos(texto = texto) }

        if (remoto is Resultado.Exito) {
            dao.guardarTodos(remoto.datos.map { it.aLocal() })
            return remoto
        }

        val enCache = dao.cantidad() > 0
        return if (enCache) {
            Resultado.Error("Sin conexion: se muestra la informacion guardada en el telefono")
        } else {
            remoto
        }
    }

    suspend fun cercanos(latitud: Double, longitud: Double, radioKm: Double = 5.0):
        Resultado<List<RecintoResumen>> {
        val remoto = llamadaSegura { api.recintosCercanos(latitud, longitud, radioKm) }
        if (remoto is Resultado.Exito) {
            dao.guardarTodos(remoto.datos.map { it.aLocal() })
        }
        return remoto
    }

    suspend fun detalle(id: Long): Resultado<RecintoDetalle> = llamadaSegura { api.detalleRecinto(id) }

    /** Copia local, para funcionar sin conexion. */
    fun observarCache() = dao.observarTodos()

    suspend fun cacheVacio(): Boolean = dao.cantidad() == 0

    private fun RecintoResumen.aLocal() = RecintoLocal(
        idRecinto = id,
        nombre = nombreOficial,
        tipo = tipo,
        comuna = comuna,
        direccion = "",
        latitud = latitud,
        longitud = longitud,
        estadoApertura = estadoApertura,
        descripcion = "",
        fechaSync = System.currentTimeMillis()
    )
}
