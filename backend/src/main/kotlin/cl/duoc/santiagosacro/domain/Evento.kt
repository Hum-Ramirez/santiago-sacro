package cl.duoc.santiagosacro.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(name = "evento")
class Evento(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_recinto", nullable = false)
    var recinto: Recinto? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    var categoria: CategoriaEvento? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    var usuario: Usuario? = null,

    @Column(name = "titulo", nullable = false, length = 160)
    var titulo: String = "",

    @Column(name = "descripcion", length = 1000)
    var descripcion: String = "",

    @Column(name = "fecha_inicio", nullable = false)
    var fechaInicio: LocalDate = LocalDate.now(),

    @Column(name = "hora_inicio", nullable = false)
    var horaInicio: LocalTime = LocalTime.of(19, 0),

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    var estado: EstadoEvento = EstadoEvento.PUBLICADO,

    @Column(name = "fecha_publicacion", nullable = false)
    var fechaPublicacion: LocalDateTime = LocalDateTime.now()
)
