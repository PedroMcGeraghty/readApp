package ar.edu.unsam.algo2.readapp

import LibroBuilder
import ar.edu.unsam.algo2.readapp.builders.AutorBuilder
import ar.edu.unsam.algo2.readapp.builders.UsuarioBuilder
import ar.edu.unsam.algo2.readapp.features.Recomendacion
import ar.edu.unsam.algo2.readapp.libro.Lenguaje
import excepciones.BusinessException
import excepciones.RecomendacionException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import java.time.LocalDate


class RecomendacionSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest


    describe("Dada una recomendacion") {
        val autorArabe = AutorBuilder().lenguaNativa(Lenguaje.ARABE).build()

        val libroA1 = LibroBuilder().autor(autorArabe).traducciones(mutableSetOf(Lenguaje.RUSO, Lenguaje.ARABE)).build()
        val libroA2 = LibroBuilder().build()
        val libroB1 = LibroBuilder().build()

        val usuarioA = UsuarioBuilder()
            .nombre("hernan")
            .apellido("lopez")
            .alias("hl")
            .fechaNacimiento(LocalDate.now().minusYears(1))
            .velocidadLectura(1)
            .build()

        val usuarioB = UsuarioBuilder()
            .nombre("mario")
            .apellido("perez")
            .alias("mp")
            .fechaNacimiento(LocalDate.now().minusYears(1))
            .velocidadLectura(1)
            .build()

        val usuarioC = UsuarioBuilder()
            .nombre("pepito")
            .apellido("moralez")
            .alias("pm")
            .fechaNacimiento(LocalDate.now().minusYears(1))
            .velocidadLectura(1)
            .build()


        val recomendacionPocoTraducida = Recomendacion(librosRecomendados = mutableSetOf(libroA1),creador = usuarioA, titulo = "recomendacion poco traducida", contenido = "pocos lenguajes")
        describe("Crear recomendacion") {
            describe("Debe haber leidos los libros") {
                describe("dado un libro") {

                    it("aplicamos el metodo leer y agregar a recomendados ") {
                        usuarioA.leido(libroA1) shouldBe (false)

                        usuarioA.leer(libroA1)
                        usuarioA.leido(libroA1) shouldBe (true)
                        usuarioA.librosLeidos.size shouldBeGreaterThan 0

                    }
                    it("ya leido el libro, procedo a crear la recomendacion ") {
                        usuarioA.leer(libroA1)

                        usuarioA.crearRecomendacion(
                            "recomendacion de libro a1",
                            mutableSetOf(libroA1), "el mejor libro de todos", true
                        )
                        usuarioA.recomendaciones.size shouldBeGreaterThan 0
                    }
                }
                describe("dado un libro no leido, no se podra crear la recomendacion") {

                    it("se verifica que el libro no fue leido") {}
                    usuarioA.leido(libroA1) shouldBe (false)

                    it("Throw Error") {

                        shouldThrow<RecomendacionException> {
                            usuarioA.crearRecomendacion(
                                "A",
                                mutableSetOf(libroA1),
                                "esto no se podria realizar",
                                true
                            )
                        }
                    }
                }
            }

            describe("Accesibilidad") {
                usuarioB.leer(libroB1)
                usuarioB.crearRecomendacion("B", mutableSetOf(libroB1), "blabla", true)

                describe("PUBLICA") {
                    describe("todos acceden") {
                        it("Usuario B crea recomendacion publica, usuario A puede acceder") {
                            usuarioB.recomendaciones.first().esAccesiblePara(usuarioA) shouldBe (true)
                            usuarioA.buscarRecomendacionesPor(usuarioB) shouldBeEqual listOf(usuarioB.recomendaciones.first())
                        }
                    }
                }

                describe("PRIVADA") {
                    //Recomendacion creada pasa a ser privada
                    usuarioB.recomendaciones.first().publica = false
                    usuarioB.recomendaciones.first().accesoPublico() shouldBe (false)

                    //usuarioB.recomendaciones.first().criterioPrivado()
                    describe("solo amigos") {
                        usuarioB.agregarAmigo(usuarioC)
                        //USUARIO A NO PUEDE ACCEDER
                        usuarioB.recomendaciones.first().esAccesiblePara(usuarioA) shouldBe (false)
                        usuarioA.buscarRecomendacionesPor(usuarioB) shouldBeEqual listOf<Recomendacion>()
                        //USUARIO C PUEDE ACCEDER
                        usuarioB.recomendaciones.first().esAccesiblePara(usuarioC) shouldBe (true)
                        usuarioC.buscarRecomendacionesPor(usuarioB) shouldBeEqual listOf(usuarioB.recomendaciones.first())
                    }
                }
            }

        }

        describe("Dado un usuario B amigo de un usuario A saber si puede editar su recomendacion") {
            //Arrange
            val libroNuevo = LibroBuilder().build()
            //Act
            usuarioA.leer(libroA1)
            usuarioA.leer(libroA2)
            usuarioA.leer(libroB1)
            usuarioA.amigos.add(usuarioB)
            usuarioB.leer(libroA1)
            usuarioB.leer(libroA2)
            usuarioB.leer(libroB1)
            usuarioA.crearRecomendacion("Recomendacion", mutableSetOf(libroA1, libroA2), "algo", true)

            //Assert
            it("usuarioB debería poder editar la recomendación creada por usuarioA") {
                usuarioA.recomendaciones.first().esEditablePor(usuarioB, mutableSetOf(libroB1)) shouldBe true
            }

            it("usuarioA debería poder editar su propia recomendacion") {
                usuarioA.recomendaciones.first().esEditablePor(usuarioA, mutableSetOf(libroB1)) shouldBe true
            }

            describe("Si no son amigos usuario B no deberia poder editar la recomendacion de usuarioA") {
                usuarioA.amigos.remove(usuarioB)
                it("usuario B no debería poder editar la recomendación creada por usuarioA") {
                    usuarioA.recomendaciones.first().esEditablePor(usuarioB, mutableSetOf(libroB1)) shouldBe false
                }
            }

            describe("Si el creador no leyo el libro no puede editar su recomendacion") {

                it("El creador no puede editar su recomendacion por que no leyo el libro") {
                    usuarioA.recomendaciones.first().esEditablePor(usuarioA, mutableSetOf(libroNuevo)) shouldBe false
                }
            }

            describe("Si leyeron o no los libros para poder editar") {
                usuarioA.amigos.add(usuarioB)
                usuarioA.leer(libroNuevo)
                it("El amigo no puede editar su recomendacion por que no leyo el libro y el creador si") {
                    usuarioA.recomendaciones.first().esEditablePor(usuarioB, mutableSetOf(libroNuevo)) shouldBe false
                }
                usuarioB.leer(libroNuevo)
                it("El amigo  puede editar su recomendacion por que ambos leyeron el libro") {
                    usuarioA.recomendaciones.first().esEditablePor(usuarioB, mutableSetOf(libroNuevo)) shouldBe true
                }
                usuarioA.librosLeidos.remove(libroNuevo)
                it("El usuario B no puede editar la recomendacion por que el libro A no lo leyo") {
                    usuarioA.recomendaciones.first().esEditablePor(usuarioB, mutableSetOf(libroNuevo)) shouldBe false
                }

            }

            describe("Si el usuario B no leyo los libros recomendados no puede editar") {
                usuarioB.librosLeidos.clear()
                it("El usario B no podria editar los libros de la recomendacion por que no leyo los recomendados") {
                    usuarioA.recomendaciones.first().esEditablePor(usuarioB, mutableSetOf(libroA1)) shouldBe false
                }
            }
        }

        describe("Recomendaciones pendientes a valorar") {
            //Arrange
            val usuario1 = UsuarioBuilder().build()
            val usuario2 = UsuarioBuilder().build()
            val LOTR = LibroBuilder().build()
            val HarryPotter = LibroBuilder().build()
            val WOR = LibroBuilder().build()

            //Act
            usuario1.apply {
                librosLeidos.add(WOR)
                librosLeidos.add(LOTR)
            }
            usuario1.crearRecomendacion("Review archivo de las tormentas", mutableSetOf(WOR), "Review", publico = true)
            usuario1.crearRecomendacion("Review LOTR", mutableSetOf(LOTR), "review", publico = false)

            it("Agrego recomendaciones publicas a la a de recomendaciones a valorar del usuario") {
                shouldNotThrow<BusinessException> { (usuario2.agregarRecomendacionAValorar(usuario1.recomendaciones[0])) }
            }

            it("No puedo Agregar recomendaciones privadas a la a de recomendaciones a valorar del usuario") {
                shouldThrow<BusinessException> { usuario2.agregarRecomendacionAValorar(usuario1.recomendaciones[1]) }
            }
        }
        describe("Cantidad de lenguajes de una recomendacion"){
            recomendacionPocoTraducida.cantidadDeLenguajes() shouldBe 2
        }
    }
})
