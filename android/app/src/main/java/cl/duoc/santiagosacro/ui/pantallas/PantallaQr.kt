package cl.duoc.santiagosacro.ui.pantallas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.santiagosacro.ui.theme.*
import cl.duoc.santiagosacro.ui.viewmodel.QrViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

/**
 * Lector de los codigos QR de las placas de los recintos.
 *
 * Usa el Google Code Scanner de ML Kit: la pantalla de escaneo la muestra
 * Google Play Services y la aplicacion solo recibe el texto leido. Por eso
 * no se declara el permiso de camara ni se piden permisos en tiempo de
 * ejecucion: esta app nunca abre la camara.
 */
@Composable
fun PantallaQr(
    viewModel: QrViewModel,
    onAbrirRecinto: (Long) -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val contexto = LocalContext.current

    // Se limita a QR: el lector ignora codigos de barras de otro tipo.
    val opciones = remember {
        GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    }

    val escaner = remember(contexto, opciones) {
        GmsBarcodeScanning.getClient(contexto, opciones)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(24.dp))

        Text("Codigo del templo", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(6.dp))
        Text(
            "Escanee la placa de la entrada para abrir su ficha patrimonial",
            style = MaterialTheme.typography.bodyMedium,
            color = Piedra
        )

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = {
                escaner.startScan()
                    .addOnSuccessListener { codigo ->
                        viewModel.procesarQr(codigo.rawValue.orEmpty())
                    }
                    .addOnCanceledListener {
                        // El usuario cerro el lector: no hay nada que hacer.
                    }
                    .addOnFailureListener { fallo ->
                        Toast.makeText(
                            contexto,
                            "No se pudo abrir el lector: " + fallo.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            },
            enabled = !estado.verificando,
            colors = ButtonDefaults.buttonColors(containerColor = Terracota),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Escanear codigo QR")
        }

        Spacer(Modifier.height(28.dp))

        when {
            estado.verificando -> CircularProgressIndicator(color = Terracota)

            estado.error != null -> Card(
                colors = CardDefaults.cardColors(containerColor = PapelClaro),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        estado.error!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TerracotaOscuro
                    )
                    if (estado.contenido.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Contenido leido: " + estado.contenido,
                            style = MaterialTheme.typography.labelSmall,
                            color = Piedra
                        )
                    }
                }
            }

            estado.idRecinto != null -> Card(
                colors = CardDefaults.cardColors(containerColor = PapelClaro),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Recinto reconocido",
                        style = MaterialTheme.typography.labelSmall,
                        color = Piedra
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        estado.nombreRecinto,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Codigo: " + estado.contenido,
                        style = MaterialTheme.typography.labelSmall,
                        color = Piedra
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val id = estado.idRecinto
                            if (id != null) {
                                viewModel.limpiar()
                                onAbrirRecinto(id)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Terracota),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver ficha patrimonial")
                    }
                }
            }

            else -> Text(
                "Todavia no se ha escaneado ningun codigo",
                style = MaterialTheme.typography.bodyMedium,
                color = Piedra
            )
        }
    }
}
