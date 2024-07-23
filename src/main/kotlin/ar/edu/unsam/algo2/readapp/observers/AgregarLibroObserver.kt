package ar.edu.unsam.algo2.readapp.observers

import ar.edu.unsam.algo2.readapp.Mail
import ar.edu.unsam.algo2.readapp.MailSender
import ar.edu.unsam.algo2.readapp.features.Recomendacion
import ar.edu.unsam.algo2.readapp.features.Valoracion
import ar.edu.unsam.algo2.readapp.libro.Libro
import ar.edu.unsam.algo2.readapp.usuario.Usuario

interface AgregarLibroObserver {


   abstract fun ejecutar(libro: Libro, usuario: Usuario, recomendacion: Recomendacion)
}




/*
Llevar un registro por recomendación, donde se pueda contabilizar el aporte de cada usuario que agrega un
 libro (sin discriminar al creador), es decir que, este registro debe registrar los libros que aportó cada
  usuario.
 */

open class ObserverRegistro() : AgregarLibroObserver{
   val registroAportes : MutableMap<Usuario, Int> = mutableMapOf()

   override fun ejecutar(libro: Libro, usuario: Usuario, recomendacion: Recomendacion) {
      registroAportes[usuario] = this.aportesPorUsuario(usuario) + 1
   }

   fun aportesPorUsuario(usuario: Usuario): Int {
      return registroAportes[usuario] ?: 0
   }

}

class ObserverVetador(val maximo: Int) : ObserverRegistro() {

   override fun ejecutar(libro: Libro, usuario: Usuario, recomendacion: Recomendacion) {
      if (registroAportes.getOrDefault(usuario, 0) >= maximo) {
         recomendacion.creador.vetarAmigo(usuario)
      }
      registroAportes[usuario] = (registroAportes[usuario] ?: 0) + 1
   }
}


class MailSenderObserver(val mailSender: MailSender) : AgregarLibroObserver {

   override fun ejecutar(libro: Libro, usuario: Usuario, recomendacion: Recomendacion) {
      if (!recomendacion.esCreador(usuario)) {
         mailSender.enviarMail(
            Mail(from = "notificaciones@readapp.com.ar",
                  to = recomendacion.creador.direccionMail,
                  asunto = "Se agregó un Libro",
                  cuerpo = this.armarCuerpo(libro, usuario, recomendacion)
            )
         )
      }
   }

   private fun armarCuerpo(libro: Libro, usuario: Usuario, recomendacion: Recomendacion): String {
      return "El usuario: ${usuario.nombre} agrego el Libro ${libro.titulo}" +
              " a la recomendación que tenía estos Títulos:" +
              " ${recomendacion.librosRecomendados.subtract(mutableSetOf(libro))}"
   }


class ObserverValoracionExcelente(): AgregarLibroObserver {
   val comentario: String = "Excelente 100% recomendable"
   override fun ejecutar(libro: Libro, usuario: Usuario, recomendacion: Recomendacion) {
      if (!recomendacion.esCreador(usuario) && !recomendacion.fueValoradaPor(usuario)) {
         recomendacion.valoraciones.add(Valoracion(Valoracion.VALOR_MAX, comentario, usuario))
      }
   }
}}