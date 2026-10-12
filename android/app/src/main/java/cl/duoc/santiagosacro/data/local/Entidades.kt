package cl.duoc.santiagosacro.data.local

import androidx.room.Entity
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
