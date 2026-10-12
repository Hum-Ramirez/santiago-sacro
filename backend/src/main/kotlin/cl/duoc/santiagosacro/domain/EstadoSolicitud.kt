package cl.duoc.santiagosacro.domain

import jakarta.persistence.*

@Entity
@Table(name = "estado_solicitud")
class EstadoSolicitud(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    var id: Long? = null,

    @Column(name = "nombre", nullable = false, unique = true, length = 30)
    var nombre: String = ""
) {
    companion object {
        const val PENDIENTE = "PENDIENTE"
        const val ACEPTADA = "ACEPTADA"
        const val RECHAZADA = "RECHAZADA"
        const val VENCIDA = "VENCIDA"
    }
}
