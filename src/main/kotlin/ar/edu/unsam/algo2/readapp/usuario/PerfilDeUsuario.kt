package ar.edu.unsam.algo2.readapp.usuario

import ar.edu.unsam.algo2.readapp.features.Recomendacion
import ar.edu.unsam.algo2.readapp.libro.Libro

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///   ESTRUCTURA
///
///  <Interface> PerfilDeUsuario: Filtra recomendaciones segun condiciones.
///     Tipos:
///         <Objects>
///             -Precavido: recomendaciones de libros que por lo menos incluyan uno de los libros que
///                         tiene pendiente lectura o que amigo haya leído.
///
///             -Leedor: no tiene una preferencia específica, por lo que le interesa cualquier recomendación.
///
///             -Poliglota: le gustaría ver recomendaciones que tengan por lo menos 5 idiomas distintos.
///
///             -Nativista: espera recomendaciones que tengan libros cuyo idioma original sea el mismo nativo de él.
///
///             -Demandante: quiere que le ofrezcamos recomendaciones que tengan una valoración de entre 4 y 5 puntos.
///
///             -Experimentado: quiere recomendaciones de libros donde la mayoría sean autores consagrados (es decir, que tengan 50 o más años de edad
///                              y tenga por lo menos un premio como escritor).
///
///             -Cambiante: son los que se comportan como leedor hasta los 25 años de edad, luego se comportan como un calculador con una tolerancia
///                         de 10.000 a 15.000 minutos.
///         <Class>
///             -Calculador: como le gusta tener control del tiempo que lee, acepta recomendaciones que no le lleven más de un rango de tiempo
///                          (el tiempo correspondiente a leer toda la serie de libros), este rango puede variar entre los distintos usuarios

interface PerfilDeUsuario {
    abstract fun condicion(recomendacion: Recomendacion, usuario: Usuario): Boolean
    fun recomendacionEsInteresante(recomendacion: Recomendacion, usuario: Usuario) = condicion(recomendacion, usuario)
}

object Precavido : PerfilDeUsuario {
    override fun condicion(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        usuario.tieneLibrosPendientes(recomendacion.librosRecomendados).isNotEmpty() || tieneLibrosLeidosPorAmigo(
            recomendacion,
            usuario
        )

    private fun tieneLibrosLeidosPorAmigo(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        usuario.librosLeidosAmigos().intersect(recomendacion.librosRecomendados).isNotEmpty()
}

object Leedor : PerfilDeUsuario {
    override fun condicion(recomendacion: Recomendacion, usuario: Usuario): Boolean = true
}

object Poliglota : PerfilDeUsuario {
    override fun condicion(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        recomendacion.cantidadDeLenguajes() >= 5

}

object Nativista : PerfilDeUsuario {
    override fun condicion(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        recomendacion.librosRecomendados.any { it.lenguajeAutor() == (usuario.lenguaje) }
}

class Calculador(var rangoMin: Double, var rangoMax: Double) : PerfilDeUsuario {

    override fun condicion(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        recomendacion.tiempodeLectura(usuario) in this.rango()

    private fun rango() = rangoMin..rangoMax

    fun modificarRangoDeRecomendacion(rangoMin: Double, rangoMax: Double) {
        if (rangoMin < 0.0 || rangoMax < 0.0) {
            throw IllegalArgumentException("El nuevo rango de recomendación debe ser válido (start >= 0 y endInclusive >= start)")
        }
        this.rangoMin = rangoMin
        this.rangoMax = rangoMax
    }
}

object Demandante : PerfilDeUsuario {
    override fun condicion(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        recomendacion.valoraciones.any { it.valor in 4..5 }
}

object Experimentado : PerfilDeUsuario {
    override fun condicion(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        cantidadAutoresConsagrados(recomendacion) > (cantidadAutores(recomendacion) / 2)

    private fun cantidadAutoresConsagrados(recomendacion: Recomendacion): Int =
        recomendacion.librosRecomendados.count { libro -> autoresConsagrados(libro) }


    //Funcion que me devuelve la cantidad total de autores de todos los libros
    private fun cantidadAutores(recomendacion: Recomendacion): Int = recomendacion.librosRecomendados.size

    //Funcion que me devuelve una lista con los autores consagrados del libro
    private fun autoresConsagrados(libro: Libro) = libro.autor.esConsagrado()
}

object Cambiante : PerfilDeUsuario {

    val EDAD_MAX_LEEDOR: Int = 25
    override fun condicion(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        checkeoPerfil(usuario).condicion(recomendacion, usuario)


    private fun checkeoPerfil(usuario: Usuario): PerfilDeUsuario {
        if (esLeedor(usuario)) {
            return Leedor
        }
        usuario.cambioPerfil(Calculador(10000.0, 15000.0))
        return usuario.perfil
    }

    private fun esLeedor(usuario: Usuario): Boolean = usuario.edad() < EDAD_MAX_LEEDOR

}

class Combinador(val perfiles : MutableSet<PerfilDeUsuario> ) : PerfilDeUsuario {
    override fun condicion(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        perfiles.all { it.condicion(recomendacion,usuario) }

    fun agregarPerfil(perfil : PerfilDeUsuario){
        perfiles.add(perfil)
    }
    fun eliminarPerfil(perfil : PerfilDeUsuario){
        perfiles.remove(perfil)
    }
}