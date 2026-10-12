package cl.duoc.santiagosacro.service

import cl.duoc.santiagosacro.domain.Aviso
import cl.duoc.santiagosacro.dto.AvisoRequest
import cl.duoc.santiagosacro.dto.AvisoResponse
import cl.duoc.santiagosacro.exception.ReglaNegocioException
import cl.duoc.santiagosacro.repository.AvisoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AvisoService(
    private val avisoRepository: AvisoRepository,
    private val recintoService: RecintoService,
    private val authService: AuthService
) {

    /** HU29: avisos vigentes de toda la red. */
    @Transactional(readOnly = true)
    fun vigentes(): List<AvisoResponse> =
        avisoRepository.findByVigenciaHastaAfterOrderByVigenciaDesdeDesc(LocalDateTime.now())
            .map { it.aDto() }

    /** HU28: el encargado levanta un aviso sobre el mapa. */
    @Transactional
    fun crear(idRecinto: Long, correo: String, peticion: AvisoRequest): AvisoResponse {
        val recinto = recintoService.buscar(idRecinto)
        recintoService.verificarQueAdministra(correo, recinto)

        val hasta = peticion.vigenciaHasta
            ?: throw ReglaNegocioException("Debe indicar hasta cuando rige el aviso")

        if (hasta.isBefore(LocalDateTime.now())) {
            throw ReglaNegocioException("La vigencia del aviso ya esta vencida")
        }

        val aviso = Aviso(
            recinto = recinto,
            usuario = authService.buscarPorCorreo(correo),
            texto = peticion.texto.trim(),
            vigenciaDesde = LocalDateTime.now(),
            vigenciaHasta = hasta
        )

        return avisoRepository.save(aviso).aDto()
    }
}
