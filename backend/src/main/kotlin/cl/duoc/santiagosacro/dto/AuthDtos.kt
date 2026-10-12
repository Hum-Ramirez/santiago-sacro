package cl.duoc.santiagosacro.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegistroRequest(
    @field:NotBlank(message = "El nombre es obligatorio")
    val nombre: String = "",

    @field:Email(message = "El correo no tiene un formato valido")
    @field:NotBlank(message = "El correo es obligatorio")
    val correo: String = "",

    @field:Size(min = 6, message = "La clave debe tener al menos 6 caracteres")
    val clave: String = ""
)

data class LoginRequest(
    @field:NotBlank(message = "El correo es obligatorio")
    val correo: String = "",

    @field:NotBlank(message = "La clave es obligatoria")
    val clave: String = ""
)

data class AuthResponse(
    val token: String,
    val tipo: String = "Bearer",
    val correo: String,
    val nombre: String,
    val rol: String,
    val expiraEnMs: Long
)

data class PerfilResponse(
    val id: Long,
    val correo: String,
    val nombre: String,
    val rol: String,
    val recintosAdministrados: List<RecintoResumenResponse>
)
