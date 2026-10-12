package cl.duoc.santiagosacro.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.santiagosacro.core.Geo
import cl.duoc.santiagosacro.ui.componentes.*
import cl.duoc.santiagosacro.ui.theme.*
import cl.duoc.santiagosacro.ui.viewmodel.MapaViewModel

/** Pantalla principal del visitante: mapa de la red patrimonial. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(
    viewModel: MapaViewModel,
    onAbrirFicha: (Long) -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val visibles = if (estado.filtroAbiertos) {
        estado.recintos.filter { it.estadoApertura == "ABIERTO" }
    } else {
        estado.recintos
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Surface(color = PapelClaro, tonalElevation = 0.dp) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text("Santiago Sacro", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Templos, arte e historia de Santiago",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Piedra
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = estado.textoBusqueda,
                    onValueChange = { viewModel.buscar(it) },
                    placeholder = { Text("Buscar templo o comuna") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipFiltro(
                        texto = "Abiertos ahora",
                        activo = estado.filtroAbiertos,
                        onClick = { viewModel.alternarFiltroAbiertos() }
                    )
                    ChipFiltro(
                        texto = if (estado.usandoGps) "Ordenado por cercania" else "Sin GPS",
                        activo = estado.usandoGps,
                        onClick = { viewModel.cargar() }
                    )
                }
            }
        }

        if (estado.mensaje != null) {
            MensajeError(estado.mensaje!!, onReintentar = { viewModel.cargar() })
        }

        when {
            estado.cargando -> CargandoCentrado(Modifier.weight(1f))

            visibles.isEmpty() -> MensajeVacio(
                "No hay recintos para mostrar. Verifique que el backend este en ejecucion.",
                Modifier.weight(1f)
            )

            else -> {
                MapaIlustrado(
                    recintos = visibles,
                    seleccionado = estado.seleccionado,
                    onSeleccionar = { viewModel.seleccionar(it) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                estado.seleccionado?.let { recinto ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PapelClaro),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    recinto.nombreOficial,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    EtiquetaEstado(recinto.estadoApertura)
                                    val distancia = recinto.distanciaKm
                                    Text(
                                        text = if (distancia != null) {
                                            recinto.comuna + " · " + Geo.formatearDistancia(distancia)
                                        } else {
                                            recinto.comuna
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Piedra
                                    )
                                }
                            }

                            Button(
                                onClick = { onAbrirFicha(recinto.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Terracota),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Ver ficha")
                            }
                        }
                    }
                }
            }
        }
    }
}
