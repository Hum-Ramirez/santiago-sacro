package cl.duoc.santiagosacro.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.santiagosacro.ui.theme.*
import cl.duoc.santiagosacro.ui.viewmodel.AccesoViewModel

/** Registro de un visitante nuevo. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(
    viewModel: AccesoViewModel,
    onVolver: () -> Unit,
    onRegistrado: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(estado.sesionIniciada) {
        if (estado.sesionIniciada) onRegistrado()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta", style = MaterialTheme.typography.titleLarge) },
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
                .padding(24.dp)
        ) {

            Text(
                "Con tu cuenta puedes guardar rutas y solicitar guias.",
                style = MaterialTheme.typography.bodyMedium,
                color = Piedra
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = estado.nombre,
                onValueChange = viewModel::nombreCambio,
                label = { Text("Nombre") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = estado.correo,
                onValueChange = viewModel::correoCambio,
                label = { Text("Correo") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = estado.clave,
                onValueChange = viewModel::claveCambio,
                label = { Text("Contrasena (minimo 6 caracteres)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            estado.error?.let { mensaje ->
                Spacer(Modifier.height(10.dp))
                Text(mensaje, color = TerracotaOscuro, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.registrarse() },
                enabled = !estado.cargando,
                colors = ButtonDefaults.buttonColors(containerColor = Terracota),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (estado.cargando) "Creando cuenta..." else "Crear cuenta")
            }
        }
    }
}
