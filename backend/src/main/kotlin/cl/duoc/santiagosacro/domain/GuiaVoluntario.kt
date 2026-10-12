package cl.duoc.santiagosacro.domain

import jakarta.persistence.*

@Entity
@Table(name = "guia_voluntario")
class GuiaVoluntario(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_guia")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_recinto", nullable = false)
    var recinto: Recinto? = null,

    @Column(name = "nombre", nullable = false, length = 120)
    var nombre: String = "",

    @Column(name = "idioma", nullable = false, length = 40)
    var idioma: String = "Espanol",

    @Column(name = "disponible", nullable = false)
    var disponible: Boolean = true
)
