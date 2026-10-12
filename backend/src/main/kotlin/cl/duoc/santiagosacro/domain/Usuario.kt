package cl.duoc.santiagosacro.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "usuario")
class Usuario(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    var id: Long? = null,

    @Column(name = "correo", nullable = false, unique = true, length = 120)
    var correo: String = "",

    @Column(name = "clave_hash", nullable = false, length = 100)
    var claveHash: String = "",

    @Column(name = "nombre", nullable = false, length = 120)
    var nombre: String = "",

    @Column(name = "activo", nullable = false)
    var activo: Boolean = true,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    var rol: Rol? = null,

    /**
     * Recintos que administra el encargado.
     * Relacion N:M del modelo relacional, resuelta con la tabla usuario_recinto.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuario_recinto",
        joinColumns = [JoinColumn(name = "id_usuario")],
        inverseJoinColumns = [JoinColumn(name = "id_recinto")]
    )
    var recintos: MutableSet<Recinto> = mutableSetOf(),

    @Column(name = "fecha_registro", nullable = false)
    var fechaRegistro: LocalDateTime = LocalDateTime.now()
)
