package cl.duoc.santiagosacro.ui.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cl.duoc.santiagosacro.ui.navigation.Rutas
import cl.duoc.santiagosacro.ui.theme.*

private data class Destino(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
)

private val destinos = listOf(
    Destino(Rutas.MAPA, "Mapa", Icons.Default.Map),
    Destino(Rutas.CARTELERA, "Cartelera", Icons.Default.CalendarMonth),
    Destino(Rutas.RUTA, "Mi ruta", Icons.Default.Route),
    Destino(Rutas.FAVORITOS, "Favoritos", Icons.Default.FavoriteBorder),
    Destino(Rutas.ACCESO, "Acceso", Icons.Default.Person)
)

@Composable
fun BarraInferior(rutaActual: String?, onNavegar: (String) -> Unit) {
    NavigationBar(containerColor = PapelClaro, tonalElevation = 0.dp) {
        destinos.forEach { destino ->
            NavigationBarItem(
                selected = rutaActual == destino.ruta,
                onClick = { onNavegar(destino.ruta) },
                icon = { Icon(destino.icono, contentDescription = destino.titulo) },
                label = { Text(destino.titulo, style = androidx.compose.material3.MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Terracota,
                    selectedTextColor = Terracota,
                    unselectedIconColor = Piedra,
                    unselectedTextColor = Piedra,
                    indicatorColor = VerdeSuave
                )
            )
        }
    }
}
