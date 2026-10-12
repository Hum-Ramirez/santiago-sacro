package cl.duoc.santiagosacro.data.remote.dto

// ---------------------------------------------------------------- autenticacion

data class RegistroPeticion(
    val nombre: String,
    val correo: String,
    val clave: String
)

data class LoginPeticion(
    val correo: String,
    val clave: String
)

data class AuthRespuesta(
    val token: String,
    val tipo: String,
    val correo: String,
    val nombre: String,
    val rol: String,
    val expiraEnMs: Long
)

data class PerfilRespuesta(
    val id: Long,
    val correo: String,
    val nombre: String,
    val rol: String,
    val recintosAdministrados: List<RecintoResumen>
)

// ---------------------------------------------------------------- recintos

data class RecintoResumen(
    val id: Long,
    val nombreOficial: String,
    val tipo: String,
    val comuna: String,
    val latitud: Double,
    val longitud: Double,
    val estadoApertura: String,
    val imagenPrincipal: String?,
    val distanciaKm: Double?
)

data class RecintoDetalle(
    val id: Long,
    val nombreOficial: String,
    val tipo: String,
    val comuna: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val descripcionHistorica: String,
    val estadoApertura: String,
    val horarios: List<HorarioRespuesta>,
    val bloquesExcepcionales: List<BloqueRespuesta>,
    val galeria: List<RecursoRespuesta>,
    val eventosProximos: List<EventoRespuesta>
)

data class HorarioRespuesta(
    val diaSemana: Int,
    val nombreDia: String,
    val horaApertura: String,
    val horaCierre: String
)

data class BloqueRespuesta(
    val id: Long,
    val fecha: String,
    val horaInicio: String,
    val horaTermino: String,
    val motivo: String
)

data class RecursoRespuesta(
    val id: Long,
    val tipo: String,
    val url: String,
    val descripcion: String
)

data class CambioEstadoPeticion(
    val estado: String
)

data class BloquePeticion(
    val fecha: String,
    val horaInicio: String,
    val horaTermino: String,
    val motivo: String
)

// ---------------------------------------------------------------- eventos y avisos

data class EventoRespuesta(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val fechaInicio: String,
    val horaInicio: String,
    val estado: String,
    val idRecinto: Long,
    val nombreRecinto: String,
    val latitud: Double,
    val longitud: Double
)

data class EventoPeticion(
    val titulo: String,
    val descripcion: String,
    val idCategoria: Long,
    val fechaInicio: String,
    val horaInicio: String
)

data class CategoriaRespuesta(
    val id: Long,
    val nombre: String
)

data class AvisoRespuesta(
    val id: Long,
    val texto: String,
    val vigenciaDesde: String,
    val vigenciaHasta: String,
    val idRecinto: Long,
    val nombreRecinto: String,
    val latitud: Double,
    val longitud: Double
)

// ---------------------------------------------------------------- guias

data class SolicitudPeticion(
    val fechaVisita: String,
    val horaVisita: String,
    val tamanoGrupo: Int,
    val contacto: String
)

data class GestionSolicitudPeticion(
    val aceptar: Boolean,
    val idGuia: Long? = null,
    val motivoRechazo: String? = null
)

data class SolicitudRespuesta(
    val id: Long,
    val idRecinto: Long,
    val nombreRecinto: String,
    val fechaVisita: String,
    val horaVisita: String,
    val tamanoGrupo: Int,
    val contacto: String,
    val estado: String,
    val guiaAsignado: String?,
    val motivoRechazo: String?
)

data class GuiaRespuesta(
    val id: Long,
    val nombre: String,
    val idioma: String,
    val disponible: Boolean
)
