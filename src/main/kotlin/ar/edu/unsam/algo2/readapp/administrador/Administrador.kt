package ar.edu.unsam.algo2.readapp.administrador


class Administrador(){

    val listaProcesos: MutableList<Proceso> = mutableListOf()

    val procesosEjecutados: MutableList<Proceso> = mutableListOf()

    fun ejecutarListaProcesos(){
        listaProcesos.forEach{  it.ejecutar()  }
    }

    fun agregarProceso(proceso:Proceso){
        listaProcesos.add(proceso)
    }

    fun agregarProcesos(procesos:List<Proceso>){
        listaProcesos.addAll(procesos)
    }
}