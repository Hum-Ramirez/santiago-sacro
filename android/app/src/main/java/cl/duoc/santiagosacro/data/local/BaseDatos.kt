package cl.duoc.santiagosacro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/** Base de datos local del dispositivo (Room sobre SQLite). */
@Database(
    entities = [
        RecintoLocal::class,
        Favorito::class,
        ParadaRuta::class,
        UsuarioLocal::class,
        EventoLocal::class,
        HorarioLocal::class
    ],
    version = 2,
    exportSchema = false
)
abstract class BaseDatos : RoomDatabase() {

    abstract fun recintoDao(): RecintoDao
    abstract fun favoritoDao(): FavoritoDao
    abstract fun rutaDao(): RutaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun eventoDao(): EventoDao
    abstract fun horarioDao(): HorarioDao

    companion object {

        @Volatile
        private var instancia: BaseDatos? = null

        fun obtener(contexto: Context): BaseDatos =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    BaseDatos::class.java,
                    "santiago_sacro.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instancia = it }
            }
    }
}
