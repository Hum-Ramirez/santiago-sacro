package cl.duoc.santiagosacro.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "bloque_excepcional")
class BloqueExcepcional(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bloque")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_recinto", nullable = false)
    var recinto: Recinto? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    var usuario: Usuario? = null,

    @Column(name = "fecha", nullable = false)
    var fecha: LocalDate = LocalDate.now(),

    @Column(name = "hora_inicio", nullable = false)
    var horaInicio: LocalTime = LocalTime.of(20, 0),

    @Column(name = "hora_termino", nullable = false)
    var horaTermino: LocalTime = LocalTime.of(22, 0),

    @Column(name = "motivo", length = 200)
    var motivo: String = ""
)
