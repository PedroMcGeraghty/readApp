package ar.edu.unsam.algo2.readapp.builders

import ar.edu.unsam.algo2.readapp.libro.Libro
import ar.edu.unsam.algo2.readapp.centrosDeLectura.Biblioteca
import ar.edu.unsam.algo2.readapp.centrosDeLectura.CentroDeLectura
import ar.edu.unsam.algo2.readapp.centrosDeLectura.Editorial
import ar.edu.unsam.algo2.readapp.centrosDeLectura.Particular
import java.time.LocalDate

abstract class CentroLecturaBuilder<T : CentroDeLectura>(val nuevoCentro: T) {

    fun direccion(direccion: String): CentroLecturaBuilder<T> = apply {
        nuevoCentro.direccion = direccion
    }

    fun libro(libro: Libro): CentroLecturaBuilder<T> = apply {
        nuevoCentro.libroDesignado = libro
    }

    fun fecha(fecha: MutableSet<LocalDate>): CentroLecturaBuilder<T> = apply {
        nuevoCentro.fechasDeLectura = fecha
    }

    fun duracion(tiempo: Int): CentroLecturaBuilder<T> = apply {
        nuevoCentro.duracionEncuentro = tiempo
    }

    abstract fun build(): CentroDeLectura
}

class ParticularBuilder(val nuevoParticular: Particular = Particular()):CentroLecturaBuilder<Particular>(nuevoCentro = nuevoParticular){

    fun participantes(cantidad: Int):ParticularBuilder = apply {
        nuevoParticular.participantesMax = cantidad
    }

    override fun build(): Particular = nuevoParticular
}

class EditorialBuilder(val nuevoEditorial: Editorial = Editorial()):CentroLecturaBuilder<Editorial>(nuevoCentro = nuevoEditorial){

    fun montoAlcanzable(cantidad: Double): EditorialBuilder = apply {
        nuevoEditorial.montoAlcanzable = cantidad
    }

    override fun build() = nuevoEditorial
}

class BibliotecaBuilder(val nuevaBiblioteca: Biblioteca = Biblioteca()):CentroLecturaBuilder<Biblioteca>(nuevoCentro = nuevaBiblioteca){

    fun metros(metros: Double) = apply {
        nuevaBiblioteca.metrosCuadrados = metros
    }

    fun gastos(listaGastos: DoubleArray) = apply {
        nuevaBiblioteca.gastosFijos = listaGastos
    }

    override fun build() = nuevaBiblioteca
}