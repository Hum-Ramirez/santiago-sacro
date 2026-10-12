package cl.duoc.santiagosacro

import android.app.Application
import cl.duoc.santiagosacro.core.SesionLocal
import cl.duoc.santiagosacro.data.local.BaseDatos
import cl.duoc.santiagosacro.data.remote.ApiSantiagoSacro
import cl.duoc.santiagosacro.data.remote.ClienteRetrofit
import cl.duoc.santiagosacro.data.repository.*
import cl.duoc.santiagosacro.ubicacion.ServicioUbicacion

/**
 * Contenedor de dependencias hecho a mano.
 * Cumple el mismo papel que Hilt pero sin generacion de codigo,
 * que para el tamano de este MVP es mas simple de explicar y mantener.
 */
object Contenedor {

    lateinit var sesion: SesionLocal
        private set
    lateinit var api: ApiSantiagoSacro
        private set
    lateinit var baseDatos: BaseDatos
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
        api = ClienteRetrofit.crear(sesion)
        baseDatos = BaseDatos.obtener(app)

        authRepositorio = AuthRepositorio(api, sesion)
        recintoRepositorio = RecintoRepositorio(api, baseDatos.recintoDao())
        eventoRepositorio = EventoRepositorio(api)
        encargadoRepositorio = EncargadoRepositorio(api)
        rutaRepositorio = RutaRepositorio(baseDatos.rutaDao(), baseDatos.favoritoDao())
        servicioUbicacion = ServicioUbicacion(app)
    }
}

class SantiagoSacroApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Contenedor.inicializar(this)
    }
}
