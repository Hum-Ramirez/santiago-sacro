package cl.duoc.santiagosacro.exception

class RecursoNoEncontradoException(mensaje: String) : RuntimeException(mensaje)

class ReglaNegocioException(mensaje: String) : RuntimeException(mensaje)

class OperacionNoAutorizadaException(mensaje: String) : RuntimeException(mensaje)
