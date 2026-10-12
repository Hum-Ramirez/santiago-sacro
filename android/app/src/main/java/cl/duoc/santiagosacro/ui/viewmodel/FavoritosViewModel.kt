package cl.duoc.santiagosacro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.santiagosacro.Contenedor
import cl.duoc.santiagosacro.data.local.RecintoLocal
import cl.duoc.santiagosacro.data.repository.RecintoRepositorio
import cl.duoc.santiagosacro.data.repository.RutaRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class EstadoFavoritos(
    val favoritos: List<RecintoLocal> = emptyList(),
    val cargando: Boolean = true
)

/**
 * Los favoritos se resuelven enteramente en el telefono:
 * cruzan la tabla favorito con la copia local de recintos.
 */
class FavoritosViewModel(
    rutaRepositorio: RutaRepositorio,
    recintoRepositorio: RecintoRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoFavoritos())
    val estado: StateFlow<EstadoFavoritos> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                rutaRepositorio.observarFavoritos(),
                recintoRepositorio.observarCache()
            ) { favoritos, recintos ->
                val ids = favoritos.map { it.idRecinto }.toSet()
                recintos.filter { it.idRecinto in ids }
            }.collect { lista ->
                _estado.value = EstadoFavoritos(favoritos = lista, cargando = false)
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                FavoritosViewModel(Contenedor.rutaRepositorio, Contenedor.recintoRepositorio)
            }
        }
    }
}
