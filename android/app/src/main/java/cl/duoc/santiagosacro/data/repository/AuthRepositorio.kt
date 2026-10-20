package cl.duoc.santiagosacro.data.repository

import cl.duoc.santiagosacro.core.Resultado
import cl.duoc.santiagosacro.core.SesionLocal
import cl.duoc.santiagosacro.data.local.Sembrador
import cl.duoc.santiagosacro.data.local.UsuarioLocal
import cl.duoc.santiagosacro.data.local.UsuarioDao
import cl.duoc.santiagosacro.data.remote.dto.AuthRespuesta

/**
 * Autenticacion contra la tabla usuario_local del telefono.
 *
 * En esta entrega la aplicacion no se conecta al backend, asi que la app
 * valida sola. Es una demostracion del flujo de acceso, no un control de
 * seguridad: quien tiene el dispositivo puede leer la tabla. La
 * autenticacion real vive en el backend, donde la clave se guarda con
 * BCrypt en el campo claveHash y se responde con un JWT firmado.
 *
 * Las firmas no cambiaron, asi que el AccesoViewModel sigue igual.
 */
class AuthRepositorio(
    private val usuarioDao: UsuarioDao,
    private val sesion: SesionLocal,
    private val sembrador: Sembrador
) {

    /**
     * Valida el acceso en dos pasos separados a proposito:
     * primero busca por correo, despues compara la clave.
     *
     * Se podria resolver en una sola consulta con
     * "WHERE correo = :correo AND clave = :clave", pero entonces la decision
     * de que significa "clave correcta" quedaria escrita dentro del SQL. El
     * DAO busca; el repositorio decide.
     */
    suspend fun login(correo: String, clave: String): Resultado<AuthRespuesta> {
        sembrador.sembrarSiHaceFalta()

        val usuario = usuarioDao.porCorreo(correo.trim().lowercase())
            ?: return Resultado.Error("No existe una cuenta con ese correo")

        if (usuario.clave != clave) {
            return Resultado.Error("La clave no es correcta")
        }

        val respuesta = construirRespuesta(usuario)
        guardarSesion(respuesta)
        return Resultado.Exito(respuesta)
    }

    /**
     * Registro de visitante. Siempre crea el usuario con rol VISITANTE:
     * nadie puede registrarse y asignarse el rol de encargado, igual que en
     * el backend.
     *
     * El insert del DAO no lleva estrategia de conflicto, asi que un correo
     * repetido choca con el indice unico y lanza excepcion. Eso es lo que se
     * quiere: es un error de negocio que la app tiene que informar, no algo
     * que deba resolverse en silencio sobrescribiendo la cuenta del otro.
     */
    suspend fun registrar(
        nombre: String,
        correo: String,
        clave: String
    ): Resultado<AuthRespuesta> {
        sembrador.sembrarSiHaceFalta()

        val correoLimpio = correo.trim().lowercase()

        if (usuarioDao.porCorreo(correoLimpio) != null) {
            return Resultado.Error("Ya existe una cuenta registrada con ese correo")
        }

        return try {
            usuarioDao.insertar(
                UsuarioLocal(
                    correo = correoLimpio,
                    clave = clave,
                    nombre = nombre.trim(),
                    rol = "VISITANTE"
                )
            )

            val guardado = usuarioDao.porCorreo(correoLimpio)
                ?: return Resultado.Error("No se pudo crear la cuenta")

            val respuesta = construirRespuesta(guardado)
            guardarSesion(respuesta)
            Resultado.Exito(respuesta)
        } catch (e: Exception) {
            Resultado.Error("No se pudo crear la cuenta: " + (e.message ?: "error desconocido"))
        }
    }

    fun cerrarSesion() = sesion.cerrar()

    fun haySesion(): Boolean = sesion.haySesion

    fun esEncargado(): Boolean = sesion.esEncargado

    fun nombreUsuario(): String = sesion.nombre ?: "Visitante"

    /** El recinto que administra el usuario de la sesion, si es encargado. */
    suspend fun recintoAdministrado(): Long? {
        val correo = sesion.correo ?: return null
        return usuarioDao.porCorreo(correo)?.idRecintoAdministrado
    }

    /**
     * Arma la misma respuesta que devolvia el backend. El token no es un JWT
     * firmado sino una marca local: SesionLocal solo necesita un texto no
     * vacio para considerar que hay sesion abierta, y eso es lo que mantiene
     * el caso de uso de no volver a iniciar sesion al reabrir la app.
     */
    private fun construirRespuesta(usuario: UsuarioLocal) = AuthRespuesta(
        token = "local-" + usuario.id,
        tipo = "Local",
        correo = usuario.correo,
        nombre = usuario.nombre,
        rol = usuario.rol,
        expiraEnMs = 0L
    )

    private fun guardarSesion(datos: AuthRespuesta) {
        sesion.token = datos.token
        sesion.nombre = datos.nombre
        sesion.correo = datos.correo
        sesion.rol = datos.rol
    }
}
