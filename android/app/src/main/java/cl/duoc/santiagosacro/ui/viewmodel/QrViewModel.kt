package cl.duoc.santiagosacro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.santiagosacro.Contenedor
import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.data.repository.RecintoRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoQr(
    val contenido: String = "",
    val idRecinto: Long? = null,
    val nombreRecinto: String = "",
    val verificando: Boolean = false,
    val error: String? = null
)

/**
 * Interpreta el texto que devuelve el lector de codigos QR.
 *
 * Las placas de los recintos llevan un codigo con el formato
 * SANTIAGOSACRO-RECINTO-2, donde el numero final es el identificador del
 * recinto. El codigo no contiene los datos del templo, solo lo senala: asi
 * la informacion se corrige en la base sin reimprimir las placas, y el QR
 * se mantiene chico y facil de leer.
 *
 * Despues de extraer el numero se consulta la base local para comprobar
 * que ese recinto existe de verdad. Un QR cualquiera, o uno de la red con
 * un numero que no esta cargado, terminan en un mensaje de error en vez de
 * una navegacion a una ficha vacia.
 */
class QrViewModel(
    private val recintoRepositorio: RecintoRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoQr())
    val estado: StateFlow<EstadoQr> = _estado.asStateFlow()

    fun procesarQr(contenido: String) {
        val texto = contenido.trim()

        _estado.update {
            it.copy(
                contenido = texto,
                idRecinto = null,
                nombreRecinto = "",
                verificando = true,
                error = null
            )
        }

        if (!texto.startsWith(PREFIJO, ignoreCase = true)) {
            _estado.update {
                it.copy(
                    verificando = false,
                    error = "Este codigo no pertenece a la red patrimonial"
                )
            }
            return
        }

        // substringAfterLast devuelve lo que viene despues del ultimo guion,
        // y toLongOrNull entrega null si ese texto no es un numero, en vez de
        // lanzar una excepcion como lo haria toLong.
        val id = texto.substringAfterLast("-").toLongOrNull()

        if (id == null) {
            _estado.update {
                it.copy(
                    verificando = false,
                    error = "El codigo no contiene un numero de recinto valido"
                )
            }
            return
        }

        viewModelScope.launch {
            when (val resultado = recintoRepositorio.detalle(id)) {
                is Resultado.Exito -> _estado.update {
                    it.copy(
                        verificando = false,
                        idRecinto = id,
                        nombreRecinto = resultado.datos.nombreOficial,
                        error = null
                    )
                }

                is Resultado.Error -> _estado.update {
                    it.copy(
                        verificando = false,
                        error = "No hay ningun recinto con ese codigo"
                    )
                }
            }
        }
    }

    /** Vuelve el estado a su valor inicial al salir de la pantalla. */
    fun limpiar() {
        _estado.value = EstadoQr()
    }

    companion object {

        private const val PREFIJO = "SANTIAGOSACRO-RECINTO-"

        val Factory = viewModelFactory {
            initializer { QrViewModel(Contenedor.recintoRepositorio) }
        }
    }
}
