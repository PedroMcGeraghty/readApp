package ar.edu.unsam.algo2.readapp.centrosDeLectura

import kotlin.random.Random

class Biblioteca(
    var metrosCuadrados: Double = (1..100).random()+ Random.nextDouble(),
    var gastosFijos: DoubleArray = doubleArrayOf()
) : CentroDeLectura() {

    override fun condicion(): Double {
        return (this.fijosTotales() + (this.fijosTotales() * this.obtenerPorcentaje())) / listaParticipantes.size
    }

    private fun obtenerPorcentaje(): Double {
        return if (fechasDeLectura.size < 5) ((fechasDeLectura.size) * 0.1) else 0.5
    }

    private fun fijosTotales(): Double = gastosFijos.sum()
    override fun puedeResevar(): Boolean = !cubreMetrosCuadrados() && hayFechasVigentes()
    override fun condicionExpirar(): Boolean = cubreMetrosCuadrados()
    private fun cubreMetrosCuadrados(): Boolean = listaParticipantes.size == metrosCuadrados.toInt()

}