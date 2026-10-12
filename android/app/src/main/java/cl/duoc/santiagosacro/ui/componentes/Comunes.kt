package cl.duoc.santiagosacro.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.santiagosacro.ui.theme.*

/** Etiqueta de color segun el estado de apertura del recinto. */
@Composable
fun EtiquetaEstado(estado: String, modifier: Modifier = Modifier) {
    val (texto, fondo, color) = when (estado) {
        "ABIERTO" -> Triple("Abierto", VerdeSuave, VerdeOscuro)
        "EN_CELEBRACION" -> Triple("En celebracion", Color(0xFFE4DCF0), Color(0xFF3B2F55))
        else -> Triple("Cerrado", Color(0xFFE8E2D6), Piedra)
    }

    Box(
        modifier = modifier
            .background(fondo, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = texto, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
fun CargandoCentrado(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Terracota)
    }
}

@Composable
fun MensajeVacio(texto: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            color = Piedra,
            textAlign = TextAlign.Center
        )
    }
}

/** Aviso de error con opcion de reintentar. */
@Composable
fun MensajeError(texto: String, onReintentar: (() -> Unit)? = null) {
    Surface(
        color = Color(0xFFF7E4DB),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyMedium,
                color = TerracotaOscuro,
                modifier = Modifier.weight(1f)
            )
            if (onReintentar != null) {
                TextButton(onClick = onReintentar) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = TerracotaOscuro)
                    Spacer(Modifier.width(4.dp))
                    Text("Reintentar", color = TerracotaOscuro)
                }
            }
        }
    }
}

/** Chip de filtro reutilizable. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipFiltro(texto: String, activo: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (activo) Terracota else PapelClaro,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (activo) Terracota else Borde
        ),
        onClick = onClick
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = if (activo) PapelClaro else Tinta,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}
