package cl.duoc.santiagosacro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.santiagosacro.Contenedor
import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.data.remote.dto.SolicitudRespuesta
import cl.duoc.santiagosacro.data.repository.RecintoRepositorio
import cl.duoc.santiagosacro.data.repository.SolicitudRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoSolicitud(
    val nombreRecinto: String = "",
    val fecha: String = "",
    val hora: String = "",
    val tamanoGrupo: String = "",
    val contacto: String = "",
    val haySesion: Boolean = false,
    val enviando: Boolean = false,
    val mensaje: String? = null,
    val error: String? = null,
    val misSolicitudes: List<SolicitudRespuesta> = emptyList()
)

/**
 * Solicitud de guia voluntario (HU21) y seguimiento de su estado (HU22).
 *
 * El formulario solo se muestra con sesion iniciada: una solicitud tiene que
 * quedar a nombre de alguien, porque el guia necesita saber a quien espera y
 * el encargado responde a una persona concreta. Es la misma regla del
 * backend, donde el endpoint esta protegido por el filtro JWT.
 */
class SolicitudViewModel(
    private val solicitudRepositorio: SolicitudRepositorio,
    private val recintoRepositorio: RecintoRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoSolicitud())
    val estado: StateFlow<EstadoSolicitud> = _estado.asStateFlow()

    fun cargar(idRecinto: Long) {
        viewModelScope.launch {
            _estado.update { it.copy(haySesion = solicitudRepositorio.haySesion()) }

            when (val resultado = recintoRepositorio.detalle(idRecinto)) {
                is Resultado.Exito -> _estado.update {
                    it.copy(nombreRecinto = resultado.datos.nombreOficial)
                }
                is Resultado.Error -> _estado.update {
                    it.copy(error = resultado.mensaje)
                }
            }

            refrescarMisSolicitudes()
        }
    }

    fun fechaCambio(valor: String) = _estado.update { it.copy(fecha = valor, error = null) }
    fun horaCambio(valor: String) = _estado.update { it.copy(hora = valor, error = null) }
    fun grupoCambio(valor: String) = _estado.update { it.copy(tamanoGrupo = valor, error = null) }
    fun contactoCambio(valor: String) = _estado.update { it.copy(contacto = valor, error = null) }

    fun enviar(idRecinto: Long) {
        val actual = _estado.value

        // toIntOrNull devuelve null si el texto no es un numero, en vez de
        // lanzar excepcion: el campo del formulario es texto libre.
        val grupo = actual.tamanoGrupo.toIntOrNull()

        if (grupo == null) {
            _estado.update { it.copy(error = "El tamano del grupo debe ser un numero") }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(enviando = true, error = null, mensaje = null) }

            val resultado = solicitudRepositorio.solicitar(
                idRecinto = idRecinto,
                nombreRecinto = actual.nombreRecinto,
                fechaVisita = actual.fecha,
                horaVisita = actual.hora,
                tamanoGrupo = grupo,
                contacto = actual.contacto
            )

            when (resultado) {
                is Resultado.Exito -> {
                    _estado.update {
                        it.copy(
                            enviando = false,
                            mensaje = "Solicitud enviada. El encargado del recinto la revisara.",
                            fecha = "",
                            hora = "",
                            tamanoGrupo = "",
                            contacto = ""
                        )
                    }
                    refrescarMisSolicitudes()
                }

                is Resultado.Error -> _estado.update {
                    it.copy(enviando = false, error = resultado.mensaje)
                }
            }
        }
    }

    fun limpiarMensaje() = _estado.update { it.copy(mensaje = null) }

    private suspend fun refrescarMisSolicitudes() {
        when (val resultado = solicitudRepositorio.misSolicitudes()) {
            is Resultado.Exito -> _estado.update { it.copy(misSolicitudes = resultado.datos) }
            is Resultado.Error -> Unit
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                SolicitudViewModel(
                    Contenedor.solicitudRepositorio,
                    Contenedor.recintoRepositorio
                )
            }
        }
    }
}
