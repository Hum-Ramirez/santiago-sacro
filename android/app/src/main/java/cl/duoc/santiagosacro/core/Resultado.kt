package cl.duoc.santiagosacro.core

/**
 * Envoltorio del resultado de una operacion de red o de base de datos.
 * Evita propagar excepciones hasta la interfaz.
 */
sealed class Resultado<out T> {
    data class Exito<T>(val datos: T) : Resultado<T>()
    data class Error(val mensaje: String) : Resultado<Nothing>()
}

/** Ejecuta un bloque de red y traduce cualquier fallo a un mensaje legible. */
suspend fun <T> llamadaSegura(bloque: suspend () -> T): Resultado<T> = try {
    Resultado.Exito(bloque())
} catch (e: retrofit2.HttpException) {
    val mensaje = when (e.code()) {
        400 -> "Revise los datos ingresados"
        401 -> "Debe iniciar sesion nuevamente"
        403 -> "No tiene permiso para esta accion"
        404 -> "No se encontro la informacion solicitada"
        else -> "Error del servidor (" + e.code() + ")"
    }
    Resultado.Error(mensaje)
} catch (e: java.io.IOException) {
    Resultado.Error("Sin conexion con el servidor. Revise que el backend este encendido.")
} catch (e: Exception) {
    Resultado.Error(e.message ?: "Ocurrio un error inesperado")
}
