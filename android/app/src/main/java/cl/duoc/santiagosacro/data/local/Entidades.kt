package cl.duoc.santiagosacro.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Copia local del recinto. Conserva el identificador del servidor para
 * que una nueva sincronizacion actualice la fila en vez de duplicarla.
 */
@Entity(tableName = "recinto_local")
data class RecintoLocal(
    @PrimaryKey val idRecinto: Long,
    val nombre: String,
    val tipo: String,
    val comuna: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val estadoApertura: String,
    val descripcion: String,
    val santoPatrono: String,
    val anioConstruccion: Int,
    val fiestaPrincipal: String,
    val fechaSync: Long
)

/** Marca de favorito. Es dato del dispositivo y no viaja al servidor. */
@Entity(tableName = "favorito")
data class Favorito(
    @PrimaryKey val idRecinto: Long,
    val fechaMarcado: Long
)

/** Parada de la ruta personalizada del visitante. */
@Entity(tableName = "parada_ruta")
data class ParadaRuta(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idRecinto: Long,
    val nombreRecinto: String,
    val orden: Int,
    val latitud: Double,
    val longitud: Double
)

/** Usuarios guardados en el telefono. Permiten validar el acceso sin backend. */
@Entity(
    tableName = "usuario_local",
    indices = [Index(value = ["correo"], unique = true)]
)
data class UsuarioLocal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val correo: String,
    val clave: String,
    val nombre: String,
    val rol: String,
    val idRecintoAdministrado: Long? = null
)

/** Copia local de los eventos de la cartelera. */
@Entity(tableName = "evento_local")
data class EventoLocal(
    @PrimaryKey val id: Long,
    val titulo: String,
    val fechaInicio: String,
    val horaInicio: String,
    val idCategoria: Long,
    val categoria: String,
    val idRecinto: Long,
    val nombreRecinto: String
)

/**
 * Horario regular de atencion de un recinto, un registro por dia.
 * Relacion 1:N con recinto_local a traves de idRecinto.
 */
@Entity(tableName = "horario_local")
data class HorarioLocal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idRecinto: Long,
    val diaSemana: Int,
    val nombreDia: String,
    val horaApertura: String,
    val horaCierre: String
)

/** Proyeccion de una consulta, no es una tabla. */
data class CategoriaLocal(
    val idCategoria: Long,
    val categoria: String
)
