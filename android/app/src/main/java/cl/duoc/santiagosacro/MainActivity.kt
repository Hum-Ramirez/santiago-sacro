package cl.duoc.santiagosacro

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import cl.duoc.santiagosacro.ui.navigation.AppSantiagoSacro
import cl.duoc.santiagosacro.ui.theme.SantiagoSacroTheme

class MainActivity : ComponentActivity() {

    /**
     * Solicitud del permiso de ubicacion.
     * El mapa funciona igual sin el: simplemente no ordena por cercania.
     */
    private val solicitarUbicacion = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* El resultado lo consulta ServicioUbicacion antes de cada uso. */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Contenedor.servicioUbicacion.hayPermiso()) {
            solicitarUbicacion.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        setContent {
            SantiagoSacroTheme {
                AppSantiagoSacro()
            }
        }
    }
}
