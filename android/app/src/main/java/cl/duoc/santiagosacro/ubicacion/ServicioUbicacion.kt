package cl.duoc.santiagosacro.ubicacion

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/** Posicion del telefono entregada por el GPS. */
data class Posicion(val latitud: Double, val longitud: Double)

/**
 * Acceso al GPS del dispositivo mediante los servicios de Google Play.
 * Es el recurso nativo exigido por el caso.
 */
class ServicioUbicacion(private val contexto: Context) {

    private val cliente = LocationServices.getFusedLocationProviderClient(contexto)

    fun hayPermiso(): Boolean =
        ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    suspend fun posicionActual(): Posicion? {
        if (!hayPermiso()) return null

        return suspendCancellableCoroutine { continuacion ->
            cliente.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { ubicacion ->
                    val posicion = ubicacion?.let { Posicion(it.latitude, it.longitude) }
                    continuacion.resume(posicion)
                }
                .addOnFailureListener {
                    continuacion.resume(null)
                }
        }
    }

    companion object {
        /** Punto de referencia cuando el usuario no entrega su ubicacion. */
        val PLAZA_DE_ARMAS = Posicion(-33.4378, -70.6506)
    }
}
