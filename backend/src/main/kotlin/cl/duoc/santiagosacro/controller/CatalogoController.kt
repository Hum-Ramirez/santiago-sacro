package cl.duoc.santiagosacro.controller

import cl.duoc.santiagosacro.domain.EstadoApertura
import cl.duoc.santiagosacro.dto.CategoriaResponse
import cl.duoc.santiagosacro.repository.CategoriaEventoRepository
import cl.duoc.santiagosacro.repository.TipoRecintoRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** Catalogos de apoyo para los desplegables de la app movil. */
@RestController
@RequestMapping("/api/catalogos")
class CatalogoController(
    private val categoriaRepository: CategoriaEventoRepository,
    private val tipoRecintoRepository: TipoRecintoRepository
) {

    @GetMapping("/categorias")
    fun categorias(): ResponseEntity<List<CategoriaResponse>> =
        ResponseEntity.ok(
            categoriaRepository.findAll().map { CategoriaResponse(it.id ?: 0, it.nombre) }
        )

    @GetMapping("/tipos-recinto")
    fun tipos(): ResponseEntity<List<CategoriaResponse>> =
        ResponseEntity.ok(
            tipoRecintoRepository.findAll().map { CategoriaResponse(it.id ?: 0, it.nombre) }
        )

    @GetMapping("/estados-apertura")
    fun estados(): ResponseEntity<List<String>> =
        ResponseEntity.ok(EstadoApertura.entries.map { it.name })
}
