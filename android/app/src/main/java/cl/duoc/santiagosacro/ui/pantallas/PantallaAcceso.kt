package cl.duoc.santiagosacro.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.santiagosacro.ui.theme.*
import cl.duoc.santiagosacro.ui.viewmodel.AccesoViewModel

/** Login de encargados y acceso del visitante registrado. */
@Composable
fun PantallaAcceso(
    viewModel: AccesoViewModel,
    onIrAlPanel: () -> Unit,
    onIrARegistro: () -> Unit,
    onExplorar: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Santiago Sacro", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Templos, arte e historia de Santiago",
            style = MaterialTheme.typography.bodyMedium,
            color = Piedra
        )

        Spacer(Modifier.height(32.dp))

        if (estado.sesionIniciada) {
            Card(
                colors = CardDefaults.cardColors(containerColor = PapelClaro),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Sesion iniciada como " + viewModel.nombreUsuario(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (estado.esEncargado) "Perfil: encargado de templo"
                        else "Perfil: visitante",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Piedra
                    )

                    Spacer(Modifier.height(16.dp))

                    if (estado.esEncargado) {
                        Button(
                            onClick = onIrAlPanel,
                            colors = ButtonDefaults.buttonColors(containerColor = Terracota),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ir al panel de mi recinto")
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    OutlinedButton(
                        onClick = { viewModel.cerrarSesion() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar sesion")
                    }
                }
            }
        } else {
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
                label = { Text("Contrasena") },
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

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { viewModel.ingresar() },
                enabled = !estado.cargando,
                colors = ButtonDefaults.buttonColors(containerColor = Terracota),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (estado.cargando) {
                    CircularProgressIndicator(
                        color = PapelClaro,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Entrar")
                }
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = onIrARegistro,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Crear una cuenta de visitante", color = TerracotaOscuro)
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Borde)
        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onExplorar,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Explorar el mapa")
        }
    }
}
