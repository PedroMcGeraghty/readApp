package ar.edu.unsam.algo2.readapp


import LibroBuilder
import ar.edu.unsam.algo2.readapp.builders.AutorBuilder
import ar.edu.unsam.algo2.readapp.builders.ParticularBuilder
import ar.edu.unsam.algo2.readapp.builders.UsuarioBuilder
import ar.edu.unsam.algo2.readapp.centrosDeLectura.CentroDeLectura
import ar.edu.unsam.algo2.readapp.features.Recomendacion
import ar.edu.unsam.algo2.readapp.features.Valoracion
import ar.edu.unsam.algo2.readapp.libro.Autor
import ar.edu.unsam.algo2.readapp.libro.Libro
import ar.edu.unsam.algo2.readapp.repositorios.Repositorio
import ar.edu.unsam.algo2.readapp.usuario.Usuario
import excepciones.BusinessException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import java.time.LocalDate


class RepositoriosSpec : DescribeSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    val repositorioLibro = Repositorio<Libro>()
    describe("Dados Repositorios ya creados(Objetos)") {
        it("Tienen la memoria vacia por default") {
            repositorioLibro.objetosEnMemoria.shouldBeEmpty()
        }

        describe("Pueden agreagar objetos") {

            //https://kotest.io/docs/framework/isolation-mode.html
            isolationMode = IsolationMode.SingleInstance//CHEQUEAR ESTO
            //isolationMode = IsolationMode.InstancePerLeaf//CHEQUEAR ESTO
            //isolationMode = IsolationMode.InstancePerTest//CHEQUEAR ESTO

            describe("Se puede agregar a la hora de instanciarlo") {

                val libroMock = LibroBuilder()
                    .cantidadPalabras(2000)
                    .build()
                repositorioLibro.create(libroMock)
                it("Al crear objeto lo almacena en una coleccion") {
                    repositorioLibro.objetosEnMemoria.size shouldBeEqual 1
                    repositorioLibro.objetosEnMemoria shouldContain libroMock
                }

                it("Les asigna un ID") {
                    libroMock.id shouldBeEqual 1
                }

                it("Busqueda por ID") {
                    repositorioLibro.getByID(1) shouldBe libroMock
                }
                describe("ELimina los objetos") {

                    repositorioLibro.objetosEnMemoria shouldContain libroMock
                    repositorioLibro.delete(libroMock)
                    repositorioLibro.objetosEnMemoria shouldNotContain libroMock

                    it("Buscar por ID algo que NO existe") {
                        shouldThrow<BusinessException> { repositorioLibro.getByID(1) }
                    }

                    it("Eliminar algo que NO existe") {
                        shouldThrow<BusinessException> { repositorioLibro.delete(libroMock) }

                    }
                }

                it("Actualizarlos algo que NO existe") {
                    //OJO la manera de resolver esto
                    repositorioLibro.delete(libroMock)
                    shouldThrow<BusinessException> { repositorioLibro.update(libroMock) }
                }


            }

            describe("Se puede agregar a partir del repositorio") {

                val libroMock = LibroBuilder()
                    .cantidadPalabras(2000)
                    .build()

                repositorioLibro.create(libroMock) shouldBeEqual true
                it("NO puedo agregar ya existentes") {
                    repositorioLibro.create(libroMock) shouldBeEqual false
                }

            }

            describe("Search Libro") {
                val autorMock = AutorBuilder().apellido("geraghty").build()
                val libroMock = LibroBuilder()
                    .titulo("El señor de los anillos")
                    .autor(autorMock)
                    .build()
                val repositorioLibroSearch = Repositorio<Libro>()

                repositorioLibroSearch.create(libroMock)
                describe("Buscar un libro por el titulo") {
                    repositorioLibroSearch.search("señor") shouldContain libroMock
                    repositorioLibroSearch.search("El señor") shouldContain libroMock
                }
                describe("Buscar por el apellido del autor") {
                    repositorioLibroSearch.search("geraghty") shouldContain libroMock
                    repositorioLibroSearch.search("gera") shouldContain libroMock
                    repositorioLibroSearch.search("perez") shouldNotContain libroMock
                }
            }

            describe("Search Usuario") {
                val usuarioMock = UsuarioBuilder()
                    .nombre("pedro")
                    .apellido("geraghty")
                    .alias("elpica66")
                    .build()
                val repositorioUsuario = Repositorio<Usuario>()
                repositorioUsuario.create(usuarioMock)
                describe("Buscar un usuario por su nombre") {
                    repositorioUsuario.search("pedro") shouldContain usuarioMock
                    repositorioUsuario.search("pe") shouldContain usuarioMock
                    repositorioUsuario.search("pedri") shouldNotContain usuarioMock
                }
                describe("Buscar un usuario por su apellido") {
                    repositorioUsuario.search("geraghty") shouldContain usuarioMock
                    repositorioUsuario.search("gera") shouldContain usuarioMock
                    repositorioUsuario.search("gerarty") shouldNotContain usuarioMock
                }
                describe("Buscar un usuario por su username") {
                    repositorioUsuario.search("elpica66") shouldContain usuarioMock
                    repositorioUsuario.search("pica") shouldContain usuarioMock
                    repositorioUsuario.search("el pica 66") shouldNotContain usuarioMock
                }
            }

            describe("Search Autor") {
                val autorMock = AutorBuilder()
                    .nombre("charles")
                    .apellido("bukowski")
                    .seudonimo("Henry Chiniasky")
                    .build()
                val repositorioAutores = Repositorio<Autor>()
                repositorioAutores.create(autorMock)
                describe("Buscar un autor por su nombre") {
                    repositorioAutores.search("charles") shouldContain autorMock
                    repositorioAutores.search("charl") shouldContain autorMock
                    repositorioAutores.search("charli") shouldNotContain autorMock
                }
                describe("Buscar un autor por su apellido") {
                    repositorioAutores.search("bukowski") shouldContain autorMock
                    repositorioAutores.search("buko") shouldContain autorMock
                    repositorioAutores.search("bukoski") shouldNotContain autorMock
                }
                describe("Buscar un autor por su sudonimo") {
                    repositorioAutores.search("Henry Chiniasky") shouldContain autorMock
                    repositorioAutores.search("Henry") shouldNotContain autorMock
                }
            }

            describe("Search Centros de Lectura") {

                val libroMock =
                    LibroBuilder().titulo("Cartero").build()//Con este builder rompe , si fuera sin bulider funciona

                val particularMock = ParticularBuilder().participantes(100)
                    .direccion("mock")
                    .libro(libroMock)
                    .fecha(mutableSetOf(LocalDate.now().plusDays(7)))
                    .duracion(3)
                    .build()
//                val particularMock = Particular.Builder()
//                    .direccion("obelisco")
//                    .libro(libroMock)
//                    .fecha(mutableSetOf(LocalDate.now().plusDays(7)))
//                    .duracionEncuentro(3)
//                    .build()
                val repositorioCentroDeLectura = Repositorio<CentroDeLectura>()
                repositorioCentroDeLectura.create(particularMock)
                describe("Buscar por nombre del libro designado") {
                    repositorioCentroDeLectura.search("Cartero") shouldContain particularMock
                    repositorioCentroDeLectura.search("Cart") shouldNotContain particularMock
                }
            }

            describe("search recomendacion") {
                val autor = UsuarioBuilder()
                    .nombre("pedro")
                    .apellido("geraghty")
                    .alias("elpica66")
                    .build()
                val usuarioX = UsuarioBuilder().build()

                val creador = UsuarioBuilder()
                    .nombre("agus")
                    .apellido("gonzalez")
                    .alias("el traidor")
                    .build()

                val algoritmo = LibroBuilder().titulo("programacion orientada a objetos").build()
                val caso = LibroBuilder().titulo("sistemas operativos").build()
                val valoracion = Valoracion(8, "buena intro", autor)

                creador.leer(algoritmo)
                creador.leer(caso)
                creador.crearRecomendacion(librosParaRecomendar = mutableSetOf(algoritmo, caso))
                val recomendacionX = creador.recomendaciones[0]
                usuarioX.leer(algoritmo)
                usuarioX.leer(caso)
                usuarioX.valorarRecomendacion(recomendacionX, 5, "buena intro")
                val repositorioRecomendaciones = Repositorio<Recomendacion>()
                repositorioRecomendaciones.create(recomendacionX)

                describe(
                    "valor de búsqueda exactamente con el apellido del creador o " +
                            "parcialmente con alguno de los títulos de los libros o reseña"
                ) {

                    it("busqueda exacta apellido creador") {
                        val busquedaEx = repositorioRecomendaciones.search("gonzalez")
                        val busquedaPa = repositorioRecomendaciones.search("gonz")

                        busquedaEx.shouldContain(recomendacionX)
                        busquedaPa.shouldNotContain(recomendacionX)
                    }
                    it("busqueda parcial titulos de libros") {
                        val busquedaPa = repositorioRecomendaciones.search("sistemas")
                        val busquedaPa2 = repositorioRecomendaciones.search("progr")

                        busquedaPa.shouldContain(recomendacionX)
                        busquedaPa2.shouldContain(recomendacionX)
                    }
                    it("busqueda parcial de resena") {
                        val busquedaPa = repositorioRecomendaciones.search("buen")

                        busquedaPa.shouldContain(recomendacionX)

                    }

                }
            }


        }

    }

})