package cl.duoc.santiagosacro.data.local

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Carga el conjunto de datos sinteticos en la base local del telefono.
 *
 * Es el equivalente en Room del CargaDatosIniciales del backend, y existe
 * porque en esta entrega la aplicacion movil no se conecta al servidor: si
 * nadie siembra, Room arranca vacia y las pantallas no tienen que mostrar.
 *
 * Tres decisiones de diseno:
 *
 *  1. Se invoca desde los repositorios, en la primera lectura, no al
 *     arrancar la aplicacion. Asi la siembra y la lectura ocurren en la
 *     misma secuencia y ninguna pantalla puede adelantarse a los datos.
 *
 *  2. Es idempotente: cada bloque pregunta por la cantidad de filas antes
 *     de insertar. Ejecutarlo una vez o cien veces deja el mismo resultado.
 *
 *  3. Todas las operaciones son suspend, asi que Room las despacha a un
 *     hilo de fondo. Tocar la base en el hilo principal esta prohibido.
 *
 * Los identificadores coinciden con los que asigna el backend, para que al
 * conectar las dos partes en la Unidad 3 no haya desajustes.
 */
class Sembrador(
    private val recintoDao: RecintoDao,
    private val usuarioDao: UsuarioDao,
    private val eventoDao: EventoDao,
    private val horarioDao: HorarioDao
) {

    suspend fun sembrarSiHaceFalta() {
        if (recintoDao.cantidad() == 0) recintoDao.guardarTodos(recintos())
        if (horarioDao.cantidad() == 0) horarioDao.guardarTodos(horarios())
        if (eventoDao.cantidad() == 0) eventoDao.guardarTodos(eventos())
        if (usuarioDao.cantidad() == 0) usuarios().forEach { usuarioDao.insertar(it) }
    }

    // ------------------------------------------------------------ recintos

    private fun recintos(): List<RecintoLocal> {
        val ahora = System.currentTimeMillis()
        return listOf(
            RecintoLocal(
                idRecinto = 1,
                nombre = "Catedral Metropolitana",
                tipo = "Templo",
                comuna = "Santiago",
                direccion = "Plaza de Armas s/n",
                latitud = -33.4378,
                longitud = -70.6506,
                estadoApertura = "ABIERTO",
                descripcion = "Ficha de demostracion con datos sinteticos para el MVP academico.",
                fechaSync = ahora
            ),
            RecintoLocal(
                idRecinto = 2,
                nombre = "Iglesia de San Francisco",
                tipo = "Templo",
                comuna = "Santiago",
                direccion = "Av. Libertador Bernardo O'Higgins 834",
                latitud = -33.4450,
                longitud = -70.6490,
                estadoApertura = "ABIERTO",
                descripcion = "Ficha de demostracion con datos sinteticos para el MVP academico.",
                fechaSync = ahora
            ),
            RecintoLocal(
                idRecinto = 3,
                nombre = "Basilica de La Merced",
                tipo = "Templo",
                comuna = "Santiago",
                direccion = "Mac Iver 341",
                latitud = -33.4365,
                longitud = -70.6475,
                estadoApertura = "EN_CELEBRACION",
                descripcion = "Ficha de demostracion con datos sinteticos para el MVP academico.",
                fechaSync = ahora
            ),
            RecintoLocal(
                idRecinto = 4,
                nombre = "Museo de Santo Domingo",
                tipo = "Museo",
                comuna = "Santiago",
                direccion = "Santo Domingo 961",
                latitud = -33.4340,
                longitud = -70.6520,
                estadoApertura = "CERRADO",
                descripcion = "Ficha de demostracion con datos sinteticos para el MVP academico.",
                fechaSync = ahora
            ),
            RecintoLocal(
                idRecinto = 5,
                nombre = "Iglesia de San Agustin",
                tipo = "Templo",
                comuna = "Santiago",
                direccion = "Estado 180",
                latitud = -33.4395,
                longitud = -70.6465,
                estadoApertura = "ABIERTO",
                descripcion = "Ficha de demostracion con datos sinteticos para el MVP academico.",
                fechaSync = ahora
            ),
            RecintoLocal(
                idRecinto = 6,
                nombre = "Basilica de los Sacramentinos",
                tipo = "Templo",
                comuna = "Santiago",
                direccion = "Arturo Prat 471",
                latitud = -33.4512,
                longitud = -70.6494,
                estadoApertura = "CERRADO",
                descripcion = "Ficha de demostracion con datos sinteticos para el MVP academico.",
                fechaSync = ahora
            )
        )
    }

    // ------------------------------------------------------------ horarios

    /**
     * Tres franjas por recinto en vez de siete dias sueltos: la ficha
     * imprime una linea por registro, y agrupar los dias habiles se lee
     * mejor que repetir el mismo horario cinco veces.
     *
     * diaSemana no se muestra, solo sirve para ordenar las franjas.
     */
    private fun horarios(): List<HorarioLocal> {
        val templos = listOf(1L, 2L, 3L, 5L, 6L)

        val deTemplos = templos.flatMap { id ->
            listOf(
                HorarioLocal(
                    idRecinto = id,
                    diaSemana = 1,
                    nombreDia = "Lunes a viernes",
                    horaApertura = "09:00",
                    horaCierre = "19:00"
                ),
                HorarioLocal(
                    idRecinto = id,
                    diaSemana = 6,
                    nombreDia = "Sabado",
                    horaApertura = "09:00",
                    horaCierre = "20:00"
                ),
                HorarioLocal(
                    idRecinto = id,
                    diaSemana = 7,
                    nombreDia = "Domingo",
                    horaApertura = "08:00",
                    horaCierre = "21:00"
                )
            )
        }

        // El museo tiene horario propio y cierra lunes y domingo.
        val delMuseo = listOf(
            HorarioLocal(
                idRecinto = 4,
                diaSemana = 2,
                nombreDia = "Martes a viernes",
                horaApertura = "10:00",
                horaCierre = "18:00"
            ),
            HorarioLocal(
                idRecinto = 4,
                diaSemana = 6,
                nombreDia = "Sabado",
                horaApertura = "10:00",
                horaCierre = "14:00"
            )
        )

        return deTemplos + delMuseo
    }

    // ------------------------------------------------------------ eventos

    private fun eventos(): List<EventoLocal> = listOf(
        EventoLocal(
            id = 1,
            titulo = "Concierto de musica sacra",
            fechaInicio = fechaEnDias(2),
            horaInicio = "19:30:00",
            idCategoria = 1,
            categoria = "Musica sacra",
            idRecinto = 2,
            nombreRecinto = "Iglesia de San Francisco"
        ),
        EventoLocal(
            id = 2,
            titulo = "Visita guiada al claustro",
            fechaInicio = fechaEnDias(2),
            horaInicio = "16:00:00",
            idCategoria = 4,
            categoria = "Visita guiada",
            idRecinto = 3,
            nombreRecinto = "Basilica de La Merced"
        ),
        EventoLocal(
            id = 3,
            titulo = "Exposicion de arte colonial",
            fechaInicio = fechaEnDias(3),
            horaInicio = "11:00:00",
            idCategoria = 2,
            categoria = "Exposicion",
            idRecinto = 4,
            nombreRecinto = "Museo de Santo Domingo"
        ),
        EventoLocal(
            id = 4,
            titulo = "Apertura especial nocturna",
            fechaInicio = fechaEnDias(4),
            horaInicio = "20:00:00",
            idCategoria = 3,
            categoria = "Hito liturgico",
            idRecinto = 5,
            nombreRecinto = "Iglesia de San Agustin"
        )
    )

    // ------------------------------------------------------------ usuarios

    /**
     * Las claves van en texto plano, igual que el ejercicio de clase.
     * Es una decision consciente: una validacion que corre en el propio
     * telefono es una demostracion del flujo, no un control de seguridad.
     * La autenticacion real vive en el backend, donde la clave se guarda
     * con BCrypt en el campo claveHash y nunca en texto plano.
     *
     * En el backend el encargado de San Francisco administra dos recintos
     * (relacion N:M via usuario_recinto). Aca se simplifico a uno solo.
     */
    private fun usuarios(): List<UsuarioLocal> = listOf(
        UsuarioLocal(
            correo = "admin@santiagosacro.cl",
            clave = "Admin123",
            nombre = "Administrador de la red",
            rol = "ADMIN"
        ),
        UsuarioLocal(
            correo = "visitante@correo.cl",
            clave = "Visita123",
            nombre = "Visitante de prueba",
            rol = "VISITANTE"
        ),
        UsuarioLocal(
            correo = "encargado.sanfrancisco@iglesiadesantiago.cl",
            clave = "Encargado123",
            nombre = "Encargado de San Francisco",
            rol = "ENCARGADO",
            idRecintoAdministrado = 2
        ),
        UsuarioLocal(
            correo = "encargado.lamerced@iglesiadesantiago.cl",
            clave = "Encargado123",
            nombre = "Encargado de La Merced",
            rol = "ENCARGADO",
            idRecintoAdministrado = 3
        )
    )

    // ------------------------------------------------------------ fechas

    /**
     * Devuelve una fecha relativa a hoy en formato ISO (yyyy-MM-dd).
     *
     * Se siembra relativo a hoy y no con fechas fijas para que la cartelera
     * muestre actividades proximas cualquier dia que se abra la app.
     *
     * No se usa java.time porque LocalDate pide API 26 y minSdk es 24.
     */
    private fun fechaEnDias(dias: Int): String {
        val calendario = Calendar.getInstance()
        calendario.add(Calendar.DAY_OF_YEAR, dias)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendario.time)
    }
}
