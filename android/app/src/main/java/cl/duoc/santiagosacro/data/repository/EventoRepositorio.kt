package cl.duoc.santiagosacro.data.repository

import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.data.local.EventoDao
import cl.duoc.santiagosacro.data.local.Sembrador
import cl.duoc.santiagosacro.data.remote.dto.AvisoRespuesta
import cl.duoc.santiagosacro.data.remote.dto.CategoriaRespuesta
import cl.duoc.santiagosacro.data.remote.dto.EventoRespuesta

/**
 * Cartelera y categorias, leidas de la base local.
 *
 * Igual que el repositorio de recintos, conserva las firmas que tenia cuando
 * consultaba la API: el CarteleraViewModel no cambio ni una linea.
 */
class EventoRepositorio(
    private val eventoDao: EventoDao,
    private val sembrador: Sembrador
) {

    /** Toda la cartelera, o solo una categoria si viene el filtro. */
    suspend fun cartelera(idCategoria: Long? = null): Resultado<List<EventoRespuesta>> {
        sembrador.sembrarSiHaceFalta()

        val filas = if (idCategoria == null) {
            eventoDao.listarTodos()
        } else {
            eventoDao.porCategoria(idCategoria)
        }

        return Resultado.Exito(filas.map { it.aRespuesta() })
    }

    /**
     * Las categorias para los chips de filtro.
     *
     * No hay tabla de categorias: se derivan de los eventos con un
     * SELECT DISTINCT. Tiene dos ventajas sobre guardarlas aparte: una tabla
     * menos que sincronizar, y los chips siempre calzan con el contenido
     * (nunca aparece un filtro que no tenga ningun evento detras).
     */
    suspend fun categorias(): Resultado<List<CategoriaRespuesta>> {
        sembrador.sembrarSiHaceFalta()

        val categorias = eventoDao.categorias().map { fila ->
            CategoriaRespuesta(id = fila.idCategoria, nombre = fila.categoria)
        }

        return Resultado.Exito(categorias)
    }

    /**
     * Los avisos son publicaciones vigentes del encargado y no tienen tabla
     * local: son informacion que cambia en el momento y cachearla mostraria
     * algo falso. Se devuelve una lista vacia, que el mapa ya contempla.
     *
     * Cuando se conecten las dos partes en la Unidad 3, este metodo vuelve a
     * consultar GET /api/avisos/vigentes.
     */
    suspend fun avisosVigentes(): Resultado<List<AvisoRespuesta>> =
        Resultado.Exito(emptyList<AvisoRespuesta>())
}
