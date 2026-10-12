package cl.duoc.santiagosacro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.santiagosacro.Contenedor
import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.data.remote.dto.*
import cl.duoc.santiagosacro.data.repository.EncargadoRepositorio
import cl.duoc.santiagosacro.data.repository.EventoRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoPanel(
    val cargando: Boolean = true,
    val recintos: List<RecintoResumen> = emptyList(),
    val recintoActivo: RecintoResumen? = null,
    val solicitudes: List<SolicitudRespuesta> = emptyList(),
    val guias: List<GuiaRespuesta> = emptyList(),
    val categorias: List<CategoriaRespuesta> = emptyList(),
    val mensaje: String? = null,
    val error: String? = null
)

class PanelViewModel(
    private val encargadoRepositorio: EncargadoRepositorio,
    private val eventoRepositorio: EventoRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoPanel())
    val estado: StateFlow<EstadoPanel> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }

            when (val resultado = encargadoRepositorio.misRecintos()) {
                is Resultado.Exito -> {
                    val primero = resultado.datos.firstOrNull()
                    _estado.update {
                        it.copy(cargando = false, recintos = resultado.datos, recintoActivo = primero)
                    }
                    primero?.let { cargarGuias(it.id) }
                }
                is Resultado.Error -> _estado.update {
                    it.copy(cargando = false, error = resultado.mensaje)
                }
            }

            cargarSolicitudes()
            cargarCategorias()
        }
    }

    fun seleccionarRecinto(recinto: RecintoResumen) {
        _estado.update { it.copy(recintoActivo = recinto) }
        cargarGuias(recinto.id)
    }

    fun cambiarEstado(nuevoEstado: String) {
        val recinto = _estado.value.recintoActivo ?: return
        viewModelScope.launch {
            when (val resultado = encargadoRepositorio.cambiarEstado(recinto.id, nuevoEstado)) {
                is Resultado.Exito -> {
                    val actualizados = _estado.value.recintos.map {
                        if (it.id == recinto.id) resultado.datos else it
                    }
                    _estado.update {
                        it.copy(
                            recintos = actualizados,
                            recintoActivo = resultado.datos,
                            mensaje = "Estado actualizado"
                        )
                    }
                }
                is Resultado.Error -> _estado.update { it.copy(error = resultado.mensaje) }
            }
        }
    }

    fun publicarEvento(
        titulo: String,
        descripcion: String,
        idCategoria: Long,
        fecha: String,
        hora: String
    ) {
        val recinto = _estado.value.recintoActivo ?: return

        if (titulo.isBlank() || fecha.isBlank() || hora.isBlank()) {
            _estado.update { it.copy(error = "Complete titulo, fecha y hora") }
            return
        }

        viewModelScope.launch {
            val resultado = encargadoRepositorio.publicarEvento(
                recinto.id, titulo, descripcion, idCategoria, fecha, hora
            )
            when (resultado) {
                is Resultado.Exito -> _estado.update {
                    it.copy(mensaje = "Evento publicado: " + resultado.datos.titulo, error = null)
                }
                is Resultado.Error -> _estado.update { it.copy(error = resultado.mensaje) }
            }
        }
    }

    fun gestionarSolicitud(idSolicitud: Long, aceptar: Boolean, idGuia: Long?) {
        viewModelScope.launch {
            val motivo = if (aceptar) null else "Sin guias disponibles para esa fecha"
            when (val resultado =
                encargadoRepositorio.gestionar(idSolicitud, aceptar, idGuia, motivo)) {
                is Resultado.Exito -> {
                    _estado.update {
                        it.copy(
                            solicitudes = it.solicitudes.map { s ->
                                if (s.id == idSolicitud) resultado.datos else s
                            },
                            mensaje = "Solicitud " + resultado.datos.estado.lowercase()
                        )
                    }
                }
                is Resultado.Error -> _estado.update { it.copy(error = resultado.mensaje) }
            }
        }
    }

    fun limpiarMensajes() = _estado.update { it.copy(mensaje = null, error = null) }

    private fun cargarSolicitudes() {
        viewModelScope.launch {
            when (val resultado = encargadoRepositorio.solicitudes()) {
                is Resultado.Exito -> _estado.update { it.copy(solicitudes = resultado.datos) }
                is Resultado.Error -> Unit
            }
        }
    }

    private fun cargarGuias(idRecinto: Long) {
        viewModelScope.launch {
            when (val resultado = encargadoRepositorio.guias(idRecinto)) {
                is Resultado.Exito -> _estado.update { it.copy(guias = resultado.datos) }
                is Resultado.Error -> Unit
            }
        }
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
            initializer {
                PanelViewModel(Contenedor.encargadoRepositorio, Contenedor.eventoRepositorio)
            }
        }
    }
}
