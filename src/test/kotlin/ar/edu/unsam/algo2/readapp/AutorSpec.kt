package ar.edu.unsam.algo2.readapp

import ar.edu.unsam.algo2.readapp.builders.AutorBuilder
import excepciones.BookException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class AutorSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest //ojo sin esto. Algunos estados quedan guardados SIN isolationMode

    describe("Correcta inicializacion"){
        shouldNotThrow<BookException> { AutorBuilder().build() }
    }
    describe("Dado un Autor"){
        val autorX =  AutorBuilder().build()
        it("Agregar premio"){
            autorX.cantidadPremios shouldBe 0
            autorX.ganarPremio()
            autorX.cantidadPremios shouldBe 1
        }
        describe("CONSAGRADO - Supera edad && tiene premios"){
            it("SOLO edad"){
                autorX.apply { edad = 51 }
                autorX.apply { cantidadPremios = 0 }
                autorX.esConsagrado() shouldBe(false)
            }
            it("SOLO premios"){
                autorX.apply { edad = 50 }
                autorX.apply { cantidadPremios = 1 }
                autorX.esConsagrado() shouldBe(false)
            }
            autorX.apply {
                edad = 51
                cantidadPremios = 1
            }
            autorX.esConsagrado() shouldBe(true)
        }
    }
})


