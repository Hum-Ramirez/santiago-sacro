package cl.duoc.santiagosacro.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.santiagosacro.ui.componentes.MensajeVacio
import cl.duoc.santiagosacro.ui.theme.*
import cl.duoc.santiagosacro.ui.viewmodel.RutaViewModel

/**
 * Ruta patrimonial del visitante.
 * Las paradas viven en Room: siguen disponibles sin conexion.
 */
@Composable
fun PantallaRuta(
    viewModel: RutaViewModel,
    onAbrirRecinto: (Long) -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        Surface(color = PapelClaro) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mi ruta patrimonial", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = estado.paradas.size.toString() + " paradas · " +
                            estado.minutosTotales + " min estimados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Piedra
                    )
                }

                if (estado.paradas.isNotEmpty()) {
                    TextButton(onClick = { viewModel.vaciar() }) {
                        Text("Vaciar", color = TerracotaOscuro)
                    }
                }
            }
        }

        if (estado.paradas.isEmpty()) {
            MensajeVacio(
                "Tu ruta esta vacia. Abre la ficha de un recinto y usa " +
                    "Agregar a mi ruta para comenzar a armar el recorrido.",
                Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(estado.paradas, key = { _, parada -> parada.id }) { indice, parada ->

                    Card(
                        colors = CardDefaults.cardColors(containerColor = PapelClaro),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(color = Terracota, shape = CircleShape) {
                                Text(
                                    text = (indice + 1).toString(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = PapelClaro,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .wrapContentHeight()
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Text(
                                text = parada.nombreRecinto,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .weight(1f)
                            )

                            TextButton(onClick = { onAbrirRecinto(parada.idRecinto) }) {
                                Text("Ver", color = TerracotaOscuro)
                            }

                            IconButton(onClick = { viewModel.quitar(parada.id) }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Quitar " + parada.nombreRecinto,
                                    tint = Piedra
                                )
                            }
                        }
                    }

                    // Tramo hasta la siguiente parada
                    if (indice < estado.tramos.size) {
                        val tramo = estado.tramos[indice]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.DirectionsWalk,
                                contentDescription = null,
                                tint = Piedra,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = tramo.minutos.toString() + " min caminando · " + tramo.distancia,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Piedra
                            )
                        }
                    }
                }
            }
        }
    }
}
