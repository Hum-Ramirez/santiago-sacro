package cl.duoc.santiagosacro.domain

import jakarta.persistence.*

@Entity
@Table(name = "recinto")
class Recinto(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recinto")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_tipo", nullable = false)
    var tipo: TipoRecinto? = null,

    @Column(name = "nombre_oficial", nullable = false, length = 160)
    var nombreOficial: String = "",

    @Column(name = "descripcion_historica", length = 2000)
    var descripcionHistorica: String = "",

    @Column(name = "direccion", nullable = false, length = 200)
    var direccion: String = "",

    @Column(name = "latitud", nullable = false)
    var latitud: Double = 0.0,

    @Column(name = "longitud", nullable = false)
    var longitud: Double = 0.0,

    @Column(name = "comuna", nullable = false, length = 80)
    var comuna: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_apertura", nullable = false, length = 20)
    var estadoApertura: EstadoApertura = EstadoApertura.CERRADO,

    @OneToMany(mappedBy = "recinto", cascade = [CascadeType.ALL], orphanRemoval = true)
    var recursos: MutableList<RecursoMultimedia> = mutableListOf(),

    @OneToMany(mappedBy = "recinto", cascade = [CascadeType.ALL], orphanRemoval = true)
    var horarios: MutableList<HorarioRegular> = mutableListOf(),

    @OneToMany(mappedBy = "recinto", cascade = [CascadeType.ALL], orphanRemoval = true)
    var bloques: MutableList<BloqueExcepcional> = mutableListOf()
)
