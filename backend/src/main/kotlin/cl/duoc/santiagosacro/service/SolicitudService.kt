package cl.duoc.santiagosacro.service

import cl.duoc.santiagosacro.domain.EstadoSolicitud
import cl.duoc.santiagosacro.domain.SolicitudGuia
import cl.duoc.santiagosacro.dto.GestionSolicitudRequest
import cl.duoc.santiagosacro.dto.SolicitudRequest
import cl.duoc.santiagosacro.dto.SolicitudResponse
import cl.duoc.santiagosacro.exception.OperacionNoAutorizadaException
import cl.duoc.santiagosacro.exception.RecursoNoEncontradoException
import cl.duoc.santiagosacro.exception.ReglaNegocioException
import cl.duoc.santiagosacro.repository.EstadoSolicitudRepository
import cl.duoc.santiagosacro.repository.GuiaVoluntarioRepository
import cl.duoc.santiagosacro.repository.SolicitudGuiaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class SolicitudService(
    private val solicitudRepository: SolicitudGuiaRepository,
    private val estadoRepository: EstadoSolicitudRepository,
    private val guiaRepository: GuiaVoluntarioRepository,
    private val recintoService: RecintoService,
    private val authService: AuthService
) {

    /** HU21: el visitante solicita acompanamiento de guia. */
    @Transactional
    fun crear(idRecinto: Long, correo: String, peticion: SolicitudRequest): SolicitudResponse {
        val recinto = recintoService.buscar(idRecinto)
        val fecha = peticion.fechaVisita ?: throw ReglaNegocioException("La fecha de visita es obligatoria")

        if (fecha.isBefore(LocalDate.now())) {
            throw ReglaNegocioException("La fecha de visita no puede ser anterior a hoy")
        }

        val pendiente = estadoRepository.findByNombre(EstadoSolicitud.PENDIENTE)
            ?: throw RecursoNoEncontradoException("No esta configurado el estado PENDIENTE")

        val solicitud = SolicitudGuia(
            recinto = recinto,
            estado = pendiente,
            usuarioSolicita = authService.buscarPorCorreo(correo),
            fechaVisita = fecha,
            horaVisita = peticion.horaVisita ?: throw ReglaNegocioException("La hora es obligatoria"),
            tamanoGrupo = peticion.tamanoGrupo,
            contacto = peticion.contacto.trim()
        )

        return solicitudRepository.save(solicitud).aDto()
    }

    /** HU22: el visitante sigue el estado de sus solicitudes. */
    @Transactional(readOnly = true)
    fun misSolicitudes(correo: String): List<SolicitudResponse> {
        val usuario = authService.buscarPorCorreo(correo)
        return solicitudRepository
            .findByUsuarioSolicitaIdOrderByFechaSolicitudDesc(usuario.id ?: 0)
            .map { it.aDto() }
    }

    /** Bandeja del encargado: solicitudes de todos los recintos que administra. */
    @Transactional(readOnly = true)
    fun bandejaEncargado(correo: String): List<SolicitudResponse> {
        val usuario = authService.buscarPorCorreo(correo)
        val ids = usuario.recintos.mapNotNull { it.id }
        if (ids.isEmpty()) return emptyList()
        return solicitudRepository.findByRecintoIdInOrderByFechaVisitaAsc(ids).map { it.aDto() }
    }

    /** HU23 y HU24: aceptar asignando guia, o rechazar indicando motivo. */
    @Transactional
    fun gestionar(idSolicitud: Long, correo: String, peticion: GestionSolicitudRequest): SolicitudResponse {
        val solicitud = solicitudRepository.findById(idSolicitud).orElseThrow {
            RecursoNoEncontradoException("No existe la solicitud con id " + idSolicitud)
        }

        val recinto = solicitud.recinto
            ?: throw RecursoNoEncontradoException("La solicitud no tiene recinto asociado")
        recintoService.verificarQueAdministra(correo, recinto)

        if (solicitud.fechaVisita.isBefore(LocalDate.now())) {
            val vencida = estadoRepository.findByNombre(EstadoSolicitud.VENCIDA)
            solicitud.estado = vencida
            solicitudRepository.save(solicitud)
            throw ReglaNegocioException("La solicitud ya vencio y no puede gestionarse")
        }

        val aceptar = peticion.aceptar
            ?: throw ReglaNegocioException("Debe indicar si acepta o rechaza la solicitud")

        if (aceptar) {
            val idGuia = peticion.idGuia
                ?: throw ReglaNegocioException("Debe asignar un guia para aceptar la solicitud")

            val guia = guiaRepository.findById(idGuia).orElseThrow {
                RecursoNoEncontradoException("No existe el guia con id " + idGuia)
            }

            if (guia.recinto?.id != recinto.id) {
                throw OperacionNoAutorizadaException("El guia no pertenece a este recinto")
            }
            if (!guia.disponible) {
                throw ReglaNegocioException("El guia seleccionado no esta disponible")
            }

            solicitud.guia = guia
            solicitud.estado = estadoRepository.findByNombre(EstadoSolicitud.ACEPTADA)
            solicitud.motivoRechazo = null
        } else {
            val motivo = peticion.motivoRechazo
            if (motivo.isNullOrBlank()) {
                throw ReglaNegocioException("Debe indicar el motivo del rechazo")
            }
            solicitud.guia = null
            solicitud.motivoRechazo = motivo.trim()
            solicitud.estado = estadoRepository.findByNombre(EstadoSolicitud.RECHAZADA)
        }

        solicitud.usuarioGestiona = authService.buscarPorCorreo(correo)
        return solicitudRepository.save(solicitud).aDto()
    }

    /** HU25: nomina de guias del recinto. */
    @Transactional(readOnly = true)
    fun guiasDelRecinto(idRecinto: Long) =
        guiaRepository.findByRecintoId(idRecinto).map { it.aDto() }
}
