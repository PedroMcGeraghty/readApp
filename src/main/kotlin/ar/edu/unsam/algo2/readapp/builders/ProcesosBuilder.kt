package ar.edu.unsam.algo2.readapp.builders

import ServiceLibros
import ar.edu.unsam.algo2.readapp.MailSender
import ar.edu.unsam.algo2.readapp.administrador.*
import ar.edu.unsam.algo2.readapp.centrosDeLectura.CentroDeLectura
import ar.edu.unsam.algo2.readapp.libro.Autor
import ar.edu.unsam.algo2.readapp.libro.Libro
import ar.edu.unsam.algo2.readapp.repositorios.Repositorio
import ar.edu.unsam.algo2.readapp.usuario.Usuario
import excepciones.BusinessException

class ProcesosBuilder(val _mailSender:MailSender) {
    val listaProcesos: MutableList<Proceso> = mutableListOf()

    fun borrarUsuariosInactivos(repositorioAsociado: Repositorio<Usuario>): ProcesosBuilder = apply {
        val proceso = BorrarUsuariosInactivos(repositorioAsociado, _mailSender)
        listaProcesos.add(proceso)
    }

    fun actualizarLibros(serviceLibros: ServiceLibros, repositorioAsociado: Repositorio<Libro>): ProcesosBuilder = apply {
        val proceso = ProcesoActualizadorLibros(serviceLibros, repositorioAsociado, _mailSender)
        listaProcesos.add(proceso)
    }

    fun borrarCentros(repositorioAsociado: Repositorio<CentroDeLectura>): ProcesosBuilder = apply {
        val proceso = ProcesoBorradoCentros(repositorioAsociado, _mailSender)
        listaProcesos.add(proceso)
    }

    fun agregarAutores(autoresNuevos: List<Autor>, repositorioAsociado: Repositorio<Autor>): ProcesosBuilder = apply {
        val proceso = ProcesoAgregarAutores(autoresNuevos, repositorioAsociado, _mailSender)
        listaProcesos.add(proceso)
    }
    fun build(): List<Proceso>{
        if(listaProcesos.isEmpty()){
            throw BusinessException("NO puede crear procesos vacios")
        }
        return listaProcesos
    }

}