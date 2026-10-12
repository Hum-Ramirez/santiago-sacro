package cl.duoc.santiagosacro.data.remote

import cl.duoc.santiagosacro.data.remote.dto.*
import retrofit2.http.*

/**
 * Contrato de la API REST.
 * Retrofit genera la implementacion a partir de estas anotaciones.
 */
interface ApiSantiagoSacro {

    // ---------------------------------------------------------- autenticacion
    @POST("api/auth/registro")
    suspend fun registrar(@Body peticion: RegistroPeticion): AuthRespuesta

    @POST("api/auth/login")
    suspend fun login(@Body peticion: LoginPeticion): AuthRespuesta

    @GET("api/auth/perfil")
    suspend fun perfil(): PerfilRespuesta

    // ---------------------------------------------------------- recintos
    @GET("api/recintos")
    suspend fun listarRecintos(
        @Query("q") texto: String? = null,
        @Query("comuna") comuna: String? = null,
        @Query("estado") estado: String? = null
    ): List<RecintoResumen>

    @GET("api/recintos/cercanos")
    suspend fun recintosCercanos(
        @Query("latitud") latitud: Double,
        @Query("longitud") longitud: Double,
        @Query("radioKm") radioKm: Double = 5.0
    ): List<RecintoResumen>

    @GET("api/recintos/{id}")
    suspend fun detalleRecinto(@Path("id") id: Long): RecintoDetalle

    // ---------------------------------------------------------- eventos
    @GET("api/eventos")
    suspend fun cartelera(
        @Query("desde") desde: String? = null,
        @Query("hasta") hasta: String? = null,
        @Query("idCategoria") idCategoria: Long? = null
    ): List<EventoRespuesta>

    @GET("api/catalogos/categorias")
    suspend fun categorias(): List<CategoriaRespuesta>

    // ---------------------------------------------------------- avisos
    @GET("api/avisos/vigentes")
    suspend fun avisosVigentes(): List<AvisoRespuesta>

    // ---------------------------------------------------------- encargado
    @GET("api/encargado/mis-recintos")
    suspend fun misRecintos(): List<RecintoResumen>

    @PATCH("api/recintos/{id}/estado")
    suspend fun cambiarEstado(
        @Path("id") id: Long,
        @Body peticion: CambioEstadoPeticion
    ): RecintoResumen

    @POST("api/recintos/{id}/eventos")
    suspend fun publicarEvento(
        @Path("id") id: Long,
        @Body peticion: EventoPeticion
    ): EventoRespuesta

    @POST("api/recintos/{id}/bloques")
    suspend fun registrarBloque(
        @Path("id") id: Long,
        @Body peticion: BloquePeticion
    ): BloqueRespuesta

    @GET("api/encargado/solicitudes")
    suspend fun solicitudesDelEncargado(): List<SolicitudRespuesta>

    @GET("api/encargado/recintos/{id}/guias")
    suspend fun guiasDelRecinto(@Path("id") id: Long): List<GuiaRespuesta>

    @PATCH("api/solicitudes/{id}")
    suspend fun gestionarSolicitud(
        @Path("id") id: Long,
        @Body peticion: GestionSolicitudPeticion
    ): SolicitudRespuesta

    // ---------------------------------------------------------- visitante
    @POST("api/recintos/{id}/solicitudes-guia")
    suspend fun solicitarGuia(
        @Path("id") id: Long,
        @Body peticion: SolicitudPeticion
    ): SolicitudRespuesta

    @GET("api/solicitudes/mias")
    suspend fun misSolicitudes(): List<SolicitudRespuesta>
}
