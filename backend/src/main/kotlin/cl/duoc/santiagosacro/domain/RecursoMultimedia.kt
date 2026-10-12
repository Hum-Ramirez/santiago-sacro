package cl.duoc.santiagosacro.domain

import jakarta.persistence.*

@Entity
@Table(name = "recurso_multimedia")
class RecursoMultimedia(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recurso")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_recinto", nullable = false)
    var recinto: Recinto? = null,

    @Column(name = "tipo", nullable = false, length = 20)
    var tipo: String = "IMAGEN",

    @Column(name = "url", nullable = false, length = 300)
    var url: String = "",

    @Column(name = "descripcion", length = 200)
    var descripcion: String = "",

    @Column(name = "orden", nullable = false)
    var orden: Int = 1
)
