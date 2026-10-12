package cl.duoc.santiagosacro.service

import cl.duoc.santiagosacro.domain.EstadoEvento
import cl.duoc.santiagosacro.domain.Evento
import cl.duoc.santiagosacro.dto.EventoRequest
import cl.duoc.santiagosacro.dto.EventoResponse
import cl.duoc.santiagosacro.exception.RecursoNoEncontradoException
import cl.duoc.santiagosacro.exception.ReglaNegocioException
import cl.duoc.santiagosacro.repository.CategoriaEventoRepository
import cl.duoc.santiagosacro.repository.EventoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class EventoService(
    private val eventoRepository: EventoRepository,
    private val categoriaRepository: CategoriaEventoRepository,
    private val recintoService: RecintoService,
    private val authService: AuthService
) {

    /** HU09: cartelera de la red, filtrable por rango de fechas y categoria. */
    @Transactional(readOnly = true)
    fun cartelera(desde: LocalDate?, hasta: LocalDate?, idCategoria: Long?): List<EventoResponse> {
        val inicio = desde ?: LocalDate.now()
        val fin = hasta ?: inicio.plusDays(30)

        if (fin.isBefore(inicio)) {
            throw ReglaNegocioException("La fecha final no puede ser anterior a la inicial")
        }

        val eventos = eventoRepository
            .findByEstadoAndFechaInicioBetweenOrderByFechaInicioAscHoraInicioAsc(
                EstadoEvento.PUBLICADO, inicio, fin
            )

        val filtrados = if (idCategoria != null) {
            eventos.filter { it.categoria?.id == idCategoria }
        } else {
            eventos
        }

        return filtrados.map { it.aDto() }
    }

    @Transactional(readOnly = true)
    fun detalle(idEvento: Long): EventoResponse = buscar(idEvento).aDto()

    /** HU11: el encargado publica un evento en su recinto. */
    @Transactional
    fun crear(idRecinto: Long, correo: String, peticion: EventoRequest): EventoResponse {
        val recinto = recintoService.buscar(idRecinto)
        recintoService.verificarQueAdministra(correo, recinto)

        val fecha = peticion.fechaInicio ?: throw ReglaNegocioException("La fecha es obligatoria")
        if (fecha.isBefore(LocalDate.now())) {
            throw ReglaNegocioException("No se puede publicar un evento en una fecha pasada")
        }

        val categoria = categoriaRepository.findById(peticion.idCategoria ?: 0).orElseThrow {
            RecursoNoEncontradoException("La categoria indicada no existe")
        }

        val evento = Evento(
            recinto = recinto,
            categoria = categoria,
            usuario = authService.buscarPorCorreo(correo),
            titulo = peticion.titulo.trim(),
            descripcion = peticion.descripcion,
            fechaInicio = fecha,
            horaInicio = peticion.horaInicio ?: throw ReglaNegocioException("La hora es obligatoria")
        )

        return eventoRepository.save(evento).aDto()
    }

    /** HU30: corregir un evento ya publicado. */
    @Transactional
    fun actualizar(idEvento: Long, correo: String, peticion: EventoRequest): EventoResponse {
        val evento = buscar(idEvento)
        recintoService.verificarQueAdministra(correo, evento.recinto!!)

        peticion.fechaInicio?.let { evento.fechaInicio = it }
        peticion.horaInicio?.let { evento.horaInicio = it }
        if (peticion.titulo.isNotBlank()) evento.titulo = peticion.titulo.trim()
        evento.descripcion = peticion.descripcion
        peticion.idCategoria?.let { idCat ->
            evento.categoria = categoriaRepository.findById(idCat).orElseThrow {
                RecursoNoEncontradoException("La categoria indicada no existe")
            }
        }

        return eventoRepository.save(evento).aDto()
    }

    /** HU30: cancelar deja rastro, no borra el registro. */
    @Transactional
    fun cancelar(idEvento: Long, correo: String): EventoResponse {
        val evento = buscar(idEvento)
        recintoService.verificarQueAdministra(correo, evento.recinto!!)
        evento.estado = EstadoEvento.CANCELADO
        return eventoRepository.save(evento).aDto()
    }

    private fun buscar(idEvento: Long): Evento =
        eventoRepository.findById(idEvento).orElseThrow {
            RecursoNoEncontradoException("No existe el evento con id " + idEvento)
        }
}
