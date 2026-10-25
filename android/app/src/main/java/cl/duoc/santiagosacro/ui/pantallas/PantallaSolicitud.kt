package cl.duoc.santiagosacro.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.santiagosacro.ui.theme.*
import cl.duoc.santiagosacro.ui.viewmodel.SolicitudViewModel

/**
 * Solicitud de guia voluntario y seguimiento de su estado.
 *
 * Sin sesion iniciada no se muestra el formulario: una solicitud queda a
 * nombre de una persona, porque el guia necesita saber a quien espera.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSolicitud(
    idRecinto: Long,
    viewModel: SolicitudViewModel,
    onVolver: () -> Unit,
    onIrAAcceso: () -> Unit
) {
    LaunchedEffect(idRecinto) { viewModel.cargar(idRecinto) }

    val estado by viewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar guia", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PapelClaro)
            )
        },
        containerColor = Papel
    ) { relleno ->

        Column(
            modifier = Modifier
                .padding(relleno)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Text("Visita acompanada", style = MaterialTheme.typography.labelSmall, color = Piedra)
            Text(estado.nombreRecinto, style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(16.dp))

            if (!estado.haySesion) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PapelClaro),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Debe iniciar sesion para solicitar un guia voluntario",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "La solicitud queda a su nombre para que el guia sepa a quien espera.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Piedra
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onIrAAcceso,
                            colors = ButtonDefaults.buttonColors(containerColor = Terracota),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ir a iniciar sesion")
                        }
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PapelClaro),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = estado.fecha,
                                onValueChange = viewModel::fechaCambio,
                                label = { Text("Fecha") },
                                placeholder = { Text("2026-11-15") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = estado.hora,
                                onValueChange = viewModel::horaCambio,
                                label = { Text("Hora") },
                                placeholder = { Text("10:00") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = estado.tamanoGrupo,
                            onValueChange = viewModel::grupoCambio,
                            label = { Text("Personas en el grupo") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = estado.contacto,
                            onValueChange = viewModel::contactoCambio,
                            label = { Text("Telefono o correo de contacto") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        estado.error?.let { mensaje ->
                            Spacer(Modifier.height(10.dp))
                            Text(
                                mensaje,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TerracotaOscuro
                            )
                        }

                        estado.mensaje?.let { mensaje ->
                            Spacer(Modifier.height(10.dp))
                            Text(
                                mensaje,
                                style = MaterialTheme.typography.bodyMedium,
                                color = VerdeOscuro
                            )
                        }

                        Spacer(Modifier.height(18.dp))

                        Button(
                            onClick = { viewModel.enviar(idRecinto) },
                            enabled = !estado.enviando,
                            colors = ButtonDefaults.buttonColors(containerColor = Terracota),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (estado.enviando) {
                                CircularProgressIndicator(
                                    color = PapelClaro,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text("Enviar solicitud")
                            }
                        }
                    }
                }
            }

            if (estado.misSolicitudes.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Text("Mis solicitudes", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                estado.misSolicitudes.forEach { solicitud ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PapelClaro),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                solicitud.nombreRecinto,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                solicitud.fechaVisita + " a las " + solicitud.horaVisita +
                                    " · " + solicitud.tamanoGrupo + " personas",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Piedra
                            )

                            Spacer(Modifier.height(8.dp))

                            Surface(
                                color = when (solicitud.estado) {
                                    "ACEPTADA" -> VerdeSuave
                                    "RECHAZADA" -> PapelClaro
                                    else -> PapelClaro
                                },
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                Text(
                                    solicitud.estado,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when (solicitud.estado) {
                                        "ACEPTADA" -> VerdeOscuro
                                        "RECHAZADA" -> TerracotaOscuro
                                        else -> Piedra
                                    },
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            solicitud.guiaAsignado?.let { guia ->
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Guia asignado: " + guia,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = VerdeOscuro
                                )
                            }

                            solicitud.motivoRechazo?.let { motivo ->
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    motivo,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TerracotaOscuro
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
