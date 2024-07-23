package ar.edu.unsam.algo2.readapp.builders

import ar.edu.unsam.algo2.readapp.libro.Autor
import ar.edu.unsam.algo2.readapp.libro.Lenguaje

class AutorBuilder(val nuevoAutor: Autor = Autor()) {
    
    fun lenguaNativa(lenguaNativa: Lenguaje): AutorBuilder = apply {
        nuevoAutor.lenguaNativa = lenguaNativa
    }

    fun edad(edad: Int): AutorBuilder = apply {
        nuevoAutor.edad = edad
    }

    fun nombre(nombre: String): AutorBuilder = apply {
        nuevoAutor.nombre = nombre
    }

    fun apellido(apellido: String): AutorBuilder = apply {
        nuevoAutor.apellido = apellido
    }

    fun seudonimo(seudonimo: String): AutorBuilder = apply {
        nuevoAutor.seudonimo = seudonimo
    }

    fun build(): Autor = nuevoAutor

}