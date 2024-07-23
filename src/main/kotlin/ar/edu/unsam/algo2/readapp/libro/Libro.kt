package ar.edu.unsam.algo2.readapp.libro


import ar.edu.unsam.algo2.readapp.builders.AutorBuilder
import ar.edu.unsam.algo2.readapp.repositorios.AvaliableInstance


class Libro (
    //Estas variables =>> cantidadPalabras y cantidadPaginas
    // Deben insultar en caso que se inicializen con valor 0 o menor
    var autor: Autor = AutorBuilder().build(),
    var cantidadPalabras: Int = 1,
    var cantidadPaginas: Int = 1,
    var ediciones: Int = 1,
    var ventasSemanales: Int = 0,
    var traducciones: MutableSet<Lenguaje> = mutableSetOf(),
    var esComplejo: Boolean = false,
    var titulo: String = ""
    ): AvaliableInstance {
  
    override var id:Int = -1//POR DEFAULT AL FINAL
    override fun cumpleCriterioBusqueda(texto : String) = (titulo.contains(texto) || autor.apellido.contains(texto))

    companion object {
        val PAGINAS_LIBRO_LARGO_MIN: Int = 600
        val TRADUCCIONES_MIN: Int = 5
        val EDICIONES_MIN: Int = 2
        val VENTA_SEMANAL_MIN: Int = 10000
    }

    fun esLargo(): Boolean = this.cantidadPaginas > PAGINAS_LIBRO_LARGO_MIN
    fun esDesafiante(): Boolean = this.esComplejo || this.esLargo()
    fun sinTraducciones(): Boolean = this.traducciones.size == 0
    fun variasTraducciones(): Boolean = this.traducciones.size >= TRADUCCIONES_MIN
    fun variasEdiciones(): Boolean = this.ediciones > EDICIONES_MIN
    fun esPopular(): Boolean = this.variasEdiciones() || this.variasTraducciones()
    fun superaVentaSemanal() = this.ventasSemanales >= VENTA_SEMANAL_MIN
    fun esBestSeller(): Boolean = this.superaVentaSemanal() && (this.esPopular())
    fun lenguajeAutor() = this.autor.lenguaNativa
    fun unicoAutor(): Boolean = true //1 representa
    fun cantidadDeLenguajes(): Int = lenguajes().size
    fun lenguajes(): Set<Lenguaje> = (this.traducciones).plus(lenguajeAutor())

}



