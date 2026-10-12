package cl.duoc.santiagosacro.config

import cl.duoc.santiagosacro.domain.*
import cl.duoc.santiagosacro.repository.*
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Carga el conjunto de datos sinteticos exigido por el caso.
 * Todos los registros son ficticios y de uso academico: no provienen
 * de sistemas reales del Arzobispado.
 */
@Component
@ConditionalOnProperty(name = ["santiagosacro.seed.activo"], havingValue = "true", matchIfMissing = true)
class CargaDatosIniciales(
    private val rolRepository: RolRepository,
    private val usuarioRepository: UsuarioRepository,
    private val tipoRecintoRepository: TipoRecintoRepository,
    private val recintoRepository: RecintoRepository,
    private val categoriaRepository: CategoriaEventoRepository,
    private val eventoRepository: EventoRepository,
    private val guiaRepository: GuiaVoluntarioRepository,
    private val estadoSolicitudRepository: EstadoSolicitudRepository,
    private val solicitudRepository: SolicitudGuiaRepository,
    private val avisoRepository: AvisoRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(CargaDatosIniciales::class.java)

    @Transactional
    override fun run(vararg args: String) {
        if (rolRepository.count() > 0L) {
            log.info("Los datos iniciales ya estaban cargados")
            return
        }

        // ------------------------------------------------ roles y estados
        val rolVisitante = rolRepository.save(Rol(nombre = Rol.VISITANTE))
        val rolEncargado = rolRepository.save(Rol(nombre = Rol.ENCARGADO))
        val rolAdmin = rolRepository.save(Rol(nombre = Rol.ADMIN))

        val pendiente = estadoSolicitudRepository.save(EstadoSolicitud(nombre = EstadoSolicitud.PENDIENTE))
        estadoSolicitudRepository.save(EstadoSolicitud(nombre = EstadoSolicitud.ACEPTADA))
        estadoSolicitudRepository.save(EstadoSolicitud(nombre = EstadoSolicitud.RECHAZADA))
        estadoSolicitudRepository.save(EstadoSolicitud(nombre = EstadoSolicitud.VENCIDA))

        // ------------------------------------------------ catalogos
        val templo = tipoRecintoRepository.save(TipoRecinto(nombre = "Templo"))
        val museo = tipoRecintoRepository.save(TipoRecinto(nombre = "Museo"))
        tipoRecintoRepository.save(TipoRecinto(nombre = "Colegio"))
        tipoRecintoRepository.save(TipoRecinto(nombre = "Archivo"))

        val musica = categoriaRepository.save(CategoriaEvento(nombre = "Musica sacra"))
        val exposicion = categoriaRepository.save(CategoriaEvento(nombre = "Exposicion"))
        val liturgico = categoriaRepository.save(CategoriaEvento(nombre = "Hito liturgico"))
        val visita = categoriaRepository.save(CategoriaEvento(nombre = "Visita guiada"))

        // ------------------------------------------------ recintos sinteticos
        val catedral = crearRecinto(
            tipo = templo,
            nombre = "Catedral Metropolitana",
            direccion = "Plaza de Armas s/n",
            comuna = "Santiago",
            latitud = -33.4378,
            longitud = -70.6506,
            estado = EstadoApertura.ABIERTO,
            descripcion = "Ficha de demostracion. La resena historica definitiva la entrega el Area de Cultura."
        )

        val sanFrancisco = crearRecinto(
            tipo = templo,
            nombre = "Iglesia de San Francisco",
            direccion = "Av. Libertador Bernardo O'Higgins 834",
            comuna = "Santiago",
            latitud = -33.4450,
            longitud = -70.6490,
            estado = EstadoApertura.ABIERTO,
            descripcion = "Ficha de demostracion con datos sinteticos para el MVP academico."
        )

        val laMerced = crearRecinto(
            tipo = templo,
            nombre = "Basilica de La Merced",
            direccion = "Mac Iver 341",
            comuna = "Santiago",
            latitud = -33.4365,
            longitud = -70.6475,
            estado = EstadoApertura.EN_CELEBRACION,
            descripcion = "Ficha de demostracion con datos sinteticos para el MVP academico."
        )

        val santoDomingo = crearRecinto(
            tipo = museo,
            nombre = "Museo de Santo Domingo",
            direccion = "Santo Domingo 961",
            comuna = "Santiago",
            latitud = -33.4340,
            longitud = -70.6520,
            estado = EstadoApertura.CERRADO,
            descripcion = "Ficha de demostracion con datos sinteticos para el MVP academico."
        )

        val sanAgustin = crearRecinto(
            tipo = templo,
            nombre = "Iglesia de San Agustin",
            direccion = "Estado 180",
            comuna = "Santiago",
            latitud = -33.4395,
            longitud = -70.6465,
            estado = EstadoApertura.ABIERTO,
            descripcion = "Ficha de demostracion con datos sinteticos para el MVP academico."
        )

        val sacramentinos = crearRecinto(
            tipo = templo,
            nombre = "Basilica de los Sacramentinos",
            direccion = "Arturo Prat 471",
            comuna = "Santiago",
            latitud = -33.4512,
            longitud = -70.6494,
            estado = EstadoApertura.CERRADO,
            descripcion = "Ficha de demostracion con datos sinteticos para el MVP academico."
        )

        val recintos = listOf(catedral, sanFrancisco, laMerced, santoDomingo, sanAgustin, sacramentinos)

        // ------------------------------------------------ usuarios
        usuarioRepository.save(
            Usuario(
                correo = "admin@santiagosacro.cl",
                claveHash = passwordEncoder.encode("Admin123"),
                nombre = "Administrador de la red",
                rol = rolAdmin
            )
        )

        usuarioRepository.save(
            Usuario(
                correo = "visitante@correo.cl",
                claveHash = passwordEncoder.encode("Visita123"),
                nombre = "Visitante de prueba",
                rol = rolVisitante
            )
        )

        val encargadoUno = usuarioRepository.save(
            Usuario(
                correo = "encargado.sanfrancisco@iglesiadesantiago.cl",
                claveHash = passwordEncoder.encode("Encargado123"),
                nombre = "Encargado de San Francisco",
                rol = rolEncargado,
                recintos = mutableSetOf(sanFrancisco, catedral)
            )
        )

        usuarioRepository.save(
            Usuario(
                correo = "encargado.lamerced@iglesiadesantiago.cl",
                claveHash = passwordEncoder.encode("Encargado123"),
                nombre = "Encargado de La Merced",
                rol = rolEncargado,
                recintos = mutableSetOf(laMerced)
            )
        )

        // ------------------------------------------------ guias voluntarios simulados
        guiaRepository.save(GuiaVoluntario(recinto = sanFrancisco, nombre = "Guia voluntario 1", idioma = "Espanol"))
        guiaRepository.save(GuiaVoluntario(recinto = sanFrancisco, nombre = "Guia voluntario 2", idioma = "Ingles"))
        guiaRepository.save(GuiaVoluntario(recinto = catedral, nombre = "Guia voluntario 3", idioma = "Espanol"))
        guiaRepository.save(
            GuiaVoluntario(recinto = laMerced, nombre = "Guia voluntario 4", idioma = "Espanol", disponible = false)
        )

        // ------------------------------------------------ eventos
        eventoRepository.save(
            Evento(
                recinto = sanFrancisco,
                categoria = musica,
                usuario = encargadoUno,
                titulo = "Concierto de musica sacra",
                descripcion = "Evento sintetico de prueba para la cartelera.",
                fechaInicio = LocalDate.now().plusDays(2),
                horaInicio = LocalTime.of(19, 30)
            )
        )
        eventoRepository.save(
            Evento(
                recinto = laMerced,
                categoria = visita,
                titulo = "Visita guiada al claustro",
                descripcion = "Evento sintetico de prueba para la cartelera.",
                fechaInicio = LocalDate.now().plusDays(2),
                horaInicio = LocalTime.of(16, 0)
            )
        )
        eventoRepository.save(
            Evento(
                recinto = santoDomingo,
                categoria = exposicion,
                titulo = "Exposicion de arte colonial",
                descripcion = "Evento sintetico de prueba para la cartelera.",
                fechaInicio = LocalDate.now().plusDays(3),
                horaInicio = LocalTime.of(11, 0)
            )
        )
        eventoRepository.save(
            Evento(
                recinto = sanAgustin,
                categoria = liturgico,
                titulo = "Apertura especial nocturna",
                descripcion = "Evento sintetico de prueba para la cartelera.",
                fechaInicio = LocalDate.now().plusDays(4),
                horaInicio = LocalTime.of(20, 0)
            )
        )

        // ------------------------------------------------ avisos vigentes
        avisoRepository.save(
            Aviso(
                recinto = laMerced,
                usuario = encargadoUno,
                texto = "Cierre temporal de la nave lateral por mantencion.",
                vigenciaHasta = LocalDateTime.now().plusDays(3)
            )
        )

        // ------------------------------------------------ solicitud de ejemplo
        solicitudRepository.save(
            SolicitudGuia(
                recinto = sanFrancisco,
                estado = pendiente,
                fechaVisita = LocalDate.now().plusDays(5),
                horaVisita = LocalTime.of(10, 30),
                tamanoGrupo = 28,
                contacto = "Colegio de prueba, contacto@colegioficticio.cl"
            )
        )

        log.info("Datos iniciales cargados: {} recintos, {} usuarios", recintos.size, usuarioRepository.count())
    }

    /** Crea el recinto con su horario semanal y su galeria sintetica. */
    private fun crearRecinto(
        tipo: TipoRecinto,
        nombre: String,
        direccion: String,
        comuna: String,
        latitud: Double,
        longitud: Double,
        estado: EstadoApertura,
        descripcion: String
    ): Recinto {
        val recinto = Recinto(
            tipo = tipo,
            nombreOficial = nombre,
            descripcionHistorica = descripcion,
            direccion = direccion,
            comuna = comuna,
            latitud = latitud,
            longitud = longitud,
            estadoApertura = estado
        )

        // Horario de lunes a sabado
        for (dia in 1..6) {
            recinto.horarios.add(
                HorarioRegular(
                    recinto = recinto,
                    diaSemana = dia,
                    horaApertura = LocalTime.of(9, 0),
                    horaCierre = LocalTime.of(19, 0)
                )
            )
        }

        recinto.recursos.add(
            RecursoMultimedia(
                recinto = recinto,
                tipo = "IMAGEN",
                url = "https://cdn.santiagosacro.cl/sinteticos/" + nombre.lowercase().replace(" ", "-") + "-1.jpg",
                descripcion = "Imagen sintetica de referencia",
                orden = 1
            )
        )
        recinto.recursos.add(
            RecursoMultimedia(
                recinto = recinto,
                tipo = "IMAGEN",
                url = "https://cdn.santiagosacro.cl/sinteticos/" + nombre.lowercase().replace(" ", "-") + "-2.jpg",
                descripcion = "Imagen sintetica de referencia",
                orden = 2
            )
        )

        return recintoRepository.save(recinto)
    }
}
