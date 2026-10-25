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

    // ---- agregados para trabajar sin backend ----

    @Query("SELECT * FROM recinto_local ORDER BY nombre ASC")
    suspend fun listarTodos(): List<RecintoLocal>

    @Query("SELECT * FROM recinto_local WHERE nombre LIKE '%' || :texto || '%' ORDER BY nombre ASC")
    suspend fun buscarPorNombre(texto: String): List<RecintoLocal>

    @Query("SELECT * FROM recinto_local WHERE idRecinto IN (:ids) ORDER BY nombre ASC")
    suspend fun porIds(ids: List<Long>): List<RecintoLocal>

    @Query("UPDATE recinto_local SET estadoApertura = :estado WHERE idRecinto = :id")
    suspend fun cambiarEstado(id: Long, estado: String)
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

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuario_local WHERE correo = :correo")
    suspend fun porCorreo(correo: String): UsuarioLocal?

    @Insert
    suspend fun insertar(usuario: UsuarioLocal)

    @Query("SELECT COUNT(*) FROM usuario_local")
    suspend fun cantidad(): Int

    @Update
    suspend fun actualizar(usuario: UsuarioLocal)

    @Delete
    suspend fun eliminar(usuario: UsuarioLocal)
}

@Dao
interface EventoDao {

    @Query("SELECT * FROM evento_local ORDER BY fechaInicio ASC, horaInicio ASC")
    suspend fun listarTodos(): List<EventoLocal>

    @Query(
        "SELECT * FROM evento_local WHERE idCategoria = :idCategoria " +
            "ORDER BY fechaInicio ASC, horaInicio ASC"
    )
    suspend fun porCategoria(idCategoria: Long): List<EventoLocal>

    @Query("SELECT DISTINCT idCategoria, categoria FROM evento_local ORDER BY categoria ASC")
    suspend fun categorias(): List<CategoriaLocal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodos(eventos: List<EventoLocal>)

    @Query("SELECT COUNT(*) FROM evento_local")
    suspend fun cantidad(): Int

    // ---- agregados para el panel del encargado ----

    @Query("SELECT * FROM evento_local WHERE idRecinto = :idRecinto ORDER BY fechaInicio ASC")
    suspend fun porRecinto(idRecinto: Long): List<EventoLocal>

    @Query("SELECT COALESCE(MAX(id), 0) FROM evento_local")
    suspend fun ultimoId(): Long

    @Insert
    suspend fun insertar(evento: EventoLocal)
}

@Dao
interface SolicitudDao {

    @Insert
    suspend fun insertar(solicitud: SolicitudLocal)

    /** Las solicitudes de un visitante, para que siga su estado. */
    @Query("SELECT * FROM solicitud_local WHERE correoSolicitante = :correo ORDER BY id DESC")
    suspend fun porSolicitante(correo: String): List<SolicitudLocal>

    /** Las solicitudes que le llegan al encargado de un recinto. */
    @Query("SELECT * FROM solicitud_local WHERE idRecinto = :idRecinto ORDER BY id DESC")
    suspend fun porRecinto(idRecinto: Long): List<SolicitudLocal>

    @Query("SELECT * FROM solicitud_local WHERE id = :id")
    suspend fun porId(id: Long): SolicitudLocal?

    /**
     * Aceptar o rechazar cambia varios campos de la misma fila (el estado, el
     * guia asignado, el motivo), asi que aca si conviene @Update con la
     * entidad completa en vez de un UPDATE por columna.
     */
    @Update
    suspend fun actualizar(solicitud: SolicitudLocal)
}

@Dao
interface HorarioDao {

    @Query("SELECT * FROM horario_local WHERE idRecinto = :idRecinto ORDER BY diaSemana ASC")
    suspend fun porRecinto(idRecinto: Long): List<HorarioLocal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodos(horarios: List<HorarioLocal>)

    @Query("SELECT COUNT(*) FROM horario_local")
    suspend fun cantidad(): Int
}
