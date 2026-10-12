package cl.duoc.santiagosacro.data.repository

import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.core.llamadaSegura
import cl.duoc.santiagosacro.data.remote.ApiSantiagoSacro
import cl.duoc.santiagosacro.data.remote.dto.AvisoRespuesta
import cl.duoc.santiagosacro.data.remote.dto.CategoriaRespuesta
import cl.duoc.santiagosacro.data.remote.dto.EventoRespuesta

class EventoRepositorio(
    private val api: ApiSantiagoSacro
) {

    suspend fun cartelera(idCategoria: Long? = null): Resultado<List<EventoRespuesta>> =
        llamadaSegura { api.cartelera(idCategoria = idCategoria) }

    suspend fun categorias(): Resultado<List<CategoriaRespuesta>> =
        llamadaSegura { api.categorias() }

    suspend fun avisosVigentes(): Resultado<List<AvisoRespuesta>> =
        llamadaSegura { api.avisosVigentes() }
}
