package cl.duoc.santiagosacro.dto

import java.time.LocalDateTime

data class ApiError(
    val estado: Int,
    val error: String,
    val mensaje: String,
    val detalles: Map<String, String>? = null,
    val momento: LocalDateTime = LocalDateTime.now()
)
