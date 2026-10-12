package cl.duoc.santiagosacro.repository

import cl.duoc.santiagosacro.domain.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface RolRepository : JpaRepository<Rol, Long> {
    fun findByNombre(nombre: String): Rol?
}

@Repository
interface UsuarioRepository : JpaRepository<Usuario, Long> {
    fun findByCorreoIgnoreCase(correo: String): Usuario?
    fun existsByCorreoIgnoreCase(correo: String): Boolean
}

@Repository
interface TipoRecintoRepository : JpaRepository<TipoRecinto, Long> {
    fun findByNombreIgnoreCase(nombre: String): TipoRecinto?
}

@Repository
interface RecintoRepository : JpaRepository<Recinto, Long> {

    fun findByComunaIgnoreCase(comuna: String): List<Recinto>

    fun findByEstadoApertura(estado: EstadoApertura): List<Recinto>

    @Query(
        """
        select r from Recinto r
        where lower(r.nombreOficial) like lower(concat('%', :texto, '%'))
           or lower(r.comuna) like lower(concat('%', :texto, '%'))
        """
    )
    fun buscarPorTexto(@Param("texto") texto: String): List<Recinto>
}

@Repository
interface RecursoMultimediaRepository : JpaRepository<RecursoMultimedia, Long> {
    fun findByRecintoIdOrderByOrdenAsc(idRecinto: Long): List<RecursoMultimedia>
}

@Repository
interface HorarioRegularRepository : JpaRepository<HorarioRegular, Long> {
    fun findByRecintoIdOrderByDiaSemanaAsc(idRecinto: Long): List<HorarioRegular>
    fun findByRecintoIdAndDiaSemana(idRecinto: Long, diaSemana: Int): List<HorarioRegular>
}

@Repository
interface BloqueExcepcionalRepository : JpaRepository<BloqueExcepcional, Long> {
    fun findByRecintoIdAndFechaGreaterThanEqualOrderByFechaAsc(
        idRecinto: Long,
        desde: LocalDate
    ): List<BloqueExcepcional>

    fun findByRecintoIdAndFecha(idRecinto: Long, fecha: LocalDate): List<BloqueExcepcional>
}

@Repository
interface CategoriaEventoRepository : JpaRepository<CategoriaEvento, Long> {
    fun findByNombreIgnoreCase(nombre: String): CategoriaEvento?
}

@Repository
interface EventoRepository : JpaRepository<Evento, Long> {

    fun findByEstadoAndFechaInicioBetweenOrderByFechaInicioAscHoraInicioAsc(
        estado: EstadoEvento,
        desde: LocalDate,
        hasta: LocalDate
    ): List<Evento>

    fun findByRecintoIdAndEstadoOrderByFechaInicioAsc(
        idRecinto: Long,
        estado: EstadoEvento
    ): List<Evento>

    fun findByCategoriaIdAndEstadoOrderByFechaInicioAsc(
        idCategoria: Long,
        estado: EstadoEvento
    ): List<Evento>
}

@Repository
interface GuiaVoluntarioRepository : JpaRepository<GuiaVoluntario, Long> {
    fun findByRecintoId(idRecinto: Long): List<GuiaVoluntario>
    fun findByRecintoIdAndDisponibleTrue(idRecinto: Long): List<GuiaVoluntario>
}

@Repository
interface EstadoSolicitudRepository : JpaRepository<EstadoSolicitud, Long> {
    fun findByNombre(nombre: String): EstadoSolicitud?
}

@Repository
interface SolicitudGuiaRepository : JpaRepository<SolicitudGuia, Long> {
    fun findByRecintoIdOrderByFechaVisitaAsc(idRecinto: Long): List<SolicitudGuia>
    fun findByRecintoIdInOrderByFechaVisitaAsc(idsRecinto: List<Long>): List<SolicitudGuia>
    fun findByUsuarioSolicitaIdOrderByFechaSolicitudDesc(idUsuario: Long): List<SolicitudGuia>
}

@Repository
interface AvisoRepository : JpaRepository<Aviso, Long> {
    fun findByVigenciaHastaAfterOrderByVigenciaDesdeDesc(momento: LocalDateTime): List<Aviso>
    fun findByRecintoIdAndVigenciaHastaAfter(idRecinto: Long, momento: LocalDateTime): List<Aviso>
}
