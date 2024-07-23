
import ar.edu.unsam.algo2.readapp.libro.Autor
import ar.edu.unsam.algo2.readapp.libro.Lenguaje
import ar.edu.unsam.algo2.readapp.libro.Libro
import ar.edu.unsam.algo2.readapp.builders.AutorBuilder
import excepciones.BookException


class LibroBuilder (val newLibro: Libro = Libro()) {

    fun autor(autor: Autor) = apply {
        newLibro.autor = autor
    }
    fun largo() = apply{
        newLibro.cantidadPaginas = Libro.PAGINAS_LIBRO_LARGO_MIN + 1
    }
    fun cantidadPalabras(palabras: Int) = apply {
        newLibro.cantidadPalabras = palabras
        
    }

    fun cantidadPaginas(paginas: Int)= apply {
        newLibro.cantidadPaginas = paginas
    }

    fun ediciones(ediciones: Int)= apply {
        newLibro.ediciones = ediciones
    }

    fun ventasSemanales(ventas: Int)= apply {
        newLibro.ventasSemanales = ventas
    }

    fun traducciones(traducciones: MutableSet<Lenguaje>)= apply {
        newLibro.traducciones = traducciones
    }

    fun esComplejo(complejo: Boolean)= apply {
        newLibro.esComplejo = complejo
    }

    fun titulo(titulo: String)= apply {
        newLibro.titulo = titulo
    }

    fun autorRandom()= apply {
        newLibro.autor = AutorBuilder().lenguaNativa(Lenguaje.values().random()).build()
        return this
    }

    fun traduccionesRandom(cantidad: Int = 3)= apply {
        val lenguajesRandom = mutableSetOf<Lenguaje>()
        repeat(cantidad) {
            lenguajesRandom.add(Lenguaje.values().random())
        }
        newLibro.traducciones = lenguajesRandom
        return this
    }

    fun popular() = apply {
        newLibro.ediciones = Libro.EDICIONES_MIN + 1
        this.traduccionesRandom()
        return this
    }

    fun bestSeller() = apply {
        this.popular()
        newLibro.ventasSemanales = Libro.VENTA_SEMANAL_MIN
        return this
    }

    fun complejo() = apply {
        newLibro.esComplejo = true
    }

    fun build(): Libro {
        if (newLibro.cantidadPalabras <= 0) {
            throw BookException("El valor de cantidadPalabras no puede ser menor o igual 0")
        }
        if (newLibro.cantidadPaginas <= 0) {
            throw BookException("El valor de cantidadPaginas no puede ser menor o igual 0")
        }
        return newLibro
    }
}
