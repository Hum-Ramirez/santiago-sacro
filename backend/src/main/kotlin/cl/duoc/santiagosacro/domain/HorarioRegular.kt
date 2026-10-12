package cl.duoc.santiagosacro.domain

import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(name = "horario_regular")
class HorarioRegular(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_recinto", nullable = false)
    var recinto: Recinto? = null,

    /** 1 = lunes ... 7 = domingo (java.time.DayOfWeek). */
    @Column(name = "dia_semana", nullable = false)
    var diaSemana: Int = 1,

    @Column(name = "hora_apertura", nullable = false)
    var horaApertura: LocalTime = LocalTime.of(9, 0),

    @Column(name = "hora_cierre", nullable = false)
    var horaCierre: LocalTime = LocalTime.of(19, 0)
)
