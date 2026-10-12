package cl.duoc.santiagosacro.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(name = "solicitud_guia")
class SolicitudGuia(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_recinto", nullable = false)
    var recinto: Recinto? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_estado", nullable = false)
    var estado: EstadoSolicitud? = null,

    /** Se asigna recien cuando el encargado acepta la solicitud, por eso admite nulo. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_guia")
    var guia: GuiaVoluntario? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_gestiona")
    var usuarioGestiona: Usuario? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_solicita")
    var usuarioSolicita: Usuario? = null,

    @Column(name = "fecha_visita", nullable = false)
    var fechaVisita: LocalDate = LocalDate.now(),

    @Column(name = "hora_visita", nullable = false)
    var horaVisita: LocalTime = LocalTime.of(10, 0),

    @Column(name = "tamano_grupo", nullable = false)
    var tamanoGrupo: Int = 1,

    @Column(name = "contacto", nullable = false, length = 160)
    var contacto: String = "",

    @Column(name = "motivo_rechazo", length = 300)
    var motivoRechazo: String? = null,

    @Column(name = "fecha_solicitud", nullable = false)
    var fechaSolicitud: LocalDateTime = LocalDateTime.now()
)
