package cl.duoc.santiagosacro.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.santiagosacro.ui.componentes.CargandoCentrado
import cl.duoc.santiagosacro.ui.componentes.EtiquetaEstado
import cl.duoc.santiagosacro.ui.componentes.MensajeVacio
import cl.duoc.santiagosacro.ui.theme.*
import cl.duoc.santiagosacro.ui.viewmodel.FavoritosViewModel

/** Recintos marcados como favoritos. Se resuelve 100% con Room. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFavoritos(
    viewModel: FavoritosViewModel,
    onAbrirRecinto: (Long) -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        Surface(color = PapelClaro) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Favoritos", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Guardados en este telefono",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Piedra
                )
            }
        }

        when {
            estado.cargando -> CargandoCentrado(Modifier.weight(1f))

            estado.favoritos.isEmpty() -> MensajeVacio(
                "Todavia no marcas favoritos. Usa el corazon en la ficha de un recinto.",
                Modifier.weight(1f)
            )

            else -> LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(estado.favoritos, key = { it.idRecinto }) { recinto ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PapelClaro),
                        shape = RoundedCornerShape(14.dp),
                        onClick = { onAbrirRecinto(recinto.idRecinto) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(recinto.nombre, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    recinto.tipo + " · " + recinto.comuna,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Piedra
                                )
                            }
                            EtiquetaEstado(recinto.estadoApertura)
                        }
                    }
                }
            }
        }
    }
}
