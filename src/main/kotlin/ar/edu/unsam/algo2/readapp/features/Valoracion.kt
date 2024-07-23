package ar.edu.unsam.algo2.readapp.features
import ar.edu.unsam.algo2.readapp.usuario.Usuario
import excepciones.BusinessException

class Valoracion(var valor: Int, var comentario: String, var autor: Usuario) {
    companion object{
        val VALOR_MIN:Int = 1
        val VALOR_MAX:Int = 5
    }
    //EDITAR VALORACION
    fun editarValoracion(recomendacion: Recomendacion, valor: Int, comentario: String, usuario: Usuario) {
        if (!usuario.recomendoValoracion(recomendacion)) {
            throw BusinessException("No puede editar la valoracion")
        }
        this.validarValoracion(valor)
        this.modificarValoracion(valor, comentario)
    }
    fun modificarValoracion(valor: Int, comentario: String) {
        this.valor = valor
        this.comentario = comentario
    }
    fun validarValoracion(valoracion: Int) {
        if (this.validarPuntaje(valoracion)) {
            throw Exception("Puntaje de Valoracion invalido")
        }
    }
    fun validarPuntaje(valoracion: Int) = valoracion < VALOR_MIN || valoracion > VALOR_MAX

}