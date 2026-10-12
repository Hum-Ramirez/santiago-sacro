package cl.duoc.santiagosacro.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "aviso")
class Aviso(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aviso")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_recinto", nullable = false)
    var recinto: Recinto? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    var usuario: Usuario? = null,

    @Column(name = "texto", nullable = false, length = 400)
    var texto: String = "",

    @Column(name = "vigencia_desde", nullable = false)
    var vigenciaDesde: LocalDateTime = LocalDateTime.now(),

    @Column(name = "vigencia_hasta", nullable = false)
    var vigenciaHasta: LocalDateTime = LocalDateTime.now().plusDays(1)
)
