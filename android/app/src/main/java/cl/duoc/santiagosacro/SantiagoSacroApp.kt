package cl.duoc.santiagosacro

import android.app.Application
import cl.duoc.santiagosacro.core.SesionLocal
import cl.duoc.santiagosacro.data.local.BaseDatos
import cl.duoc.santiagosacro.data.local.Sembrador
import cl.duoc.santiagosacro.data.remote.ApiSantiagoSacro
import cl.duoc.santiagosacro.data.remote.ClienteRetrofit
import cl.duoc.santiagosacro.data.repository.*
import cl.duoc.santiagosacro.ubicacion.ServicioUbicacion

/**
 * Contenedor de dependencias hecho a mano.
 * Cumple el mismo papel que Hilt pero sin generacion de codigo,
 * que para el tamano de este MVP es mas simple de explicar y mantener.
 *
 * En esta entrega la aplicacion no se conecta al backend: los repositorios
 * reciben los DAO de Room en vez del cliente de red. La capa de Retrofit se
 * sigue construyendo y queda disponible, lista para la Unidad 3, pero ningun
 * repositorio la consume.
 */
object Contenedor {

    lateinit var sesion: SesionLocal
        private set
    lateinit var baseDatos: BaseDatos
        private set
    lateinit var sembrador: Sembrador
        private set

    /** Capa de red construida y sin usar: se conecta en la Unidad 3. */
    lateinit var api: ApiSantiagoSacro
        private set

    lateinit var authRepositorio: AuthRepositorio
        private set
    lateinit var recintoRepositorio: RecintoRepositorio
        private set
    lateinit var eventoRepositorio: EventoRepositorio
        private set
    lateinit var encargadoRepositorio: EncargadoRepositorio
        private set
    lateinit var rutaRepositorio: RutaRepositorio
        private set
    lateinit var servicioUbicacion: ServicioUbicacion
        private set

    fun inicializar(app: Application) {
        sesion = SesionLocal(app)
        baseDatos = BaseDatos.obtener(app)
        api = ClienteRetrofit.crear(sesion)

        sembrador = Sembrador(
            recintoDao = baseDatos.recintoDao(),
            usuarioDao = baseDatos.usuarioDao(),
            eventoDao = baseDatos.eventoDao(),
            horarioDao = baseDatos.horarioDao()
        )

        authRepositorio = AuthRepositorio(
            usuarioDao = baseDatos.usuarioDao(),
            sesion = sesion,
            sembrador = sembrador
        )

        recintoRepositorio = RecintoRepositorio(
            dao = baseDatos.recintoDao(),
            horarioDao = baseDatos.horarioDao(),
            eventoDao = baseDatos.eventoDao(),
            sembrador = sembrador
        )

        eventoRepositorio = EventoRepositorio(
            eventoDao = baseDatos.eventoDao(),
            sembrador = sembrador
        )

        encargadoRepositorio = EncargadoRepositorio(
            recintoDao = baseDatos.recintoDao(),
            usuarioDao = baseDatos.usuarioDao(),
            eventoDao = baseDatos.eventoDao(),
            sesion = sesion,
            sembrador = sembrador
        )

        rutaRepositorio = RutaRepositorio(
            baseDatos.rutaDao(),
            baseDatos.favoritoDao()
        )

        servicioUbicacion = ServicioUbicacion(app)
    }
}

class SantiagoSacroApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Contenedor.inicializar(this)
    }
}
