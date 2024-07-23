package ar.edu.unsam.algo2.readapp

import LibroBuilder
import ar.edu.unsam.algo2.readapp.builders.AutorBuilder
import ar.edu.unsam.algo2.readapp.builders.UsuarioBuilder
import ar.edu.unsam.algo2.readapp.features.Recomendacion
import ar.edu.unsam.algo2.readapp.libro.Lenguaje
import ar.edu.unsam.algo2.readapp.usuario.*
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import java.time.LocalDate


class PerfilDeUsuarioSpec : DescribeSpec({
        isolationMode = IsolationMode.InstancePerTest

        //Arrange
        val usuarioA = UsuarioBuilder().velocidadLectura(20).lenguaje(Lenguaje.RUSO).build()
        val usuarioB = UsuarioBuilder().velocidadLectura(20).lenguaje(Lenguaje.RUSO).build()
        val usuarioC = UsuarioBuilder().velocidadLectura(20).build()


        val autorJapones = AutorBuilder().lenguaNativa(Lenguaje.JAPONES).build()
        val autorArabe = AutorBuilder().lenguaNativa(Lenguaje.ARABE).build()
        val autorRuso = AutorBuilder().lenguaNativa(Lenguaje.RUSO).build()

        val traducciones = mutableSetOf(
            Lenguaje.RUSO,
            Lenguaje.ALEMAN,
            Lenguaje.HINDI,
            Lenguaje.ESPANIOL,
            Lenguaje.ITALIANO,
            Lenguaje.FRANCES
        )
        val libroMuyTraducido = LibroBuilder().autor(autorJapones).traducciones(traducciones).build()

        val libroPocoTraducido = LibroBuilder().autor(autorArabe).traducciones(mutableSetOf(Lenguaje.RUSO, Lenguaje.ARABE)).build()
        val SOIAF = LibroBuilder().cantidadPalabras(15000).cantidadPaginas(400).build()


        usuarioA.apply {
            librosLeidos.add(libroMuyTraducido)
            librosLeidos.add(libroPocoTraducido)
            crearRecomendacion(
                titulo = "Recomendacion muy traducidad",
                librosParaRecomendar = mutableSetOf(libroMuyTraducido)
            )
            crearRecomendacion(
                titulo = "Recomendacion poco traducidad",
                librosParaRecomendar = mutableSetOf(libroPocoTraducido)
            )
            crearRecomendacion(
                titulo = "Recomendacion muy traducidad y poco traducido",
                librosParaRecomendar = mutableSetOf(libroMuyTraducido, libroPocoTraducido)
            )
        }
        val recomendacionMuyTraducida = usuarioA.recomendaciones[0]
        val recomendacionPocoTraducida = usuarioA.recomendaciones[1]
        val recomendacionMuyTraducidaCompuesta = usuarioA.recomendaciones[2]

        describe("Test Usuario precavido") {
            //Act
            usuarioA.apply {
                agregarAmigo(usuarioB)
                agregarAmigo(usuarioC)
                librosLeidos.add(libroMuyTraducido)
                librosLeidos.add(libroPocoTraducido)
                librosLeidos.add(SOIAF)
                crearRecomendacion("El señor de los anillos", mutableSetOf(libroMuyTraducido), "", true)
                crearRecomendacion(librosParaRecomendar = mutableSetOf(libroPocoTraducido, SOIAF), titulo = "Fantasia")

            }

            usuarioB.apply {
                agregarLibroALeer(libroPocoTraducido)
                perfil = Precavido
            }

            it("Interesa la recomendación con el libro pendiente a leer") {
                usuarioB.buscarRecomendacionesInteresantes(usuarioA).shouldContainAll(usuarioA.recomendaciones[1])
                usuarioB.buscarRecomendacionesInteresantes(usuarioA).shouldNotContain(usuarioA.recomendaciones[0])
            }

            //Act
            usuarioB.apply {
                librosLeidos.add(libroMuyTraducido)
            }

            usuarioC.apply {
                agregarAmigo(usuarioB)
                perfil = Precavido
            }
            it("Interesa la recomendación ya que mis amigos lo leyeron") {
                usuarioC.buscarRecomendacionesInteresantes(usuarioA).shouldContainAll(usuarioA.recomendaciones[0])
                usuarioC.buscarRecomendacionesInteresantes(usuarioA).shouldNotContain(usuarioA.recomendaciones[1])
            }
        }

        describe("Perfil leedor") {
            //Act
            usuarioA.apply {
                librosLeidos.add(libroMuyTraducido)
                librosLeidos.add(libroPocoTraducido)
                crearRecomendacion(librosParaRecomendar = mutableSetOf(libroMuyTraducido), publico = true)
                crearRecomendacion(librosParaRecomendar = mutableSetOf(libroPocoTraducido), publico = true)
            }

            usuarioB.perfil = Leedor

            it("Interesan todas las recomendaciones") {
                usuarioB.buscarRecomendacionesInteresantes(usuarioA)
                    .shouldContainAll(usuarioA.recomendaciones[0], usuarioA.recomendaciones[1])
            }
        }

        describe("Perfil poliglota")
        {
            //act
          /*  usuarioA.apply {
                librosLeidos.add(libroMuyTraducido)
                librosLeidos.add(libroPocoTraducido)
                crearRecomendacion(
                    titulo = "Recomendacion muy traducidad",
                    librosParaRecomendar = mutableSetOf(libroMuyTraducido)
                )
                crearRecomendacion(
                    titulo = "Recomendacion poco traducidad",
                    librosParaRecomendar = mutableSetOf(libroPocoTraducido)
                )
                crearRecomendacion(
                    titulo = "Recomendacion muy traducidad y poco traducido",
                    librosParaRecomendar = mutableSetOf(libroMuyTraducido, libroPocoTraducido)
                )
            }*/
            /*val recomendacionMuyTraducida = usuarioA.recomendaciones[0]
            val recomendacionPocoTraducida = usuarioA.recomendaciones[1]
            val recomendacionMuyTraducidaCompuesta = usuarioA.recomendaciones[2]*/

            usuarioB.cambioPerfil(Poliglota)

            it("Interesa recomendaciónes muy traducidas") {
                usuarioB.buscarRecomendacionesInteresantes(usuarioA)
                    .shouldContainAll(recomendacionMuyTraducida, recomendacionMuyTraducidaCompuesta)
            }

            it("No interesa la recomendación poco traducida") {
                usuarioB.buscarRecomendacionesInteresantes(usuarioA).shouldNotContain(recomendacionPocoTraducida)
            }

            it("cantidad de lenguajes de la recomendacion muy traducidad"){
                recomendacionMuyTraducida.cantidadDeLenguajes() shouldBe 7
            }

            it("Cantidad de lenguajes de la recomendacion poco traducida"){
                recomendacionPocoTraducida.cantidadDeLenguajes() shouldBe 2
            }
        }

        describe("Perfil Nativista") {
            val usuarioRuso = UsuarioBuilder().lenguaje(Lenguaje.RUSO).velocidadLectura(20).build()
            val autorRuso = AutorBuilder().lenguaNativa(Lenguaje.RUSO).build()
            val libroRuso = LibroBuilder().autor(autorRuso).cantidadPalabras(10000).cantidadPaginas(600).build()

            usuarioRuso.cambioPerfil(Nativista)
            usuarioA.apply {
                librosLeidos.add(libroRuso)
                librosLeidos.add(SOIAF)
                crearRecomendacion(librosParaRecomendar = mutableSetOf(libroRuso))
                crearRecomendacion(librosParaRecomendar = mutableSetOf(SOIAF))
            }

            val recomendacionRusa = usuarioA.recomendaciones[3]
            val recomendacionEspaniol = usuarioA.recomendaciones[4]

            it("Interesa la recomendacion del libro en ruso") {
                usuarioRuso.buscarRecomendacionesInteresantes(usuarioA).shouldContain(recomendacionRusa)
            }

            it("No interesa la recomendacion del libro espaniol") {
                usuarioRuso.buscarRecomendacionesInteresantes(usuarioA).shouldNotContain(recomendacionEspaniol)
            }
        }

        describe("Perfil Calculador") {
            val libro1 = LibroBuilder().cantidadPalabras(9000).cantidadPaginas(400).build()
            val libro2 = LibroBuilder().cantidadPalabras(10000).cantidadPaginas(600).build()
            val recomendacion = Recomendacion(
                usuarioC,
                mutableSetOf(libro1, libro2), "Algo", "Probando perfil calculador", true
            )
            //usuarioA.tiempodeLectura(recomendacion) shouldBe 1266.0
            recomendacion.tiempodeLectura(usuarioA) shouldBe 950.0

            usuarioA.perfil = Calculador(900.00 ,1500.00)
            it("El usuario A deberia poder ver la recomendacion del usuarioC ya que esta dentro del rango") {
                usuarioA.perfil.condicion(recomendacion, usuarioA) shouldBe true
            }
            usuarioB.perfil = Calculador(800.00,900.00)
            it("El usuario B no deberia poder ver la recomendacion del usuarioC ya que esta fuera del rango") {
                usuarioB.perfil.condicion(recomendacion, usuarioB) shouldBe false

            }
        }
        describe("Demandante"){
            val usuarioA = UsuarioBuilder().build() //Creador de recomendacion
            val usuarioB = UsuarioBuilder().build() //Valorador
            val usuarioC = UsuarioBuilder().build() //Valorador
            val usuarioD = UsuarioBuilder().build() //Valorador
            val usuarioE = UsuarioBuilder().build() // Buscador

            val libroX  = LibroBuilder().build()
            val libroY  = LibroBuilder().build()
            val libroZ  = LibroBuilder().build()

            usuarioA.leer(libroX)
            usuarioA.leer(libroY)
            usuarioA.leer(libroZ)

            usuarioA.crearRecomendacion("Prueba1", mutableSetOf(libroX))
            val recomendacion1UsuarioA = usuarioA.recomendaciones.last()

            usuarioA.crearRecomendacion("Prueba2", mutableSetOf(libroY))
            val recomendacion2UsuarioA = usuarioA.recomendaciones.last()

            usuarioA.crearRecomendacion("Prueba3", mutableSetOf(libroZ))
            val recomendacion3UsuarioA = usuarioA.recomendaciones.last()

            recomendacion1UsuarioA.librosRecomendados shouldBe mutableSetOf(libroX)
            recomendacion2UsuarioA.librosRecomendados shouldBe mutableSetOf(libroY)
            recomendacion3UsuarioA.librosRecomendados shouldBe mutableSetOf(libroZ)

            usuarioB.leer(libroX)
            usuarioC.leer(libroY)
            usuarioD.leer(libroZ)

            usuarioB.valorarRecomendacion(recomendacion1UsuarioA, 5, "OK")
            usuarioC.valorarRecomendacion(recomendacion2UsuarioA, 4, "OK")
            usuarioD.valorarRecomendacion(recomendacion3UsuarioA, 3, "OK")
            describe("Recomendaciones con valoraciones con valor entre 4 y 5"){
                usuarioE.cambioPerfil(Demandante)
                usuarioE.perfil shouldBe Demandante
                usuarioE.buscarRecomendacionesInteresantes(usuario=usuarioA) shouldBe mutableSetOf(recomendacion1UsuarioA, recomendacion2UsuarioA)
            }
        }

        describe("perfil experimentado") {
                // creo usuarios
                val usuario1 = UsuarioBuilder().build()
                val usuario2 = UsuarioBuilder().build()

            // seteo perfil
                usuario2.cambioPerfil(Experimentado)

                //creo libros con autores consagrados
                val autor1 = AutorBuilder().edad(51).build()
                autor1.ganarPremio()
                val autor2 = AutorBuilder().edad(33).build()
                val autor3 = AutorBuilder().edad(51).build()
                autor3.ganarPremio()
                val autor4 = AutorBuilder().edad(51).build()
                autor4.ganarPremio()

                val libro1 = LibroBuilder().autor(autor1).build()
                val libro2 = LibroBuilder().autor(autor2).build()
                val libro3 = LibroBuilder().autor(autor3).build()
                val libro4 = LibroBuilder().autor(autor4).build()

                // se leen los libros
                usuario1.leer(libro1)
                usuario1.leer(libro2)
                usuario1.leer(libro3)
                usuario1.leer(libro4)


                it("se buscara segun su perfil, mayoria de autores consagrados") {
                    // se crea recomendacion con autores consagrados en su mayoria
                    usuario1.crearRecomendacion(librosParaRecomendar = mutableSetOf(libro1, libro2, libro3, libro4))
                    val recomendacion = usuario1.recomendaciones[0]
                    usuario2.buscarRecomendacionesInteresantes(usuario1).shouldContain(recomendacion)
                }
                it("se buscara segun su perfil, mayoria no consagrados") {
                    // se crea recomendacion con autores no consagrados en su mayoria
                    usuario1.crearRecomendacion(librosParaRecomendar = mutableSetOf(libro2))
                    val recomendacion = usuario1.recomendaciones[0]
                    usuario2.buscarRecomendacionesInteresantes(usuario1).shouldNotContain(recomendacion)
                }
        }

        describe("perfil cambiantes") {

            describe("se crea el usuario") {
                val usuario = UsuarioBuilder()
                    .velocidadLectura(20)
                    .nombre("manuel")
                    .apellido("fernandez")
                    .alias("manu")
                    .fechaNacimiento(LocalDate.now().minusYears(30))
                    .build()
                val usuario2 = UsuarioBuilder()
                    .velocidadLectura(20)
                    .nombre("carla")
                    .apellido("gomez")
                    .alias("carlita")
                    .fechaNacimiento(LocalDate.now().minusYears(20))
                    .build()

                val usuarioA = UsuarioBuilder()
                    .velocidadLectura(20)
                    .nombre("carla")
                    .apellido("gomez")
                    .alias("carlita")
                    .fechaNacimiento(LocalDate.now().minusYears(20))
                    .build()
                val libro1 = LibroBuilder().cantidadPalabras(95000).cantidadPaginas(400).build()
                val libro2 = LibroBuilder().cantidadPalabras(140000).cantidadPaginas(600).build()

                usuarioA.apply {
                        librosLeidos.add(libroMuyTraducido)
                        librosLeidos.add(libroPocoTraducido)
                        crearRecomendacion(librosParaRecomendar = mutableSetOf(libroMuyTraducido), publico = true)
                        crearRecomendacion(librosParaRecomendar = mutableSetOf(libroPocoTraducido), publico = true)
                }

                //usuario 2 crea una nueva recomendacion para la parte de cambiante
                usuario2.leer(libro1)
                usuario2.leer(libro2)
                usuario2.crearRecomendacion(librosParaRecomendar = mutableSetOf(libro1, libro2), publico = true)
                val recomendacion2 = usuario2.recomendaciones[0]
                usuario.cambioPerfil(Cambiante) // es > 25
                usuario2.cambioPerfil(Cambiante) // es < 25

                it("busca recomendaciones como perfil leedor") {
                        usuario2.buscarRecomendacionesInteresantes(usuarioA)
                            .shouldContainAll(usuarioA.recomendaciones[0], usuarioA.recomendaciones[1])
                }


                // verificar, pincha tiempo de lectura
                it("El usuario debería poder ver la recomendación ya que está dentro del rango") {
                        //modifico el rango al establecido en el ejercicio, verifico qe la recomendcion cumpla
                       // usuario.tiempodeLectura(recomendacion2) shouldBe 11750.0
                        recomendacion2.tiempodeLectura(usuario) shouldBe 11750.0
                        usuario.buscarRecomendacionesInteresantes(usuario2).shouldContain(recomendacion2)
                }
            }
        }
        describe("Perfil Combinador"){

            usuarioA.perfil = Combinador(mutableSetOf(Leedor, Poliglota))

            it("El usuario A cambiador deberia poder interesarle la recomendacion Muy traducida"){
                usuarioA.perfil.condicion(recomendacionMuyTraducida,usuarioA) shouldBe true
            }

            usuarioA.cambioPerfil(Combinador(mutableSetOf(Nativista, Leedor, Poliglota)))
            it("El usuario B cambiador deberia poder interesarle la recomendacion Muy traducida"){
                usuarioB.perfil.condicion(recomendacionMuyTraducida,usuarioB) shouldBe true
            }
            (usuarioA.perfil as Combinador).agregarPerfil(Demandante)
            it("Se agrego un perfil al perfil Combinador"){
                (usuarioA.perfil as Combinador).perfiles shouldContain (Demandante)
            }
            (usuarioA.perfil as Combinador).eliminarPerfil(Demandante)
            it("Se elimino un perfil al perfil Combinador"){
                (usuarioA.perfil as Combinador).perfiles shouldNotContain (Demandante)
            }


        }

})