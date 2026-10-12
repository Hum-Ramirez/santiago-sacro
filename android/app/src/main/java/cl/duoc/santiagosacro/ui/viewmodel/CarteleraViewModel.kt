package cl.duoc.santiagosacro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.santiagosacro.Contenedor
import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.data.remote.dto.CategoriaRespuesta
import cl.duoc.santiagosacro.data.remote.dto.EventoRespuesta
import cl.duoc.santiagosacro.data.repository.EventoRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoCartelera(
    val cargando: Boolean = true,
    val eventos: List<EventoRespuesta> = emptyList(),
    val categorias: List<CategoriaRespuesta> = emptyList(),
    val categoriaSeleccionada: Long? = null,
    val error: String? = null
)

class CarteleraViewModel(
    private val eventoRepositorio: EventoRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoCartelera())
    val estado: StateFlow<EstadoCartelera> = _estado.asStateFlow()

    init {
        cargarCategorias()
        cargar(null)
    }

    fun cargar(idCategoria: Long?) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, categoriaSeleccionada = idCategoria) }

            when (val resultado = eventoRepositorio.cartelera(idCategoria)) {
                is Resultado.Exito -> _estado.update {
                    it.copy(cargando = false, eventos = resultado.datos, error = null)
                }
                is Resultado.Error -> _estado.update {
                    it.copy(cargando = false, error = resultado.mensaje)
                }
            }
        }
    }

    fun filtrarPor(idCategoria: Long?) {
        val nuevo = if (_estado.value.categoriaSeleccionada == idCategoria) null else idCategoria
        cargar(nuevo)
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            when (val resultado = eventoRepositorio.categorias()) {
                is Resultado.Exito -> _estado.update { it.copy(categorias = resultado.datos) }
                is Resultado.Error -> Unit
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { CarteleraViewModel(Contenedor.eventoRepositorio) }
        }
    }
}
