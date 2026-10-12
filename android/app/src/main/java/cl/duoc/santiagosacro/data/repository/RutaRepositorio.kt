package cl.duoc.santiagosacro.data.repository

import cl.duoc.santiagosacro.data.local.FavoritoDao
import cl.duoc.santiagosacro.data.local.ParadaRuta
import cl.duoc.santiagosacro.data.local.RutaDao
import cl.duoc.santiagosacro.data.local.Favorito
import kotlinx.coroutines.flow.Flow

/**
 * Rutas y favoritos viven solo en el telefono: son datos del visitante
 * y no se publican en la red patrimonial.
 */
class RutaRepositorio(
    private val rutaDao: RutaDao,
    private val favoritoDao: FavoritoDao
) {

    fun observarParadas(): Flow<List<ParadaRuta>> = rutaDao.observarParadas()

    suspend fun agregarParada(
        idRecinto: Long,
        nombre: String,
        latitud: Double,
        longitud: Double
    ): Boolean {
        if (rutaDao.yaEstaEnLaRuta(idRecinto)) return false
        val orden = rutaDao.ultimoOrden() + 1
        rutaDao.agregar(
            ParadaRuta(
                idRecinto = idRecinto,
                nombreRecinto = nombre,
                orden = orden,
                latitud = latitud,
                longitud = longitud
            )
        )
        return true
    }

    suspend fun quitarParada(id: Long) = rutaDao.quitar(id)

    suspend fun vaciarRuta() = rutaDao.vaciar()

    fun observarFavoritos(): Flow<List<Favorito>> = favoritoDao.observarFavoritos()

    fun esFavorito(idRecinto: Long): Flow<Boolean> = favoritoDao.esFavorito(idRecinto)

    suspend fun alternarFavorito(idRecinto: Long, actualmenteEsFavorito: Boolean) {
        if (actualmenteEsFavorito) {
            favoritoDao.desmarcar(idRecinto)
        } else {
            favoritoDao.marcar(Favorito(idRecinto, System.currentTimeMillis()))
        }
    }
}
