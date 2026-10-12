package cl.duoc.santiagosacro.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.santiagosacro.ui.componentes.CargandoCentrado
import cl.duoc.santiagosacro.ui.componentes.ChipFiltro
import cl.duoc.santiagosacro.ui.componentes.MensajeError
import cl.duoc.santiagosacro.ui.theme.*
import cl.duoc.santiagosacro.ui.viewmodel.PanelViewModel

/**
 * Panel del encargado de templo.
 * Reune las tres funciones administrativas de la primera entrega:
 * estado del recinto, publicacion de eventos y gestion de solicitudes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPanel(
    viewModel: PanelViewModel,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val estadoSnackbar = remember { SnackbarHostState() }

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var idCategoria by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(estado.mensaje, estado.error) {
        val texto = estado.mensaje ?: estado.error
        if (texto != null) {
            estadoSnackbar.showSnackbar(texto)
            viewModel.limpiarMensajes()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(estadoSnackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Panel del encargado",
                            style = MaterialTheme.typography.labelSmall,
                            color = Papel
                        )
                        Text(
                            estado.recintoActivo?.nombreOficial ?: "Sin recinto asignado",
                            style = MaterialTheme.typography.titleLarge,
                            color = Papel
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Papel
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Tinta)
            )
        },
        containerColor = Papel
    ) { relleno ->

        if (estado.cargando) {
            CargandoCentrado(Modifier.padding(relleno))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(relleno)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            if (estado.error != null && estado.recintos.isEmpty()) {
                MensajeError(estado.error!!, onReintentar = { viewModel.cargar() })
            }

            // ------------------------------------------------ recintos a cargo
            if (estado.recintos.size > 1) {
                Text("Mis recintos", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    estado.recintos.forEach { recinto ->
                        ChipFiltro(
                            texto = recinto.nombreOficial,
                            activo = estado.recintoActivo?.id == recinto.id,
                            onClick = { viewModel.seleccionarRecinto(recinto) }
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // ------------------------------------------------ estado del recinto
            Text("Estado del recinto", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "ABIERTO" to "Abierto",
                    "CERRADO" to "Cerrado",
                    "EN_CELEBRACION" to "En celebracion"
                ).forEach { (valor, etiqueta) ->
                    ChipFiltro(
                        texto = etiqueta,
                        activo = estado.recintoActivo?.estadoApertura == valor,
                        onClick = { viewModel.cambiarEstado(valor) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ------------------------------------------------ publicar evento
            Card(
                colors = CardDefaults.cardColors(containerColor = PapelClaro),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Publicar evento", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Titulo") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripcion") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = fecha,
                            onValueChange = { fecha = it },
                            label = { Text("Fecha") },
                            placeholder = { Text("2026-11-15") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = hora,
                            onValueChange = { hora = it },
                            label = { Text("Hora") },
                            placeholder = { Text("19:30") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Text("Categoria", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        estado.categorias.take(3).forEach { categoria ->
                            ChipFiltro(
                                texto = categoria.nombre,
                                activo = idCategoria == categoria.id,
                                onClick = { idCategoria = categoria.id }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val categoria = idCategoria ?: estado.categorias.firstOrNull()?.id
                            if (categoria != null) {
                                viewModel.publicarEvento(
                                    titulo = titulo,
                                    descripcion = descripcion,
                                    idCategoria = categoria,
                                    fecha = fecha.trim(),
                                    hora = if (hora.trim().length == 5) hora.trim() + ":00"
                                    else hora.trim()
                                )
                                titulo = ""
                                descripcion = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Terracota),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Publicar")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ------------------------------------------------ solicitudes de guia
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Solicitudes de guia",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                val pendientes = estado.solicitudes.count { it.estado == "PENDIENTE" }
                Surface(color = Terracota, shape = RoundedCornerShape(999.dp)) {
                    Text(
                        pendientes.toString() + " pendientes",
                        style = MaterialTheme.typography.labelSmall,
                        color = PapelClaro,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            if (estado.solicitudes.isEmpty()) {
                Text(
                    "No hay solicitudes registradas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Piedra
                )
            }

            estado.solicitudes.forEach { solicitud ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = PapelClaro),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            solicitud.contacto + " · " + solicitud.tamanoGrupo + " personas",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            solicitud.fechaVisita + " a las " + solicitud.horaVisita.take(5) +
                                " · " + solicitud.estado,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Piedra
                        )

                        solicitud.guiaAsignado?.let {
                            Text(
                                "Guia asignado: " + it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = VerdeOscuro
                            )
                        }

                        if (solicitud.estado == "PENDIENTE") {
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        val guia = estado.guias.firstOrNull { it.disponible }
                                        viewModel.gestionarSolicitud(
                                            solicitud.id,
                                            aceptar = true,
                                            idGuia = guia?.id
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Aceptar")
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.gestionarSolicitud(
                                            solicitud.id,
                                            aceptar = false,
                                            idGuia = null
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Rechazar")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
