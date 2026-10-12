package cl.duoc.santiagosacro.data.repository

import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.core.SesionLocal
import cl.duoc.santiagosacro.core.llamadaSegura
import cl.duoc.santiagosacro.data.remote.ApiSantiagoSacro
import cl.duoc.santiagosacro.data.remote.dto.AuthRespuesta
import cl.duoc.santiagosacro.data.remote.dto.LoginPeticion
import cl.duoc.santiagosacro.data.remote.dto.RegistroPeticion

class AuthRepositorio(
    private val api: ApiSantiagoSacro,
    private val sesion: SesionLocal
) {

    suspend fun login(correo: String, clave: String): Resultado<AuthRespuesta> {
        val resultado = llamadaSegura { api.login(LoginPeticion(correo.trim(), clave)) }
        if (resultado is Resultado.Exito) guardarSesion(resultado.datos)
        return resultado
    }

    suspend fun registrar(nombre: String, correo: String, clave: String): Resultado<AuthRespuesta> {
        val resultado = llamadaSegura {
            api.registrar(RegistroPeticion(nombre.trim(), correo.trim(), clave))
        }
        if (resultado is Resultado.Exito) guardarSesion(resultado.datos)
        return resultado
    }

    fun cerrarSesion() = sesion.cerrar()

    fun haySesion(): Boolean = sesion.haySesion

    fun esEncargado(): Boolean = sesion.esEncargado

    fun nombreUsuario(): String = sesion.nombre ?: "Visitante"

    private fun guardarSesion(datos: AuthRespuesta) {
        sesion.token = datos.token
        sesion.nombre = datos.nombre
        sesion.correo = datos.correo
        sesion.rol = datos.rol
    }
}
