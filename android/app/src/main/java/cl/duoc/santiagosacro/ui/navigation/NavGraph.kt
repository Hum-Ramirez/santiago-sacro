package cl.duoc.santiagosacro.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.santiagosacro.ui.componentes.BarraInferior
import cl.duoc.santiagosacro.ui.pantallas.*
import cl.duoc.santiagosacro.ui.theme.Papel
import cl.duoc.santiagosacro.ui.viewmodel.*

/**
 * Grafo de navegacion de la aplicacion.
 * La barra inferior se muestra en los destinos principales y se oculta
 * en las pantallas de detalle.
 */
@Composable
fun AppSantiagoSacro() {

    val navegacion = rememberNavController()
    val entradaActual by navegacion.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    val mostrarBarra = rutaActual in listOf(
        Rutas.MAPA, Rutas.CARTELERA, Rutas.RUTA, Rutas.FAVORITOS, Rutas.ACCESO
    )

    Scaffold(
        containerColor = Papel,
        bottomBar = {
            if (mostrarBarra) {
                BarraInferior(rutaActual = rutaActual) { destino ->
                    navegacion.navigate(destino) {
                        popUpTo(Rutas.MAPA) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { relleno ->

        NavHost(
            navController = navegacion,
            startDestination = Rutas.MAPA,
            modifier = Modifier.padding(relleno)
        ) {

            composable(Rutas.MAPA) {
                val vm: MapaViewModel = viewModel(factory = MapaViewModel.Factory)
                PantallaMapa(
                    viewModel = vm,
                    onAbrirFicha = { id -> navegacion.navigate(Rutas.ficha(id)) }
                )
            }

            composable(
                route = Rutas.FICHA,
                arguments = listOf(navArgument("idRecinto") { type = NavType.LongType })
            ) { entrada ->
                val id = entrada.arguments?.getLong("idRecinto") ?: 0L
                val vm: FichaViewModel = viewModel(factory = FichaViewModel.Factory)
                PantallaFicha(
                    idRecinto = id,
                    viewModel = vm,
                    onVolver = { navegacion.popBackStack() },
                    onIrARuta = { navegacion.navigate(Rutas.RUTA) }
                )
            }

            composable(Rutas.CARTELERA) {
                val vm: CarteleraViewModel = viewModel(factory = CarteleraViewModel.Factory)
                PantallaCartelera(
                    viewModel = vm,
                    onAbrirRecinto = { id -> navegacion.navigate(Rutas.ficha(id)) }
                )
            }

            composable(Rutas.RUTA) {
                val vm: RutaViewModel = viewModel(factory = RutaViewModel.Factory)
                PantallaRuta(
                    viewModel = vm,
                    onAbrirRecinto = { id -> navegacion.navigate(Rutas.ficha(id)) }
                )
            }

            composable(Rutas.FAVORITOS) {
                val vm: FavoritosViewModel = viewModel(factory = FavoritosViewModel.Factory)
                PantallaFavoritos(
                    viewModel = vm,
                    onAbrirRecinto = { id -> navegacion.navigate(Rutas.ficha(id)) }
                )
            }

            composable(Rutas.ACCESO) {
                val vm: AccesoViewModel = viewModel(factory = AccesoViewModel.Factory)
                PantallaAcceso(
                    viewModel = vm,
                    onIrAlPanel = { navegacion.navigate(Rutas.PANEL) },
                    onIrARegistro = { navegacion.navigate(Rutas.REGISTRO) },
                    onExplorar = { navegacion.navigate(Rutas.MAPA) }
                )
            }

            composable(Rutas.REGISTRO) {
                val vm: AccesoViewModel = viewModel(factory = AccesoViewModel.Factory)
                PantallaRegistro(
                    viewModel = vm,
                    onVolver = { navegacion.popBackStack() },
                    onRegistrado = {
                        navegacion.navigate(Rutas.MAPA) {
                            popUpTo(Rutas.MAPA) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rutas.PANEL) {
                val vm: PanelViewModel = viewModel(factory = PanelViewModel.Factory)
                PantallaPanel(
                    viewModel = vm,
                    onVolver = { navegacion.popBackStack() }
                )
            }
        }
    }
}
