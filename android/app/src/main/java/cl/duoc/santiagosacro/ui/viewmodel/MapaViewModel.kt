package cl.duoc.santiagosacro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.santiagosacro.Contenedor
import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.data.remote.dto.AvisoRespuesta
import cl.duoc.santiagosacro.data.remote.dto.RecintoResumen
import cl.duoc.santiagosacro.data.repository.EventoRepositorio
import cl.duoc.santiagosacro.data.repository.RecintoRepositorio
import cl.duoc.santiagosacro.ubicacion.ServicioUbicacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoMapa(
    val cargando: Boolean = true,
    val recintos: List<RecintoResumen> = emptyList(),
    val avisos: List<AvisoRespuesta> = emptyList(),
    val seleccionado: RecintoResumen? = null,
    val textoBusqueda: String = "",
    val filtroAbiertos: Boolean = false,
    val usandoGps: Boolean = false,
    val mensaje: String? = null
)

class MapaViewModel(
    private val recintoRepositorio: RecintoRepositorio,
    private val eventoRepositorio: EventoRepositorio,
    private val servicioUbicacion: ServicioUbicacion
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoMapa())
    val estado: StateFlow<EstadoMapa> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, mensaje = null) }

            val posicion = servicioUbicacion.posicionActual()
            val usandoGps = posicion != null

            val resultado = if (posicion != null) {
                recintoRepositorio.cercanos(posicion.latitud, posicion.longitud, radioKm = 50.0)
            } else {
                recintoRepositorio.listar()
            }

            when (resultado) {
                is Resultado.Exito -> _estado.update {
                    it.copy(
                        cargando = false,
                        recintos = resultado.datos,
                        seleccionado = resultado.datos.firstOrNull(),
                        usandoGps = usandoGps,
                        mensaje = null
                    )
                }

                is Resultado.Error -> _estado.update {
                    it.copy(cargando = false, mensaje = resultado.mensaje)
                }
            }

            cargarAvisos()
        }
    }

    private fun cargarAvisos() {
        viewModelScope.launch {
            when (val resultado = eventoRepositorio.avisosVigentes()) {
                is Resultado.Exito -> _estado.update { it.copy(avisos = resultado.datos) }
                is Resultado.Error -> Unit
            }
        }
    }

    fun buscar(texto: String) {
        _estado.update { it.copy(textoBusqueda = texto) }
        viewModelScope.launch {
            val resultado = recintoRepositorio.listar(texto.ifBlank { null })
            if (resultado is Resultado.Exito) {
                _estado.update {
                    it.copy(recintos = resultado.datos, seleccionado = resultado.datos.firstOrNull())
                }
            }
        }
    }

    fun alternarFiltroAbiertos() {
        _estado.update { it.copy(filtroAbiertos = !it.filtroAbiertos) }
    }

    fun seleccionar(recinto: RecintoResumen) {
        _estado.update { it.copy(seleccionado = recinto) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                MapaViewModel(
                    Contenedor.recintoRepositorio,
                    Contenedor.eventoRepositorio,
                    Contenedor.servicioUbicacion
                )
            }
        }
    }
}
