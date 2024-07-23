package ar.edu.unsam.algo2.readapp

import LibroBuilder
import ar.edu.unsam.algo2.readapp.builders.AutorBuilder
import ar.edu.unsam.algo2.readapp.builders.UsuarioBuilder
import ar.edu.unsam.algo2.readapp.libro.Lenguaje
import ar.edu.unsam.algo2.readapp.observers.MailSenderObserver
import ar.edu.unsam.algo2.readapp.observers.ObserverRegistro

import ar.edu.unsam.algo2.readapp.observers.ObserverVetador
//import ar.edu.unsam.algo2.readapp.observers.

import excepciones.BookException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain

import io.kotest.matchers.collections.shouldBeEmpty

import io.kotest.matchers.shouldBe
import stubs.MailSenderStub
import java.time.LocalDate

class ObserverSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest //ojo sin esto. Algunos estados quedan guardados SIN isolationMode
    val autorArabe = AutorBuilder().lenguaNativa(Lenguaje.ARABE).build()

    val libroA1 = LibroBuilder().autor(autorArabe).traducciones(mutableSetOf(Lenguaje.RUSO, Lenguaje.ARABE)).build()
    val libroA2 = LibroBuilder().build()
    val libroB1 = LibroBuilder().autor(autorArabe).build()

    val usuarioA = UsuarioBuilder()
        .nombre("hernan")
        .apellido("lopez")
        .alias("hl")
        .email("hernanLopez@gmail.com")
        .fechaNacimiento(LocalDate.now().minusYears(1))
        .velocidadLectura(1)
        .build()
    val usuarioB = UsuarioBuilder()
        .nombre("mario")
        .apellido("perez")
        .alias("mp")
        .email("marioPerez@gmail.com")
        .fechaNacimiento(LocalDate.now().minusYears(1))
        .velocidadLectura(1)
        .build()

    val usuarioC = UsuarioBuilder()
        .nombre("pepito")
        .apellido("moralez")
        .alias("pm")
        .email("moralezPepito@gmail.com")
        .fechaNacimiento(LocalDate.now().minusYears(1))
        .velocidadLectura(1)
        .build()

    val observerRegistro = ObserverRegistro()
    val observerVetador = ObserverVetador(2)

    describe("testeo de observers") {

        val StubMailSender = MailSenderStub()
        describe("contador observer") {
            usuarioA.leer(libroA1)
            usuarioA.leer(libroA2)
            usuarioA.leer(libroB1)

            usuarioB.leer(libroB1)
            usuarioB.leer(libroA1)
            usuarioB.leer(libroA2)

            usuarioA.agregarAmigo(usuarioB)
            usuarioB.agregarObserver(observerRegistro)

            it("modifico recomendacion") {
                usuarioA.crearRecomendacion(
                    titulo="recomendacion de libro a1",
                    librosParaRecomendar = mutableSetOf(libroA1),
                    contenido = "el mejor libro de todos",
                    publico = true
                )

                usuarioA.recomendaciones.first().esAccesiblePara(usuarioB) shouldBe true
                usuarioB.editarRecomendacionDe(
                    recomendacion = usuarioA.recomendaciones.first(),
                    librosParaAgregar = mutableSetOf(libroB1, libroA2)
                )
                usuarioA.recomendaciones.first().librosRecomendados.size shouldBe 3
                observerRegistro.aportesPorUsuario(usuarioB) shouldBe 2
            }
        }

        describe("vetar observer maximo 2 años"){
            usuarioA.leer(libroA1)
            usuarioA.leer(libroA2)
            usuarioA.leer(libroB1)

            usuarioB.leer(libroB1)
            usuarioB.leer(libroA1)
            usuarioB.leer(libroA2)

            usuarioA.agregarAmigo(usuarioB)
            usuarioB.agregarObserver(observerRegistro)

            usuarioA.crearRecomendacion(
                "recomendacion de libro a1",
                mutableSetOf(libroA1), "el mejor libro de todos", true
            )
            it("modifico recomendacion") {
                usuarioB.agregarObserver(observerVetador)
                usuarioA.recomendaciones.first().esAccesiblePara(usuarioB) shouldBe true

                usuarioB.editarRecomendacionDe(usuarioB,usuarioA.recomendaciones.first(),mutableSetOf(libroB1,libroA2))
                usuarioA.amigos.shouldContain(usuarioB)

                usuarioB.editarRecomendacionDe(usuarioB,usuarioA.recomendaciones.first(),mutableSetOf(libroA1))
                usuarioA.amigos.shouldNotContain(usuarioB)
            }
        }

        describe("MailSenderObserver") {
            usuarioA.apply {
                leer(libroA1)
                leer(libroA2)
                crearRecomendacion("Recomendacion Usuario A", mutableSetOf(libroA1), "", true)
                agregarAmigo(usuarioB)
            }
            val recomendacionA = usuarioA.recomendaciones[0]

            usuarioB.apply {
                agregarObserver(MailSenderObserver(StubMailSender))
                leer(libroA2)
                leer(libroA1)
            }

            //Resetea el MailSender despues de cad it
            //afterSpec { MailSenderStub.reset() }

            it("UsuarioB no es el creador por lo tanto se tiene que enviar un mail") {
                usuarioB.editarRecomendacionDe(
                    recomendacion = recomendacionA,
                    librosParaAgregar = mutableSetOf(libroA2)
                )

                StubMailSender.filtrarEmailDestino(usuarioA.direccionMail).size shouldBe 1   //Tiene que haber un email destinado para el autor de la lista
            }

            it("Verifico que el mail se este enviando desde la casilla correcta") {
                usuarioB.editarRecomendacionDe(
                    recomendacion = recomendacionA,
                    librosParaAgregar = mutableSetOf(libroA2)
                )

                StubMailSender.filtrarEmailDestino(usuarioA.direccionMail)[0].from shouldBe "notificaciones@readapp.com.ar"
            }

            it("El Usuario A es el creador, por lo tanto al agregar libros no se dispara el mail") {
                usuarioA.apply { agregarObserver(MailSenderObserver(StubMailSender)) }
                usuarioA.editarRecomendacionDe(
                    recomendacion = recomendacionA,
                    librosParaAgregar = mutableSetOf(libroA2)
                )
                StubMailSender.mailsEnviados.shouldBeEmpty()
            }
        }

        describe("Valoracion excelente observer"){
            usuarioA.apply {
                leer(libroA1)
                leer(libroA2)
                crearRecomendacion("Recomendacion Usuario A", mutableSetOf(libroA1), "", true)
                agregarAmigo(usuarioB)
            }
            val recomendacionA = usuarioA.recomendaciones[0]

            usuarioB.apply {
                agregarObserver(MailSenderObserver.ObserverValoracionExcelente())
                leer(libroA2)
                leer(libroA1)
            }

            it("Usuario B edita recomendacion NO valorada del usuario A, se agrega valoracion excelente"){
                recomendacionA.esCreador(usuarioB) shouldBe(false)
                recomendacionA.fueValoradaPor(usuarioB) shouldBe(false)
                usuarioB.editarRecomendacionDe(recomendacion = recomendacionA,librosParaAgregar = mutableSetOf(libroA2))
                recomendacionA.fueValoradaPor(usuarioB) shouldBe(true)
            }

        }

    }
})

