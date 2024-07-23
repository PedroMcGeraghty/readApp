package ar.edu.unsam.algo2.readapp.centrosDeLectura

class Particular(var participantesMax: Int = (1..100).random()) : CentroDeLectura() {

    private val porcentajeEsperado: Double = 0.05
    private val valorExtra: Double = 500.0

    override fun condicion(): Double {
        return if (participantesMax > cantidadEsperada()) (participantesMax - cantidadEsperada().toInt()) * valorExtra else 0.0
    }
    override fun puedeResevar() = listaParticipantes.size < participantesMax
    override fun condicionExpirar(): Boolean = cubreParticipantes()

    fun cantidadEsperada(): Double = participantesMax * porcentajeEsperado
    private fun cubreParticipantes() = listaParticipantes.size == participantesMax
}