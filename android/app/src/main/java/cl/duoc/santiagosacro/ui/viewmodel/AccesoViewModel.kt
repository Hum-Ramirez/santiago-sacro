package cl.duoc.santiagosacro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.santiagosacro.Contenedor
import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.data.repository.AuthRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoAcceso(
    val correo: String = "",
    val clave: String = "",
    val nombre: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val sesionIniciada: Boolean = false,
    val esEncargado: Boolean = false
)

class AccesoViewModel(
    private val authRepositorio: AuthRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(
        EstadoAcceso(
            sesionIniciada = authRepositorio.haySesion(),
            esEncargado = authRepositorio.esEncargado()
        )
    )
    val estado: StateFlow<EstadoAcceso> = _estado.asStateFlow()

    fun correoCambio(valor: String) = _estado.update { it.copy(correo = valor, error = null) }
    fun claveCambio(valor: String) = _estado.update { it.copy(clave = valor, error = null) }
    fun nombreCambio(valor: String) = _estado.update { it.copy(nombre = valor, error = null) }

    fun ingresar() {
        val actual = _estado.value

        if (actual.correo.isBlank() || actual.clave.isBlank()) {
            _estado.update { it.copy(error = "Complete el correo y la clave") }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }

            when (val resultado = authRepositorio.login(actual.correo, actual.clave)) {
                is Resultado.Exito -> _estado.update {
                    it.copy(
                        cargando = false,
                        sesionIniciada = true,
                        esEncargado = resultado.datos.rol == "ENCARGADO"
                    )
                }
                is Resultado.Error -> _estado.update {
                    it.copy(cargando = false, error = resultado.mensaje)
                }
            }
        }
    }

    fun registrarse() {
        val actual = _estado.value

        if (actual.nombre.isBlank() || actual.correo.isBlank() || actual.clave.length < 6) {
            _estado.update {
                it.copy(error = "Revise los datos: la clave debe tener al menos 6 caracteres")
            }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }

            when (val resultado =
                authRepositorio.registrar(actual.nombre, actual.correo, actual.clave)) {
                is Resultado.Exito -> _estado.update {
                    it.copy(cargando = false, sesionIniciada = true, esEncargado = false)
                }
                is Resultado.Error -> _estado.update {
                    it.copy(cargando = false, error = resultado.mensaje)
                }
            }
        }
    }

    fun cerrarSesion() {
        authRepositorio.cerrarSesion()
        _estado.value = EstadoAcceso()
    }

    fun nombreUsuario(): String = authRepositorio.nombreUsuario()

    companion object {
        val Factory = viewModelFactory {
            initializer { AccesoViewModel(Contenedor.authRepositorio) }
        }
    }
}
