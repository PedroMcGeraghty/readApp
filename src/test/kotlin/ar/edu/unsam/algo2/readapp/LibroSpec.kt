package ar.edu.unsam.algo2.readapp

import LibroBuilder
import ar.edu.unsam.algo2.readapp.builders.AutorBuilder
import ar.edu.unsam.algo2.readapp.libro.Autor
import ar.edu.unsam.algo2.readapp.libro.Lenguaje
import ar.edu.unsam.algo2.readapp.libro.Libro
import excepciones.BookException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe

//Acá van los tests de Libro

class LibroSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest
    val autor = Autor(lenguaNativa = Lenguaje.ESPANIOL, edad = 20,
        nombre = "pedrito",
        apellido =  "gomez",
        seudonimo =  "prueba")
    describe("Correcta Inicializacion del libro") {




        val libroPrueba = LibroBuilder()
            .autor(autor)
            .cantidadPalabras(100)
            .cantidadPaginas(100)
            .ediciones(3)
            .ventasSemanales(10)
            .traducciones(mutableSetOf( Lenguaje.ESPANIOL))
            .esComplejo(false)
            .titulo("algoritmo 2")
            .build()

        it("se crea exitosamente"){
            libroPrueba.cantidadPaginas.shouldBe(100)
        }

        it("No puede instanciarse con determinadas variables vacías") {
            val exception = shouldThrow<BookException> {
                LibroBuilder()
                    .cantidadPaginas(0)
                //    .cantidadPalabras(0)
                    .build()
            }
            exception.message shouldBe "El valor de cantidadPaginas no puede ser menor o igual 0"
            //exception.message shouldBe "El valor de cantidadPalabras no puede ser menor o igual a 0"
        }
        it("Inicializacion por default NO hay error") {

            val libro3 = shouldNotThrow<BookException> {LibroBuilder().build()}
        }
    }
    describe("Dado un libro") {

        val libroLargo = LibroBuilder().largo().build()

        describe("esLargo() - SEGUN CANTIDAD PAGINAS") {

            it("El libro debe ser considerado largo") {
                libroLargo.cantidadPaginas.shouldBeGreaterThan(Libro.PAGINAS_LIBRO_LARGO_MIN)
                libroLargo.esLargo() shouldBe true
            }

            it("NO LARGO") {
                val libroNoLargo = LibroBuilder().build()
                libroNoLargo.cantidadPaginas shouldBeLessThanOrEqual Libro.PAGINAS_LIBRO_LARGO_MIN
                libroNoLargo.esLargo() shouldBe (false)
            }

        }
    }



                    

        describe("DESAFIANTE - SEGUN esComplejo || esLargo") {
            val libroSoloLargo = LibroBuilder().largo().build()
            val libroSoloComplejo = LibroBuilder().complejo().build()
            it("esComplejo => esDesafiante") {
                libroSoloLargo.esLargo() shouldBe (true)
                libroSoloLargo.esComplejo shouldBe (false)
                libroSoloLargo.esDesafiante() shouldBe (true)
            }
            it("esLargo() => esDesafiante") {
                libroSoloComplejo.esLargo() shouldBe (false)
                libroSoloComplejo.esComplejo shouldBe (true)
                libroSoloComplejo.esDesafiante() shouldBe (true)
            }
        }

        it("Sin traducciones - Vacio por default") {
            val libroSinTraducciones = LibroBuilder().build()
            libroSinTraducciones.traducciones.size shouldBeEqual 0
            libroSinTraducciones.sinTraducciones() shouldBe(true)
        }

        describe("varias TRADUCCIONES - Libro.TRADUCCIONES_MIN") {
            val traducciones = mutableSetOf(
                Lenguaje.ESPANIOL, Lenguaje.INGLES, Lenguaje.RUSO,
                Lenguaje.ALEMAN, Lenguaje.ARABE)
            val libroVariasTraducciones = LibroBuilder().traducciones(traducciones).build()

            libroVariasTraducciones.traducciones.size shouldBeGreaterThanOrEqual Libro.TRADUCCIONES_MIN
            libroVariasTraducciones.variasTraducciones() shouldBe(true)
            it("NO SUPERA"){
                val traducciones = mutableSetOf(
                    Lenguaje.ESPANIOL, Lenguaje.INGLES,
                    Lenguaje.RUSO, Lenguaje.ALEMAN)
                val libroNoVariasTraducciones = LibroBuilder().traducciones(traducciones).build()

                libroNoVariasTraducciones.ediciones shouldBeLessThan  Libro.TRADUCCIONES_MIN
                libroNoVariasTraducciones.variasTraducciones() shouldBe(false)
            }
        }

        describe("varias EDICIONES - Libro.EDICIONES_MIN") {
            val libroVariasEdiciones = LibroBuilder().ediciones(Libro.EDICIONES_MIN+1).build()
            libroVariasEdiciones.ediciones shouldBeGreaterThan  Libro.EDICIONES_MIN
            libroVariasEdiciones.variasEdiciones() shouldBe(true)
            it("NO SUPERA"){
                val libroNoVariasEdiciones = LibroBuilder().ediciones(Libro.EDICIONES_MIN).build()
                libroNoVariasEdiciones.ediciones shouldBeLessThanOrEqual Libro.EDICIONES_MIN
                libroNoVariasEdiciones.variasEdiciones() shouldBe(false)
            }
        }

        describe("POPULAR - SEGUN variasTraducciones || variasEdiciones") {
            val libroVariasEdiciones = LibroBuilder().ediciones(Libro.EDICIONES_MIN+1).build()
            val traducciones = mutableSetOf(
                Lenguaje.ESPANIOL, Lenguaje.INGLES, Lenguaje.RUSO,
                Lenguaje.ALEMAN, Lenguaje.ARABE)
            val libroVariasTraducciones = LibroBuilder().traducciones(traducciones).build()
            it("variasTraducciones => esPopular") {
                libroVariasEdiciones.variasTraducciones() shouldBe(false)
                libroVariasEdiciones.variasEdiciones() shouldBe(true)
                libroVariasEdiciones.esPopular() shouldBe(true)
            }
            it("variasEdiciones => esPopular") {
                libroVariasTraducciones.variasTraducciones() shouldBe(true)
                libroVariasTraducciones.variasEdiciones() shouldBe(false)
                libroVariasTraducciones.esPopular() shouldBe(true)
            }
        }

        describe("BEST SELLET - SEGUN superaVentaSemanal() && esPopular()") {
            val libroBestSeller = LibroBuilder().bestSeller().build()
            it("FALLAN 1 CONDICION"){
                val libroSoloMuyVendido = LibroBuilder().ventasSemanales(Libro.VENTA_SEMANAL_MIN).build()
                val libroSoloPopular = LibroBuilder().popular().build()

                libroSoloMuyVendido.esPopular() shouldBe(false)
                libroSoloMuyVendido.superaVentaSemanal() shouldBe(true)
                libroSoloMuyVendido.esBestSeller() shouldBe(false)

                libroSoloPopular.esPopular() shouldBe(true)
                libroSoloPopular.superaVentaSemanal() shouldBe(false)
                libroSoloPopular.esBestSeller() shouldBe(false)
            }
            libroBestSeller.esPopular() shouldBe(true)
            libroBestSeller.superaVentaSemanal() shouldBe(true)
            libroBestSeller.esBestSeller() shouldBe(true)
        }

        describe("lenguaje original = lenguaje AUtores"){
            val autorRuso = AutorBuilder().lenguaNativa(Lenguaje.RUSO).build()
            val libroRuso = LibroBuilder().autor(autorRuso).build()
            libroRuso.lenguajeAutor() shouldBeEqual Lenguaje.RUSO
        }

        describe("unico autor"){
            val autorGenerico = AutorBuilder().build()
            val libroUnicoAutor = LibroBuilder().autor(autorGenerico).build()
            libroUnicoAutor.unicoAutor() shouldBe(true)
        }

        describe("Libro.lenguaje() = lenguajeOriginal + Traducciones"){
            val traducciones = mutableSetOf(Lenguaje.ESPANIOL, Lenguaje.ARABE)
            val autorRuso = AutorBuilder().lenguaNativa(Lenguaje.RUSO).build()
            val libroMultilingue = LibroBuilder().autor(autorRuso).traducciones(traducciones).build()
            libroMultilingue.lenguajes() shouldBeEqual mutableSetOf(Lenguaje.ESPANIOL, Lenguaje.ARABE, Lenguaje.RUSO)
        }

})
