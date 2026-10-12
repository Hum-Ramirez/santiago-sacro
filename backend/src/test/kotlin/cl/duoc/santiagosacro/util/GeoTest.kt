package cl.duoc.santiagosacro.util

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class GeoTest {

    @Test
    fun `la distancia entre un punto y si mismo es cero`() {
        val distancia = Geo.distanciaKm(-33.4378, -70.6506, -33.4378, -70.6506)
        assertTrue(distancia < 0.0001, "Esperaba 0 km y obtuve " + distancia)
    }

    @Test
    fun `la distancia entre la Catedral y San Francisco es menor a dos kilometros`() {
        // Coordenadas sinteticas usadas en la carga inicial
        val distancia = Geo.distanciaKm(-33.4378, -70.6506, -33.4450, -70.6490)
        assertTrue(distancia in 0.5..2.0, "Distancia fuera del rango esperado: " + distancia)
    }

    @Test
    fun `la distancia es simetrica`() {
        val ida = Geo.distanciaKm(-33.4378, -70.6506, -33.4512, -70.6494)
        val vuelta = Geo.distanciaKm(-33.4512, -70.6494, -33.4378, -70.6506)
        assertTrue(abs(ida - vuelta) < 0.0001)
    }
}
