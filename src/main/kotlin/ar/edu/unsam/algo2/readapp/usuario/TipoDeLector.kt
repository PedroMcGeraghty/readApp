package ar.edu.unsam.algo2.readapp.usuario

import ar.edu.unsam.algo2.readapp.libro.Libro

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///   ESTRUCTURA
///
///  <Interface> TipoLector: Suma o resta tiempoLectura segun condiciones.
///     Tipos:
///         <Objects>
///             -Promedio: NO aumenta/reduce tiempo extra
///
///             -Ansioso: Reducen tiempoLecturaBase en 20% o 50%(si el libro es BestSeller)
///
///             -Fanatico: //Si el autor es su preferido && NO leyo el libro:
///                          //Libro LARGO .EJ: PaginasParaLibroLargo = 600 =>>
///                              // =>> 2 minutos por pagina hasta 600 paginas
///                              // =>> 1 minuto por pagina para paginas restantes
///                          //Libro NO LARGO =>> tiempoLectura aumenta 2 minutos por pagina
///
///             -Recurrente: Leyendo los mismos libros estos van disminuyendo la velocidad de lectura promedio
///                          en 1% cada vez que lo vuelven a leer(max 5%)
///

interface TipoDeLector {
    fun tiempoDeLectura(usuario: Usuario, libro: Libro): Double
}

object Promedio : TipoDeLector {
    override fun tiempoDeLectura(usuario: Usuario, libro: Libro): Double = 0.0
}
object Ansioso : TipoDeLector {
    const val ESCALAR_DEFAULT = 0.2 // 20%
    const val ESCALAR_BEST_SELLER = 0.5 //50%

    override fun tiempoDeLectura(usuario: Usuario, libro: Libro): Double = -(usuario.tiempoLecturaBase(libro) * modificadorAnsioso(libro))

    //Metodo auxiliar que me devuelve escalar a multiplicar
    private fun modificadorAnsioso(libro: Libro): Double = if(libro.esBestSeller()) ESCALAR_BEST_SELLER else ESCALAR_DEFAULT // 50% y 20%
}
object Fanatico : TipoDeLector {

    //Interseccion entre Set<Usuario.autoresPreferidos> y Set<libro.autores>
    private fun esAutorPreferido(usuario: Usuario, libro: Libro): Boolean = usuario.autoresPreferidos.contains(libro.autor)

    private fun condicionesLectorFanatico(usuario: Usuario, libro: Libro) = esAutorPreferido(usuario, libro) && !usuario.leido(libro)

    fun tiempoLecturaLibroCorto(libro: Libro):Double = libro.cantidadPaginas * 2.0

    fun tiempoLecturaLibroLargo(libro: Libro):Double{
        return  (libro.cantidadPaginas - Libro.PAGINAS_LIBRO_LARGO_MIN)+ Libro.PAGINAS_LIBRO_LARGO_MIN * 2.0
    }

    private fun tiempoSegunLongitudLibro(libro: Libro):Double{
        return  if(!libro.esLargo()) tiempoLecturaLibroCorto(libro)
                else tiempoLecturaLibroLargo(libro)
    }

    override fun tiempoDeLectura(usuario: Usuario, libro: Libro): Double {
        return  if (condicionesLectorFanatico(usuario, libro)) tiempoSegunLongitudLibro(libro)
                else{0.0}
    }
}
object Recurrente : TipoDeLector {
    val porcentajeLimite = 0.05

    override fun tiempoDeLectura(usuario: Usuario, libro: Libro): Double{
        return -(usuario.tiempoLecturaBase(libro) * porcentajeReduccionTiempoLectura(usuario, libro))
    }

    fun porcentajeReduccionTiempoLectura(usuario: Usuario, libro: Libro): Double{
        return  if(usuario.cantidadLecturasDe(libro)>=5) porcentajeLimite
                else calcularPorcentajeReduccion(usuario, libro)
    }

    fun calcularPorcentajeReduccion(usuario: Usuario, libro: Libro):Double = usuario.cantidadLecturasDe(libro)*0.01
}