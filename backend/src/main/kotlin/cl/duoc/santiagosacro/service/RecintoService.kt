package cl.duoc.santiagosacro.service

import cl.duoc.santiagosacro.domain.*
import cl.duoc.santiagosacro.dto.*
import cl.duoc.santiagosacro.exception.OperacionNoAutorizadaException
import cl.duoc.santiagosacro.exception.RecursoNoEncontradoException
import cl.duoc.santiagosacro.exception.ReglaNegocioException
import cl.duoc.santiagosacro.repository.*
import cl.duoc.santiagosacro.util.Geo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class RecintoService(
    private val recintoRepository: RecintoRepository,
    private val eventoRepository: EventoRepository,
    private val bloqueRepository: BloqueExcepcionalRepository,
    private val authService: AuthService
) {

    @Transactional(readOnly = true)
    fun listar(texto: String?, comuna: String?, estado: String?): List<RecintoResumenResponse> {
        val recintos = when {
            !texto.isNullOrBlank() -> recintoRepository.buscarPorTexto(texto)
            !comuna.isNullOrBlank() -> recintoRepository.findByComunaIgnoreCase(comuna)
            !estado.isNullOrBlank() -> recintoRepository.findByEstadoApertura(aEstado(estado))
            else -> recintoRepository.findAll()
        }
        return recintos.map { it.aResumen() }
    }

    /** Recintos ordenados por cercania, usando la posicion GPS del telefono. */
    @Transactional(readOnly = true)
    fun cercanos(latitud: Double, longitud: Double, radioKm: Double): List<RecintoResumenResponse> =
        recintoRepository.findAll()
            .map { recinto ->
                val distancia = distanciaKm(latitud, longitud, recinto.latitud, recinto.longitud)
                recinto.aResumen(distanciaKm = Math.round(distancia * 100.0) / 100.0)
            }
            .filter { (it.distanciaKm ?: 0.0) <= radioKm }
            .sortedBy { it.distanciaKm }

    @Transactional(readOnly = true)
    fun detalle(idRecinto: Long): RecintoDetalleResponse {
        val recinto = buscar(idRecinto)
        val eventos = eventoRepository
            .findByRecintoIdAndEstadoOrderByFechaInicioAsc(idRecinto, EstadoEvento.PUBLICADO)
            .filter { !it.fechaInicio.isBefore(LocalDate.now()) }

        return RecintoDetalleResponse(
            id = recinto.id ?: 0,
            nombreOficial = recinto.nombreOficial,
            tipo = recinto.tipo?.nombre ?: "",
            comuna = recinto.comuna,
            direccion = recinto.direccion,
            latitud = recinto.latitud,
            longitud = recinto.longitud,
            descripcionHistorica = recinto.descripcionHistorica,
            estadoApertura = recinto.estadoApertura.name,
            horarios = recinto.horarios.sortedBy { it.diaSemana }.map { it.aDto() },
            bloquesExcepcionales = bloqueRepository
                .findByRecintoIdAndFechaGreaterThanEqualOrderByFechaAsc(idRecinto, LocalDate.now())
                .map { it.aDto() },
            galeria = recinto.recursos.sortedBy { it.orden }.map { it.aDto() },
            eventosProximos = eventos.map { it.aDto() }
        )
    }

    /** HU12: el encargado cambia el estado de apertura de su recinto. */
    @Transactional
    fun cambiarEstado(idRecinto: Long, correo: String, peticion: CambioEstadoRequest): RecintoResumenResponse {
        val recinto = buscar(idRecinto)
        verificarQueAdministra(correo, recinto)
        recinto.estadoApertura = aEstado(peticion.estado)
        return recintoRepository.save(recinto).aResumen()
    }

    /** HU13: bloque horario excepcional, con validacion de horas y superposicion. */
    @Transactional
    fun registrarBloque(idRecinto: Long, correo: String, peticion: BloqueRequest): BloqueResponse {
        val recinto = buscar(idRecinto)
        val usuario = authService.buscarPorCorreo(correo)
        verificarQueAdministra(correo, recinto)

        val fecha = peticion.fecha ?: throw ReglaNegocioException("La fecha es obligatoria")
        val inicio = peticion.horaInicio ?: throw ReglaNegocioException("La hora de inicio es obligatoria")
        val termino = peticion.horaTermino ?: throw ReglaNegocioException("La hora de termino es obligatoria")

        if (fecha.isBefore(LocalDate.now())) {
            throw ReglaNegocioException("No se pueden registrar bloques en fechas pasadas")
        }
        if (!termino.isAfter(inicio)) {
            throw ReglaNegocioException("La hora de termino debe ser posterior a la de inicio")
        }

        val seSuperpone = bloqueRepository.findByRecintoIdAndFecha(idRecinto, fecha).any { existente ->
            inicio.isBefore(existente.horaTermino) && termino.isAfter(existente.horaInicio)
        }
        if (seSuperpone) {
            throw ReglaNegocioException("El bloque se superpone con otro ya registrado ese dia")
        }

        val bloque = BloqueExcepcional(
            recinto = recinto,
            usuario = usuario,
            fecha = fecha,
            horaInicio = inicio,
            horaTermino = termino,
            motivo = peticion.motivo
        )
        return bloqueRepository.save(bloque).aDto()
    }

    @Transactional(readOnly = true)
    fun buscar(idRecinto: Long): Recinto =
        recintoRepository.findById(idRecinto).orElseThrow {
            RecursoNoEncontradoException("No existe el recinto con id " + idRecinto)
        }

    /** Un encargado solo puede administrar los recintos que tiene asignados. */
    fun verificarQueAdministra(correo: String, recinto: Recinto) {
        val usuario = authService.buscarPorCorreo(correo)
        val administra = usuario.recintos.any { it.id == recinto.id }
        if (!administra) {
            throw OperacionNoAutorizadaException("Usted no administra este recinto")
        }
    }

    private fun aEstado(valor: String): EstadoApertura = try {
        EstadoApertura.valueOf(valor.trim().uppercase())
    } catch (e: IllegalArgumentException) {
        throw ReglaNegocioException("Estado invalido. Use ABIERTO, CERRADO o EN_CELEBRACION")
    }

    /** Distancia entre dos coordenadas, calculada con la formula de Haversine. */
    fun distanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double =
        Geo.distanciaKm(lat1, lon1, lat2, lon2)
}
