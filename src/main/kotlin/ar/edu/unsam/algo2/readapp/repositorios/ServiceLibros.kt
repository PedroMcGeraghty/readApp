import ar.edu.unsam.algo2.readapp.libro.Libro
import ar.edu.unsam.algo2.readapp.repositorios.Repositorio
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import excepciones.BusinessException


interface ServiceLibros {
    fun getLibros(): String
}

object ActualizadorLibro {

    fun actualizar(servicio: ServiceLibros, repositorio: Repositorio<Libro>) {
        val mapper = ObjectMapper().registerKotlinModule()
        val listaLibros: List<JsonLibro> = mapper.readValue<List<JsonLibro>>(servicio.getLibros())
        val listaLibrosActualizados = listaLibros.map { actualizarLibro(it, repositorio) }
        listaLibrosActualizados.forEach { repositorio.update(it) }
    }

    fun actualizarLibro(datosNuevos: JsonLibro, repositorio: Repositorio<Libro>): Libro {
        checkeoValidez(datosNuevos)
        val libroAActualizar: Libro = repositorio.getByID(datosNuevos.id)
        val libroActualizado: Libro = Libro().apply {
            id = datosNuevos.id
            autor = (libroAActualizar.autor)
            cantidadPalabras = (libroAActualizar.cantidadPalabras)
            cantidadPaginas = (libroAActualizar.cantidadPaginas)
            ediciones = (datosNuevos.ediciones)
            ventasSemanales = (datosNuevos.ventasSemanales)
            traducciones = (libroAActualizar.traducciones)
            esComplejo = libroAActualizar.esComplejo
            titulo = (libroAActualizar.titulo)
        }
        return libroActualizado
    }

    fun checkeoValidez(libro: JsonLibro) {
        if (libro.ediciones <= 0 || libro.ventasSemanales < 0) {
            throw BusinessException("El formato del libro es inválido")
        }
    }
}

data class JsonLibro(val id: Int, val ediciones: Int, val ventasSemanales: Int, val numeros: Int)




