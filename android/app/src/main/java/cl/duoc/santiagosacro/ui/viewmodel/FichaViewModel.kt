package cl.duoc.santiagosacro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.santiagosacro.Contenedor
import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.data.remote.dto.RecintoDetalle
import cl.duoc.santiagosacro.data.repository.RecintoRepositorio
import cl.duoc.santiagosacro.data.repository.RutaRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoFicha(
    val cargando: Boolean = true,
    val detalle: RecintoDetalle? = null,
    val esFavorito: Boolean = false,
    val error: String? = null,
    val aviso: String? = null
)

class FichaViewModel(
    private val recintoRepositorio: RecintoRepositorio,
    private val rutaRepositorio: RutaRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoFicha())
    val estado: StateFlow<EstadoFicha> = _estado.asStateFlow()

    fun cargar(idRecinto: Long) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }

            when (val resultado = recintoRepositorio.detalle(idRecinto)) {
                is Resultado.Exito -> _estado.update {
                    it.copy(cargando = false, detalle = resultado.datos)
                }
                is Resultado.Error -> _estado.update {
                    it.copy(cargando = false, error = resultado.mensaje)
                }
            }
        }

        viewModelScope.launch {
            rutaRepositorio.esFavorito(idRecinto).collect { favorito ->
                _estado.update { it.copy(esFavorito = favorito) }
            }
        }
    }

    fun alternarFavorito(idRecinto: Long) {
        viewModelScope.launch {
            rutaRepositorio.alternarFavorito(idRecinto, _estado.value.esFavorito)
        }
    }

    fun agregarARuta() {
        val detalle = _estado.value.detalle ?: return
        viewModelScope.launch {
            val agregado = rutaRepositorio.agregarParada(
                idRecinto = detalle.id,
                nombre = detalle.nombreOficial,
                latitud = detalle.latitud,
                longitud = detalle.longitud
            )
            _estado.update {
                it.copy(
                    aviso = if (agregado) "Agregado a tu ruta"
                    else "Este recinto ya esta en tu ruta"
                )
            }
        }
    }

    fun limpiarAviso() = _estado.update { it.copy(aviso = null) }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                FichaViewModel(Contenedor.recintoRepositorio, Contenedor.rutaRepositorio)
            }
        }
    }
}
