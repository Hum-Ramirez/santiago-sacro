package cl.duoc.santiagosacro.domain

import jakarta.persistence.*

@Entity
@Table(name = "rol")
class Rol(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    var id: Long? = null,

    @Column(name = "nombre", nullable = false, unique = true, length = 30)
    var nombre: String = ""
) {
    companion object {
        const val VISITANTE = "VISITANTE"
        const val ENCARGADO = "ENCARGADO"
        const val ADMIN = "ADMIN"
    }
}
