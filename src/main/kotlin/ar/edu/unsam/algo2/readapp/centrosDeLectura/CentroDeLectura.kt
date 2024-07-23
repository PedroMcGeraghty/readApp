package ar.edu.unsam.algo2.readapp.centrosDeLectura

import LibroBuilder
import ar.edu.unsam.algo2.readapp.libro.Libro
import ar.edu.unsam.algo2.readapp.repositorios.AvaliableInstance
import ar.edu.unsam.algo2.readapp.usuario.Usuario
import excepciones.BusinessException
import java.time.LocalDate


abstract class CentroDeLectura(
    var direccion: String = "",
    var libroDesignado: Libro = LibroBuilder().build(),
    var fechasDeLectura: MutableSet<LocalDate> = mutableSetOf(),
    var duracionEncuentro: Int = (1..5).random()
) : AvaliableInstance {

    val listaParticipantes: MutableSet<Usuario> = mutableSetOf()
    val costoDeApp: Double = 1000.00

    open fun costoDeReserva(): Double = costoDeApp + condicion()

    fun hayFechasVigentes(): Boolean = fechasDeLectura.any { fecha -> fecha > LocalDate.now() }
    fun reservar(usuario: Usuario) {
        if (!puedeResevar()) {
            throw BusinessException("se alcanzo la cantidad maxima de participantes")
        }
        listaParticipantes.add(usuario)
    }

    //Interface avaliable instance
    override var id: Int = -1
    override fun cumpleCriterioBusqueda(texto: String) = libroDesignado.titulo === texto

    fun estaExpirado(): Boolean = !hayFechasVigentes() || condicionExpirar()

    abstract fun condicionExpirar(): Boolean
    abstract fun puedeResevar(): Boolean
    abstract fun condicion(): Double
    open fun autorAsiste() : Unit {}
}