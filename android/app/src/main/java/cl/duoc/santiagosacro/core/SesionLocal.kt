package cl.duoc.santiagosacro.core

import android.content.Context
import android.content.SharedPreferences

/**
 * Guarda el token JWT y los datos basicos de la sesion en el dispositivo.
 * No se guarda en Room: no es informacion del dominio, es configuracion del telefono.
 */
class SesionLocal(contexto: Context) {

    private val prefs: SharedPreferences =
        contexto.getSharedPreferences("sesion_santiago_sacro", Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString(CLAVE_TOKEN, null)
        set(valor) = prefs.edit().putString(CLAVE_TOKEN, valor).apply()

    var nombre: String?
        get() = prefs.getString(CLAVE_NOMBRE, null)
        set(valor) = prefs.edit().putString(CLAVE_NOMBRE, valor).apply()

    var correo: String?
        get() = prefs.getString(CLAVE_CORREO, null)
        set(valor) = prefs.edit().putString(CLAVE_CORREO, valor).apply()

    var rol: String?
        get() = prefs.getString(CLAVE_ROL, null)
        set(valor) = prefs.edit().putString(CLAVE_ROL, valor).apply()

    val haySesion: Boolean get() = !token.isNullOrBlank()

    val esEncargado: Boolean get() = rol == "ENCARGADO"

    fun cerrar() = prefs.edit().clear().apply()

    private companion object {
        const val CLAVE_TOKEN = "token"
        const val CLAVE_NOMBRE = "nombre"
        const val CLAVE_CORREO = "correo"
        const val CLAVE_ROL = "rol"
    }
}
