package cl.duoc.santiagosacro.ui.navigation

/** Destinos de navegacion de la aplicacion. */
object Rutas {
    const val MAPA = "mapa"
    const val FICHA = "ficha/{idRecinto}"
    const val CARTELERA = "cartelera"
    const val RUTA = "ruta"
    const val FAVORITOS = "favoritos"
    const val QR = "qr"
    const val ACCESO = "acceso"
    const val REGISTRO = "registro"
    const val PANEL = "panel"
    const val SOLICITUD = "solicitud/{idRecinto}"

    fun ficha(idRecinto: Long) = "ficha/" + idRecinto

    fun solicitud(idRecinto: Long) = "solicitud/" + idRecinto
}
