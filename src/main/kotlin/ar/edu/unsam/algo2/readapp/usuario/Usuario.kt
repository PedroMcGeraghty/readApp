package ar.edu.unsam.algo2.readapp.usuario


import ar.edu.unsam.algo2.readapp.centrosDeLectura.CentroDeLectura
import ar.edu.unsam.algo2.readapp.features.Recomendacion
import ar.edu.unsam.algo2.readapp.features.Valoracion
import ar.edu.unsam.algo2.readapp.libro.Autor
import ar.edu.unsam.algo2.readapp.libro.Lenguaje
import ar.edu.unsam.algo2.readapp.libro.Libro
import ar.edu.unsam.algo2.readapp.observers.AgregarLibroObserver
import ar.edu.unsam.algo2.readapp.repositorios.AvaliableInstance
import excepciones.BusinessException
import excepciones.RecomendacionException
import java.time.LocalDate
import java.time.Period

class Usuario(
    var nombre: String = "",
    var apellido: String = "",
    var alias: String = "",
    var fechaNacimiento: LocalDate = LocalDate.now(),
    var palabrasPorMinutos: Int = (5..250).random(),
    var direccionMail: String = "",
    var lenguaje: Lenguaje = Lenguaje.values().random()
) : AvaliableInstance {

    //Interfaces
    var tipoDeLector: TipoDeLector = Promedio
    var perfil: PerfilDeUsuario = Leedor

    val listaObservers: MutableList<AgregarLibroObserver> = mutableListOf()
    override var id: Int = -1//POR DEFAULT AL FINAL

    //Collections
    val autoresPreferidos: MutableSet<Autor> = mutableSetOf()
    val librosLeidos: MutableList<Libro> = mutableListOf() //List para cantidad con repetidos
    val cantidadVecesLeido: MutableList<Int> = mutableListOf()
    val librosALeer: MutableSet<Libro> = mutableSetOf()
    val amigos: MutableList<Usuario> = mutableListOf()
    val recomendaciones: MutableList<Recomendacion> = mutableListOf()
    val recomendacionesAValorar: MutableList<Recomendacion> = mutableListOf()
    val recomendacionesValoradas: MutableMap<Recomendacion, Valoracion> = mutableMapOf()


    fun edad(): Int {
        return Period.between(this.fechaNacimiento, LocalDate.now()).years
    }

    fun agregarAutorFavorito(autor: Autor) {
        autoresPreferidos.add(autor)
    }

    fun eliminarAutorFavorito(autor: Autor) {
        autoresPreferidos.remove(autor)
    }

    fun esAmigoDe(usuario: Usuario): Boolean = this.amigos.contains(usuario)
    fun agregarAmigo(amigo: Usuario) {
        amigos.add(amigo)
    }

    ///////////////////////////////////////////////////////
    ///     ACCIONES CON LECTURA
    ///////////////////////////////////////////////////////
    private fun tiempoLecturaInicial(libro: Libro): Double =
        ((libro.cantidadPalabras / this.palabrasPorMinutos).toDouble())

    fun tiempoLecturaBase(libro: Libro): Double = this.tiempoLecturaInicial(libro) * this.factorDesafianteDe(libro)
    fun factorDesafianteDe(libro: Libro) = if (libro.esDesafiante()) 2 else 1
    fun tiempoLecturaFinal(libro: Libro): Double =
        this.tiempoLecturaBase(libro) + tipoDeLector.tiempoDeLectura(this, libro)

    fun leer(libro: Libro) {
        if (this.leido(libro)) cuantificarLibroLeido(libro)
        else {
            librosLeidos.add(libro)
            cantidadVecesLeido.add(1)
        }
    }

    fun cuantificarLibroLeido(libro: Libro) {
        cantidadVecesLeido[librosLeidos.indexOf(libro)] += 1
    }

    fun leido(libro: Libro): Boolean = this.librosLeidos.contains(libro)
    fun cantidadLecturasDe(libro: Libro): Int {
        //val predicate: (Libro) -> Boolean = {it == libro}
        // return librosLeidos.count(predicate)
        return if (!this.leido(libro)) 0
        else cantidadVecesLeido[librosLeidos.indexOf(libro)]
    }

    //Metodo para agregar libros a "lecturas pendientes" hay que verificar que no este en los leidos y tampoco esta en los pendientes
    fun agregarLibroALeer(libro: Libro) {
        if (libro in librosLeidos || libro in librosALeer) {
            throw BusinessException("Este libro ya se encuetra leido o ya se agregó a 'libros a leer'")
        }
        librosALeer.add(libro)
    }

    ///////////////////////////////////////////////////////
    ///     RECOMENDACION
    ///////////////////////////////////////////////////////
    fun crearRecomendacion(
        titulo: String = "",
        librosParaRecomendar: MutableSet<Libro>,
        contenido: String = "",
        publico: Boolean = true
    ) {
        if (!this.leyoLibrosParaAgregar(librosParaRecomendar)) {
            throw RecomendacionException("Debe agregar un libro leido")
        }
        this.almacenarRecomendacion(
            Recomendacion(
                creador = this, librosParaRecomendar, titulo, contenido, publico
            )
        )
    }

    private fun almacenarRecomendacion(recomendacion: Recomendacion) {
        this.recomendaciones.addLast(recomendacion)
    }

    private fun eliminarRecomendacion(recomendacion: Recomendacion) {
        this.recomendaciones.remove(recomendacion)
    }

    fun buscarRecomendacionesPor(usuario: Usuario): List<Recomendacion> =
        usuario.recomendaciones.filter { it.esAccesiblePara(this) }

    fun buscarRecomendacionesInteresantes(usuario: Usuario): MutableSet<Recomendacion> {
        return this.buscarRecomendacionesPor(usuario).filter { recomendacion ->
            perfil.recomendacionEsInteresante(recomendacion, this)
        }.toMutableSet()
    }

    fun leyoLibrosParaAgregar(librosAgregados: MutableSet<Libro>): Boolean =
        this.librosLeidos.containsAll(librosAgregados)

    fun editarRecomendacionDe(
        usuario: Usuario = this,
        recomendacion: Recomendacion,
        librosParaAgregar: MutableSet<Libro>
    ) {
        if (!recomendacion.esEditablePor(usuario, librosParaAgregar)) {
            // El metodo ".esEditablePor()" desencadenara una serie de condiciones para validar si puede o no editarlas
            throw RecomendacionException("No puede Editar")
        }
        librosParaAgregar.forEach({ agregarLibroARecomendacion(it, usuario, recomendacion) })

    }

    private fun agregarLibroARecomendacion(libro: Libro, usuario: Usuario, recomendacion: Recomendacion) {
        recomendacion.editar(libro)
        listaObservers.forEach({ it.ejecutar(libro, usuario, recomendacion) })
    }


    fun librosLeidosAmigos(): List<Libro> {
        return amigos.flatMap { amigo -> amigo.librosLeidos }
    }

    fun tieneLibrosPendientes(listaLibros: MutableSet<Libro>): Set<Libro> {
        return librosALeer.intersect(listaLibros)
    }
    fun vetarAmigo(usuario: Usuario){
        this.amigos.remove(usuario)
    }

    ///////////////////////////////////////////////////////
    ///     VALORACION
    ///////////////////////////////////////////////////////
    fun valorarRecomendacion(recomendacion: Recomendacion, valor: Int, comentario: String) {
        if (puedeCrearValoracion(recomendacion)) {
            throw BusinessException("No puede crear una valoracion")
        }
        this.crearValoracion(valor, comentario, recomendacion)
    }

    private fun puedeCrearValoracion(recomendacion: Recomendacion) =
        this.recomendoValoracion(recomendacion) || !recomendacion.puedeDejarValoracion(this)

    private fun crearValoracion(valor: Int, comentario: String, recomendacion: Recomendacion) {
        val valoracion = Valoracion(valor, comentario, this)
        recomendacion.valoraciones.add(valoracion)
        recomendacionesValoradas.put(recomendacion, valoracion)
    }

    fun recomendoValoracion(recomendacion: Recomendacion) =
        recomendacion.valoraciones.any { valoracion: Valoracion -> valoracion.autor == this }

    fun editarValoracion(recomendacion: Recomendacion, valor: Int, comentario: String) {
        this.buscarValoracionAEditar(recomendacion, valor, comentario)
            .editarValoracion(recomendacion, valor, comentario, this)
    }//Fijarse que pasa si devuelve una valoracion vacia, quizas haria falta una excepcion

    //Busca la recomendacion a la cual valoro para poder editarla
    fun buscarValoracionAEditar(recomendacion: Recomendacion, valor: Int, comentario: String): Valoracion =
        recomendacion.valoraciones.filter { valoracion: Valoracion -> valoracion.autor == this }
            .first()//editarValoracion(recomendacion ,valor, comentario, this)

    //Metodo para agregar una recomendacion de otro usuario a mi lista de recomendaciones pendientes de valoracion
    fun agregarRecomendacionAValorar(recomendacion: Recomendacion) {
        if (!recomendacion.esAccesiblePara(this)) {
            throw BusinessException("No se puede acceder a esta recomendacion")
        }
        recomendacionesAValorar.add(recomendacion)
    }

    ///////////////////////////////////////////////////////
    ///     Centros de Lectura                          ///
    ///////////////////////////////////////////////////////

    fun reservarCentro(centro: CentroDeLectura) {
        centro.reservar(this)
    }

    ///////////////////////////////////////////////////////
    ///     CAMBIO DE ESTADOS
    ///////////////////////////////////////////////////////
    fun <T : TipoDeLector> cambioTipoLector(tipoLector: T) {
        tipoDeLector = tipoLector
    }

    fun <T : PerfilDeUsuario> cambioPerfil(perfilNuevo: T) {
        perfil = perfilNuevo
    }

    override fun cumpleCriterioBusqueda(texto: String) =
        nombre.contains(texto) || apellido.contains(texto) || alias.contains(texto)

    ///////////////////////////////////////////////////////
    ///         observers                               ///
    ///////////////////////////////////////////////////////
    fun agregarObserver(observer: AgregarLibroObserver) {
        this.listaObservers.add(observer)
    }
}
