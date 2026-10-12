package cl.duoc.santiagosacro.domain

/** Estados de apertura definidos explicitamente en el caso. */
enum class EstadoApertura {
    ABIERTO,
    CERRADO,
    EN_CELEBRACION
}

/** Ciclo de vida de un evento publicado por el encargado. */
enum class EstadoEvento {
    PUBLICADO,
    CANCELADO
}
