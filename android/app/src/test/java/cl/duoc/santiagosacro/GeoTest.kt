package cl.duoc.santiagosacro

import cl.duoc.santiagosacro.core.Geo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeoTest {

    @Test
    fun `la distancia entre un punto y si mismo es cero`() {
        val distancia = Geo.distanciaKm(-33.4378, -70.6506, -33.4378, -70.6506)
        assertTrue(distancia < 0.0001)
    }

    @Test
    fun `calcula los minutos de caminata segun la distancia`() {
        // 4,5 km a 4,5 km/h deberia dar aproximadamente 60 minutos
        assertEquals(60, Geo.minutosCaminando(4.5))
    }

    @Test
    fun `nunca entrega menos de un minuto`() {
        assertEquals(1, Geo.minutosCaminando(0.01))
    }

    @Test
    fun `formatea distancias cortas en metros`() {
        assertEquals("650 m", Geo.formatearDistancia(0.65))
    }

    @Test
    fun `formatea distancias largas en kilometros`() {
        assertEquals("2.3 km", Geo.formatearDistancia(2.34))
    }
}
