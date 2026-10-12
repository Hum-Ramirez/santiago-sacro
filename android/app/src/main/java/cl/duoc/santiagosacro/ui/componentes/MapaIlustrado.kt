package cl.duoc.santiagosacro.ui.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cl.duoc.santiagosacro.data.remote.dto.RecintoResumen
import cl.duoc.santiagosacro.ui.theme.*
import kotlin.math.abs

/**
 * Mapa ilustrado dibujado con Canvas.
 * Proyecta las coordenadas reales de los recintos sobre el lienzo,
 * sin depender de una API externa de mapas.
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun MapaIlustrado(
    recintos: List<RecintoResumen>,
    seleccionado: RecintoResumen?,
    onSeleccionar: (RecintoResumen) -> Unit,
    modifier: Modifier = Modifier
) {
    val medidor = rememberTextMeasurer()

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(recintos) {
                    detectTapGestures { toque ->
                        val puntos = proyectar(recintos, size.width.toFloat(), size.height.toFloat())
                        val cercano = puntos.minByOrNull { (_, punto) ->
                            abs(punto.x - toque.x) + abs(punto.y - toque.y)
                        }
                        if (cercano != null) {
                            val distancia = abs(cercano.second.x - toque.x) +
                                abs(cercano.second.y - toque.y)
                            if (distancia < 140f) onSeleccionar(cercano.first)
                        }
                    }
                }
        ) {
            dibujarFondo()

            val puntos = proyectar(recintos, size.width, size.height)

            puntos.forEach { (recinto, punto) ->
                val destacado = recinto.id == seleccionado?.id
                dibujarTemplo(punto, destacado)

                val etiqueta = medidor.measure(
                    text = recinto.nombreOficial,
                    style = TextStyle(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (destacado) PapelClaro else Tinta
                    )
                )

                val anchoEtiqueta = etiqueta.size.width.toFloat()
                val xEtiqueta = (punto.x - anchoEtiqueta / 2)
                    .coerceIn(8f, size.width - anchoEtiqueta - 8f)
                val yEtiqueta = punto.y + 26f

                drawRoundRect(
                    color = if (destacado) Terracota else PapelClaro,
                    topLeft = Offset(xEtiqueta - 6f, yEtiqueta - 3f),
                    size = Size(anchoEtiqueta + 12f, etiqueta.size.height + 6f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                )

                drawText(etiqueta, topLeft = Offset(xEtiqueta, yEtiqueta))
            }
        }
    }
}

/** Convierte latitud y longitud en coordenadas del lienzo. */
private fun proyectar(
    recintos: List<RecintoResumen>,
    ancho: Float,
    alto: Float
): List<Pair<RecintoResumen, Offset>> {
    if (recintos.isEmpty()) return emptyList()

    val margen = 60f
    val latitudes = recintos.map { it.latitud }
    val longitudes = recintos.map { it.longitud }

    val latMin = latitudes.min()
    val latMax = latitudes.max()
    val lonMin = longitudes.min()
    val lonMax = longitudes.max()

    val rangoLat = (latMax - latMin).takeIf { it > 0.0001 } ?: 0.01
    val rangoLon = (lonMax - lonMin).takeIf { it > 0.0001 } ?: 0.01

    return recintos.map { recinto ->
        // La longitud crece hacia el este y la latitud hacia el norte,
        // por eso el eje Y se invierte.
        val x = margen + ((recinto.longitud - lonMin) / rangoLon).toFloat() * (ancho - margen * 2)
        val y = margen + (1 - ((recinto.latitud - latMin) / rangoLat)).toFloat() * (alto - margen * 2.4f)
        recinto to Offset(x, y)
    }
}

/** Manzanas, calles y rio del mapa ilustrado. */
private fun DrawScope.dibujarFondo() {
    drawRect(color = MapaFondo, size = size)

    val anchoBloque = size.width / 3.4f
    val altoBloque = size.height / 4.2f

    for (fila in 0..3) {
        for (columna in 0..2) {
            drawRoundRect(
                color = VerdeSalvia,
                topLeft = Offset(
                    16f + columna * (anchoBloque + 14f),
                    16f + fila * (altoBloque + 14f)
                ),
                size = Size(anchoBloque, altoBloque),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
            )
        }
    }

    val rio = Path().apply {
        moveTo(-20f, size.height * 0.46f)
        cubicTo(
            size.width * 0.3f, size.height * 0.36f,
            size.width * 0.6f, size.height * 0.56f,
            size.width + 20f, size.height * 0.42f
        )
    }
    drawPath(path = rio, color = Agua, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 26f))
}

/** Silueta de templo: nave, techo, torre y cruz. */
private fun DrawScope.dibujarTemplo(centro: Offset, destacado: Boolean) {
    val colorNave = if (destacado) Madera else MaderaClara
    val x = centro.x
    val y = centro.y

    // Nave
    drawRoundRect(
        color = colorNave,
        topLeft = Offset(x - 16f, y),
        size = Size(32f, 18f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
    )

    // Torre
    drawRect(color = Madera, topLeft = Offset(x - 6f, y - 14f), size = Size(12f, 14f))

    // Techo de la torre
    val techo = Path().apply {
        moveTo(x - 8f, y - 14f)
        lineTo(x, y - 24f)
        lineTo(x + 8f, y - 14f)
        close()
    }
    drawPath(techo, color = Tinta)

    // Cruz
    drawRect(color = Terracota, topLeft = Offset(x - 1.5f, y - 34f), size = Size(3f, 10f))
    drawRect(color = Terracota, topLeft = Offset(x - 5f, y - 31f), size = Size(10f, 3f))

    // Puerta
    drawRoundRect(
        color = Papel,
        topLeft = Offset(x - 3.5f, y + 7f),
        size = Size(7f, 11f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
    )

    if (destacado) {
        drawCircle(color = Terracota, radius = 7f, center = Offset(x, y - 44f))
        drawCircle(color = PapelClaro, radius = 3f, center = Offset(x, y - 44f))
    }
}
