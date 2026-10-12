package cl.duoc.santiagosacro.ui.pantallas

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.santiagosacro.ui.componentes.CargandoCentrado
import cl.duoc.santiagosacro.ui.componentes.ChipFiltro
import cl.duoc.santiagosacro.ui.componentes.MensajeError
import cl.duoc.santiagosacro.ui.componentes.MensajeVacio
import cl.duoc.santiagosacro.ui.theme.*
import cl.duoc.santiagosacro.ui.viewmodel.CarteleraViewModel

/** Cartelera cultural de toda la red. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCartelera(
    viewModel: CarteleraViewModel,
    onAbrirRecinto: (Long) -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        Surface(color = PapelClaro) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Cartelera", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Actividades de la red patrimonial",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Piedra
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    estado.categorias.forEach { categoria ->
                        ChipFiltro(
                            texto = categoria.nombre,
                            activo = estado.categoriaSeleccionada == categoria.id,
                            onClick = { viewModel.filtrarPor(categoria.id) }
                        )
                    }
                }
            }
        }

        estado.error?.let { MensajeError(it, onReintentar = { viewModel.cargar(null) }) }

        when {
            estado.cargando -> CargandoCentrado(Modifier.weight(1f))

            estado.eventos.isEmpty() -> MensajeVacio(
                "No hay actividades publicadas en este periodo.",
                Modifier.weight(1f)
            )

            else -> LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(estado.eventos, key = { it.id }) { evento ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PapelClaro),
                        shape = RoundedCornerShape(14.dp),
                        onClick = { onAbrirRecinto(evento.idRecinto) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(54.dp)
                            ) {
                                Text(
                                    evento.fechaInicio.takeLast(2),
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Text(
                                    mesCorto(evento.fechaInicio),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Piedra
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(evento.titulo, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    evento.nombreRecinto + " · " + evento.horaInicio.take(5),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Piedra
                                )
                                Spacer(Modifier.height(6.dp))
                                Surface(
                                    color = VerdeSuave,
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        evento.categoria,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VerdeOscuro,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Convierte 2026-10-15 en OCT. */
private fun mesCorto(fechaIso: String): String {
    val meses = listOf("ENE", "FEB", "MAR", "ABR", "MAY", "JUN",
        "JUL", "AGO", "SEP", "OCT", "NOV", "DIC")
    val partes = fechaIso.split("-")
    val mes = partes.getOrNull(1)?.toIntOrNull() ?: return ""
    return meses.getOrElse(mes - 1) { "" }
}
