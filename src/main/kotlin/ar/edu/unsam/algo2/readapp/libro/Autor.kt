package ar.edu.unsam.algo2.readapp.libro

import ar.edu.unsam.algo2.readapp.repositorios.AvaliableInstance


class Autor(
    var lenguaNativa: Lenguaje = Lenguaje.values().random(),
    var edad: Int = (18..100).random(),
    var apellido: String = "",
    var nombre: String = "",
    var seudonimo: String = ""
) : AvaliableInstance {
    override var id: Int = -1
    var cantidadPremios: Int = 0

    companion object {
        const val EDAD_MINIMA_CONDICION: Int = 50
    }

    // agregar premios es como un setter, solo qe aumenta de a 1, no se setea por cantidad
    fun ganarPremio() {
        cantidadPremios += 1
    }

    fun esConsagrado(): Boolean = (this.superaEdad() && this.tienePremios())
    
    private fun tienePremios(): Boolean = cantidadPremios > 0
  
    private fun superaEdad(): Boolean = edad > EDAD_MINIMA_CONDICION

    override fun cumpleCriterioBusqueda(texto: String) = nombre.contains(texto) || apellido.contains(texto) || seudonimo === texto
}
