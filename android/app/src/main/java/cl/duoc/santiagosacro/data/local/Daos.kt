package cl.duoc.santiagosacro.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecintoDao {

    @Query("SELECT * FROM recinto_local ORDER BY nombre ASC")
    fun observarTodos(): Flow<List<RecintoLocal>>

    @Query("SELECT * FROM recinto_local WHERE idRecinto = :id")
    suspend fun porId(id: Long): RecintoLocal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodos(recintos: List<RecintoLocal>)

    @Query("SELECT COUNT(*) FROM recinto_local")
    suspend fun cantidad(): Int
}

@Dao
interface FavoritoDao {

    @Query("SELECT * FROM favorito")
    fun observarFavoritos(): Flow<List<Favorito>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorito WHERE idRecinto = :id)")
    fun esFavorito(id: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun marcar(favorito: Favorito)

    @Query("DELETE FROM favorito WHERE idRecinto = :id")
    suspend fun desmarcar(id: Long)
}

@Dao
interface RutaDao {

    @Query("SELECT * FROM parada_ruta ORDER BY orden ASC")
    fun observarParadas(): Flow<List<ParadaRuta>>

    @Query("SELECT COALESCE(MAX(orden), 0) FROM parada_ruta")
    suspend fun ultimoOrden(): Int

    @Insert
    suspend fun agregar(parada: ParadaRuta)

    @Query("DELETE FROM parada_ruta WHERE id = :id")
    suspend fun quitar(id: Long)

    @Query("DELETE FROM parada_ruta")
    suspend fun vaciar()

    @Query("SELECT EXISTS(SELECT 1 FROM parada_ruta WHERE idRecinto = :idRecinto)")
    suspend fun yaEstaEnLaRuta(idRecinto: Long): Boolean
}
