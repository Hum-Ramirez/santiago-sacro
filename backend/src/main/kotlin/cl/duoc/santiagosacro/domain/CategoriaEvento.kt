package cl.duoc.santiagosacro.domain

import jakarta.persistence.*

@Entity
@Table(name = "categoria_evento")
class CategoriaEvento(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    var id: Long? = null,

    @Column(name = "nombre", nullable = false, unique = true, length = 60)
    var nombre: String = ""
)
