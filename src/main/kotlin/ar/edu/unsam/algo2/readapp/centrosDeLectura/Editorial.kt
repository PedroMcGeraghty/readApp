package ar.edu.unsam.algo2.readapp.centrosDeLectura

import kotlin.random.Random

class Editorial(var montoAlcanzable: Double = (1..100).random()+ Random.nextDouble() ) : CentroDeLectura() {

    val costoFijo: Double = 800.0
    val porcentajeBestSeller: Double = 0.1
    val precioNoBestseller: Double = 200.0

    var autorEstaPresente: Boolean = false

    override fun costoDeReserva(): Double = super.costoDeReserva() + costoFijoPorPersona()
    fun totalRecaudado() : Double = listaParticipantes.size * this.costoDeReserva()

    private fun costoFijoPorPersona(): Double = costoFijo * listaParticipantes.size

    override fun condicion(): Double =
        if (!autorEstaPresente) 0.0
        else if (autorPresenteYNoBestSeller()) precioNoBestseller
        else autorPresenteYBestSeller()

    private fun autorPresenteYBestSeller(): Double = libroDesignado.ventasSemanales * porcentajeBestSeller
    private fun autorPresenteYNoBestSeller(): Boolean = autorEstaPresente  && !libroDesignado.esBestSeller()

    override fun puedeResevar(): Boolean = !cubreMonto() && hayFechasVigentes()
    override fun condicionExpirar(): Boolean = cubreMonto()
    fun cubreMonto(): Boolean = (this.totalRecaudado()  >= montoAlcanzable )
    override fun autorAsiste() :Unit {
        autorEstaPresente = true
    }
}