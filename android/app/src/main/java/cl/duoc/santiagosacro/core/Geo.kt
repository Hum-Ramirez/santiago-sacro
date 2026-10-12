package cl.duoc.santiagosacro.core

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/** Calculos geograficos usados por el mapa y por el armado de rutas. */
object Geo {

    private const val RADIO_TIERRA_KM = 6371.0
    private const val VELOCIDAD_CAMINATA_KMH = 4.5

    fun distanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return 2 * RADIO_TIERRA_KM * asin(sqrt(a))
    }

    /** Tiempo estimado de traslado a pie, en minutos. */
    fun minutosCaminando(distanciaKm: Double): Int =
        ((distanciaKm / VELOCIDAD_CAMINATA_KMH) * 60).roundToInt().coerceAtLeast(1)

    fun formatearDistancia(km: Double): String =
        if (km < 1.0) (km * 1000).roundToInt().toString() + " m"
        else (Math.round(km * 10) / 10.0).toString() + " km"
}
