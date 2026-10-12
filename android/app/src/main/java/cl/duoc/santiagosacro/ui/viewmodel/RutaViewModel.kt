package cl.duoc.santiagosacro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.santiagosacro.Contenedor
import cl.duoc.santiagosacro.core.Geo
import cl.duoc.santiagosacro.data.local.ParadaRuta
import cl.duoc.santiagosacro.data.repository.RutaRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Tramo entre dos paradas consecutivas. */
data class Tramo(val minutos: Int, val distancia: String)

data class EstadoRuta(
    val paradas: List<ParadaRuta> = emptyList(),
    val tramos: List<Tramo> = emptyList(),
    val minutosTotales: Int = 0
)

class RutaViewModel(
    private val rutaRepositorio: RutaRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoRuta())
    val estado: StateFlow<EstadoRuta> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            rutaRepositorio.observarParadas().collect { paradas ->
                _estado.update { recalcular(paradas) }
            }
        }
    }

    fun quitar(id: Long) {
        viewModelScope.launch { rutaRepositorio.quitarParada(id) }
    }

    fun vaciar() {
        viewModelScope.launch { rutaRepositorio.vaciarRuta() }
    }

    /**
     * Calcula los tiempos estimados entre paradas con la formula de Haversine
     * y una velocidad de caminata de 4,5 km/h.
     */
    private fun recalcular(paradas: List<ParadaRuta>): EstadoRuta {
        val tramos = mutableListOf<Tramo>()

        for (i in 0 until paradas.size - 1) {
            val actual = paradas[i]
            val siguiente = paradas[i + 1]
            val km = Geo.distanciaKm(
                actual.latitud, actual.longitud,
                siguiente.latitud, siguiente.longitud
            )
            tramos.add(Tramo(Geo.minutosCaminando(km), Geo.formatearDistancia(km)))
        }

        val minutosVisita = paradas.size * 20
        val minutosTraslado = tramos.sumOf { it.minutos }

        return EstadoRuta(
            paradas = paradas,
            tramos = tramos,
            minutosTotales = minutosVisita + minutosTraslado
        )
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { RutaViewModel(Contenedor.rutaRepositorio) }
        }
    }
}
