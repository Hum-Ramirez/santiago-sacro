package cl.duoc.santiagosacro.exception

import cl.duoc.santiagosacro.dto.ApiError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Centraliza el manejo de errores para que la app movil
 * reciba siempre la misma estructura de respuesta.
 */
@RestControllerAdvice
class ManejadorGlobalErrores {

    @ExceptionHandler(RecursoNoEncontradoException::class)
    fun noEncontrado(ex: RecursoNoEncontradoException): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiError(404, "No encontrado", ex.message ?: "El recurso no existe")
        )

    @ExceptionHandler(ReglaNegocioException::class)
    fun reglaNegocio(ex: ReglaNegocioException): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ApiError(400, "Solicitud invalida", ex.message ?: "No se pudo procesar la solicitud")
        )

    @ExceptionHandler(OperacionNoAutorizadaException::class)
    fun noAutorizado(ex: OperacionNoAutorizadaException): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ApiError(403, "Operacion no permitida", ex.message ?: "No tiene permiso sobre este recurso")
        )

    @ExceptionHandler(BadCredentialsException::class)
    fun credenciales(ex: BadCredentialsException): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiError(401, "Credenciales invalidas", "El correo o la clave no son correctos")
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validacion(ex: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val detalles = ex.bindingResult.fieldErrors.associate { campo ->
            campo.field to (campo.defaultMessage ?: "Valor invalido")
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ApiError(400, "Datos invalidos", "Revise los campos del formulario", detalles)
        )
    }
}
