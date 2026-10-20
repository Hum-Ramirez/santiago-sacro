package cl.duoc.santiagosacro.data.repository

import cl.duoc.santiagosacro.core.Geo
import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.data.local.EventoDao
import cl.duoc.santiagosacro.data.local.HorarioDao
import cl.duoc.santiagosacro.data.local.RecintoDao
import cl.duoc.santiagosacro.data.local.Sembrador
import cl.duoc.santiagosacro.data.remote.dto.RecintoDetalle
import cl.duoc.santiagosacro.data.remote.dto.RecintoResumen

/**
 * Unica fuente de datos de recintos para la interfaz.
 *
 * En esta entrega la aplicacion no se conecta al backend, asi que lee de la
 * base local del telefono. La firma de los metodos no cambio respecto de la
 * version que consultaba la API: siguen devolviendo los mismos DTO, porque
 * ni los ViewModels ni las pantallas tienen que saber de donde salen los
 * datos. Eso es lo que compra la capa de repositorios.
 *
 * Cada metodo publico llama primero al sembrador. Es barato (una consulta
 * COUNT) y garantiza que nadie pueda leer una tabla vacia: sembrar y leer
 * ocurren en la misma secuencia.
 */
class RecintoRepositorio(
    private val dao: RecintoDao,
    private val horarioDao: HorarioDao,
    private val eventoDao: EventoDao,
    private val sembrador: Sembrador
) {

    /** Listado completo, o filtrado por nombre si viene texto de busqueda. */
    suspend fun listar(texto: String? = null): Resultado<List<RecintoResumen>> {
        sembrador.sembrarSiHaceFalta()

        val filas = if (texto.isNullOrBlank()) {
            dao.listarTodos()
        } else {
            dao.buscarPorNombre(texto.trim())
        }

        return Resultado.Exito(filas.map { it.aResumen() })
    }

    /**
     * Recintos dentro del radio indicado, ordenados del mas cercano al mas
     * lejano. El filtro por distancia lo hacia el backend con la formula de
     * Haversine; aca lo hace el mismo calculo, en Geo.distanciaKm.
     */
    suspend fun cercanos(
        latitud: Double,
        longitud: Double,
        radioKm: Double = 5.0
    ): Resultado<List<RecintoResumen>> {
        sembrador.sembrarSiHaceFalta()

        val ordenados = dao.listarTodos()
            .map { fila ->
                val distancia = Geo.distanciaKm(latitud, longitud, fila.latitud, fila.longitud)
                fila.aResumen(distanciaKm = distancia)
            }
            .filter { resumen -> (resumen.distanciaKm ?: Double.MAX_VALUE) <= radioKm }
            .sortedBy { resumen -> resumen.distanciaKm }

        return Resultado.Exito(ordenados)
    }

    /**
     * Ficha completa de un recinto: sus datos, su horario regular y sus
     * eventos proximos, armados desde tres tablas locales.
     *
     * La galeria y los bloques excepcionales van vacios porque no existen
     * como tablas locales; la ficha ya contempla ese caso y no los dibuja.
     */
    suspend fun detalle(id: Long): Resultado<RecintoDetalle> {
        sembrador.sembrarSiHaceFalta()

        val fila = dao.porId(id)
            ?: return Resultado.Error("No se encontro el recinto solicitado")

        return Resultado.Exito(
            RecintoDetalle(
                id = fila.idRecinto,
                nombreOficial = fila.nombre,
                tipo = fila.tipo,
                comuna = fila.comuna,
                direccion = fila.direccion,
                latitud = fila.latitud,
                longitud = fila.longitud,
                descripcionHistorica = fila.descripcion,
                estadoApertura = fila.estadoApertura,
                horarios = horarioDao.porRecinto(id).map { it.aRespuesta() },
                bloquesExcepcionales = emptyList(),
                galeria = emptyList(),
                eventosProximos = eventoDao.porRecinto(id).map { it.aRespuesta() }
            )
        )
    }

    /** Flujo observable de la tabla local, usado por la pantalla de favoritos. */
    fun observarCache() = dao.observarTodos()

    suspend fun cacheVacio(): Boolean = dao.cantidad() == 0
}
