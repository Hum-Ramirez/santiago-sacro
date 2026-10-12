package cl.duoc.santiagosacro.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.santiagosacro.ui.componentes.*
import cl.duoc.santiagosacro.ui.theme.*
import cl.duoc.santiagosacro.ui.viewmodel.FichaViewModel

/** Ficha patrimonial del recinto. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFicha(
    idRecinto: Long,
    viewModel: FichaViewModel,
    onVolver: () -> Unit,
    onIrARuta: () -> Unit
) {
    LaunchedEffect(idRecinto) { viewModel.cargar(idRecinto) }

    val estado by viewModel.estado.collectAsState()
    val estadoSnackbar = remember { SnackbarHostState() }

    LaunchedEffect(estado.aviso) {
        estado.aviso?.let {
            estadoSnackbar.showSnackbar(it)
            viewModel.limpiarAviso()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(estadoSnackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Ficha patrimonial", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.alternarFavorito(idRecinto) }) {
                        Icon(
                            imageVector = if (estado.esFavorito) Icons.Default.Favorite
                            else Icons.Default.FavoriteBorder,
                            contentDescription = "Marcar como favorito",
                            tint = Terracota
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PapelClaro)
            )
        },
        containerColor = Papel
    ) { relleno ->

        when {
            estado.cargando -> CargandoCentrado(Modifier.padding(relleno))

            estado.error != null -> MensajeError(estado.error!!, onReintentar = {
                viewModel.cargar(idRecinto)
            })

            estado.detalle != null -> {
                val detalle = estado.detalle!!

                Column(
                    modifier = Modifier
                        .padding(relleno)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        detalle.tipo.uppercase() + " · " + detalle.comuna,
                        style = MaterialTheme.typography.labelSmall,
                        color = Piedra
                    )
                    Text(detalle.nombreOficial, style = MaterialTheme.typography.headlineMedium)

                    Spacer(Modifier.height(10.dp))
                    EtiquetaEstado(detalle.estadoApertura)

                    Spacer(Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = PapelClaro),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Horarios", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(6.dp))

                            if (detalle.horarios.isEmpty()) {
                                Text(
                                    "Sin horario publicado",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Piedra
                                )
                            } else {
                                detalle.horarios.forEach { horario ->
                                    Text(
                                        horario.nombreDia + ": " +
                                            horario.horaApertura + " a " + horario.horaCierre,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Piedra
                                    )
                                }
                            }

                            if (detalle.bloquesExcepcionales.isNotEmpty()) {
                                Spacer(Modifier.height(10.dp))
                                Text("Aperturas especiales", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                detalle.bloquesExcepcionales.forEach { bloque ->
                                    Text(
                                        bloque.fecha + ": " + bloque.horaInicio +
                                            " a " + bloque.horaTermino +
                                            (if (bloque.motivo.isNotBlank()) " (" + bloque.motivo + ")" else ""),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TerracotaOscuro
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("Direccion", style = MaterialTheme.typography.titleMedium)
                    Text(
                        detalle.direccion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Piedra
                    )

                    Spacer(Modifier.height(16.dp))
                    Text("Resena historica", style = MaterialTheme.typography.titleMedium)
                    Text(
                        detalle.descripcionHistorica.ifBlank {
                            "Sin resena historica cargada."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Piedra
                    )

                    if (detalle.eventosProximos.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Text("Proximas actividades", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(6.dp))
                        detalle.eventosProximos.forEach { evento ->
                            Text(
                                evento.fechaInicio + " · " + evento.titulo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Piedra
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.agregarARuta() },
                            colors = ButtonDefaults.buttonColors(containerColor = Terracota),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Agregar a mi ruta")
                        }

                        OutlinedButton(
                            onClick = onIrARuta,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Ver mi ruta")
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}
