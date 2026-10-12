package cl.duoc.santiagosacro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = Terracota,
    onPrimary = PapelClaro,
    primaryContainer = VerdeSuave,
    onPrimaryContainer = VerdeOscuro,
    secondary = VerdeOscuro,
    onSecondary = PapelClaro,
    background = Papel,
    onBackground = Tinta,
    surface = PapelClaro,
    onSurface = Tinta,
    surfaceVariant = MapaFondo,
    onSurfaceVariant = Piedra,
    outline = Borde,
    error = TerracotaOscuro
)

@Composable
fun SantiagoSacroTheme(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = Tipografia,
        content = contenido
    )
}
