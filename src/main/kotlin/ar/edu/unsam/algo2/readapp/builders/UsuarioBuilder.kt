package ar.edu.unsam.algo2.readapp.builders

import ar.edu.unsam.algo2.readapp.libro.Lenguaje
import ar.edu.unsam.algo2.readapp.usuario.Usuario
import java.time.LocalDate

class UsuarioBuilder(val nuevoUsuario: Usuario = Usuario()) {
    
    fun nombre(nombre: String): UsuarioBuilder = apply {
        nuevoUsuario.nombre = nombre
    }

    fun apellido(apellido: String): UsuarioBuilder = apply {
        nuevoUsuario.apellido = apellido
    }

    fun alias(alias: String): UsuarioBuilder = apply {
        nuevoUsuario.alias = alias
    }

    fun fechaNacimiento(date: LocalDate): UsuarioBuilder = apply {
        nuevoUsuario.fechaNacimiento = date
    }

    fun velocidadLectura(velocidad: Int): UsuarioBuilder = apply {
        nuevoUsuario.palabrasPorMinutos = velocidad
    }

    fun email(email: String): UsuarioBuilder = apply {
        nuevoUsuario.direccionMail = email
    }

    fun lenguaje(lenguaje: Lenguaje): UsuarioBuilder = apply {
        nuevoUsuario.lenguaje = lenguaje
    }

    fun build(): Usuario = nuevoUsuario
}