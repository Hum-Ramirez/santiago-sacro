package cl.duoc.santiagosacro.domain

import jakarta.persistence.*

@Entity
@Table(name = "tipo_recinto")
class TipoRecinto(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo")
    var id: Long? = null,

    @Column(name = "nombre", nullable = false, unique = true, length = 60)
    var nombre: String = ""
)
