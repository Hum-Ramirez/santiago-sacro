package cl.duoc.santiagosacro.util

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Calculo de distancias sobre la superficie terrestre.
 * Se usa para ordenar los recintos por cercania a la posicion GPS del telefono.
 */
object Geo {

    private const val RADIO_TIERRA_KM = 6371.0

    /** Formula de Haversine. Devuelve la distancia en kilometros. */
    fun distanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return 2 * RADIO_TIERRA_KM * asin(sqrt(a))
    }
}
