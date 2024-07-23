package ar.edu.unsam.algo2.readapp

import LibroBuilder
import ar.edu.unsam.algo2.readapp.builders.*
import ar.edu.unsam.algo2.readapp.libro.Lenguaje
import excepciones.BusinessException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class CentroDeLecturaSpec : DescribeSpec(
    {
        isolationMode = IsolationMode.InstancePerTest
        val usuarioA = UsuarioBuilder().build()
        val usuarioB = UsuarioBuilder().build()
        val usuarioC = UsuarioBuilder().build()
        val usuarioD = UsuarioBuilder().build()
        val usuarioE = UsuarioBuilder().build()

        val autor = AutorBuilder().build()

        val traduccionesParaUsar = mutableSetOf(
            Lenguaje.RUSO,
            Lenguaje.ESPANIOL,
            Lenguaje.ALEMAN,
            Lenguaje.ITALIANO,
            Lenguaje.ARABE,
            Lenguaje.MANDARIN
        )
        val libroMuyTraducido = LibroBuilder().traducciones(traduccionesParaUsar).build()

        val libroPocoTraducido = LibroBuilder().build()


        //Ojo con el orden del builder
        val centroDeLecturaParticular = ParticularBuilder().participantes(2)
            .direccion("mock")
            .libro(libroMuyTraducido)
            .fecha(mutableSetOf())
            .duracion(8)
            .build()


        val centroDeLecturaEditorial = EditorialBuilder()
            .montoAlcanzable(4000.0)
            .direccion("la plata")
            .libro(libroPocoTraducido)
            .fecha(mutableSetOf())
            .duracion(8)
            .build()

        val centroDeLecturaEditorial2 = EditorialBuilder()
            .montoAlcanzable(4000.0)
            .direccion("la plata")
            .libro(libroPocoTraducido)
            .fecha(mutableSetOf())
            .duracion(8)
            .build()


        val gastos = doubleArrayOf(10000.00, 5000.00, 15000.00)
        val centroDeLecturaBiblioteca = BibliotecaBuilder().metros(2.0).gastos(gastos)
            .direccion("Nunez")
            .libro(libroPocoTraducido)
            .fecha(mutableSetOf())
            .duracion(1)
            .build()


        describe("Centro de Lectura particular expiró su publicación") {
            it("Expiró debido a cantidad de participantes") {
                centroDeLecturaParticular.apply {
                    listaParticipantes.add(usuarioA)
                    listaParticipantes.add(usuarioB)
                    fechasDeLectura.add(LocalDate.now().plusDays(20))
                    fechasDeLectura.add(LocalDate.now().plusDays(30))
                }
                centroDeLecturaParticular.estaExpirado() shouldBe true
            }
        }

        describe("Centro de Lectura editorial expiró su publicación") {
            it("Expiró debido a cantidad de participantes, autor presente libro no es best seller") {
                centroDeLecturaEditorial.apply {
                    listaParticipantes.add(usuarioA)
                    listaParticipantes.add(usuarioB)
                    fechasDeLectura.add(LocalDate.now().minusDays(20))
                    fechasDeLectura.add(LocalDate.now().minusDays(30))

                }

                centroDeLecturaEditorial.costoDeReserva() shouldBe 2600
                centroDeLecturaEditorial.estaExpirado() shouldBe true
            }
            it("no podran asistir mas personas se cumplieron recaudacion") {
                centroDeLecturaEditorial.apply {
                    listaParticipantes.add(usuarioA)
                    listaParticipantes.add(usuarioB)
                    listaParticipantes.add(usuarioC)
                    listaParticipantes.add(usuarioD)
                    fechasDeLectura.add(LocalDate.now().plusDays(20))
                    fechasDeLectura.add(LocalDate.now().plusDays(30))
                }
                centroDeLecturaEditorial.estaExpirado() shouldBe true
                shouldThrow<BusinessException> { centroDeLecturaEditorial.reservar(usuarioE) }

            }

            it("el autor ahora esta presente con libro no best seller") {
                centroDeLecturaEditorial.apply {
                    listaParticipantes.add(usuarioA)
                    listaParticipantes.add(usuarioB)

                    fechasDeLectura.add(LocalDate.now().plusDays(20))
                    fechasDeLectura.add(LocalDate.now().plusDays(30))
                    centroDeLecturaEditorial.autorAsiste()

                    centroDeLecturaEditorial.costoDeReserva() shouldBe 2800

                }

            }
            describe("Centro de Lectura Biblioteca expiró su publicación")
            {

                it("Metros cuadrados ocupados") {
                    centroDeLecturaBiblioteca.apply {
                        listaParticipantes.add(usuarioA)
                        listaParticipantes.add(usuarioB)
                        fechasDeLectura.add(LocalDate.now().plusDays(30))
                    }
                    centroDeLecturaBiblioteca.estaExpirado() shouldBe true
                }

                it("Centro de Lectura bilbioteca no expira") {
                    centroDeLecturaBiblioteca.apply {
                        listaParticipantes.add(usuarioA)
                        fechasDeLectura.add(LocalDate.now().plusDays(30))
                    }
                    centroDeLecturaBiblioteca.estaExpirado() shouldBe false
                }

            }

            describe("Centros de lectura fechas vencidas expiran") {
                centroDeLecturaEditorial.apply { fechasDeLectura.add(LocalDate.now().minusDays(30)) }
                centroDeLecturaParticular.apply { fechasDeLectura.add(LocalDate.now().minusDays(30)) }
                centroDeLecturaBiblioteca.apply { fechasDeLectura.add(LocalDate.now().minusDays(30)) }

                centroDeLecturaEditorial.estaExpirado() shouldBe true
                centroDeLecturaParticular.estaExpirado() shouldBe true
                centroDeLecturaBiblioteca.estaExpirado() shouldBe true
            }

            describe("biblioteca costo reserva") {
                centroDeLecturaBiblioteca.apply {
                    listaParticipantes.add(usuarioA)
                    listaParticipantes.add(usuarioB)
                    listaParticipantes.add(usuarioC)
                    listaParticipantes.add(usuarioD)

                    fechasDeLectura.add(LocalDate.now().plusDays(20))
                    fechasDeLectura.add(LocalDate.now().plusDays(21))
                    fechasDeLectura.add(LocalDate.now().plusDays(22))
                    fechasDeLectura.add(LocalDate.now().plusDays(23))
                }

                it("costo reserva para 4 asistentes y 4 fechas") {
                    // = (30000 + (30000*0.4))/4 + 1000(divulgacion app)
                    centroDeLecturaBiblioteca.fechasDeLectura.size shouldBe 4
                    centroDeLecturaBiblioteca.costoDeReserva() shouldBe 11500.00
                }

                centroDeLecturaBiblioteca.apply {

                    fechasDeLectura.add(LocalDate.now().plusDays(24))
                    fechasDeLectura.add(LocalDate.now().plusDays(25))
                    fechasDeLectura.add(LocalDate.now().plusDays(26))
                    fechasDeLectura.add(LocalDate.now().plusDays(27))
                    fechasDeLectura.add(LocalDate.now().plusDays(28))
                    fechasDeLectura.add(LocalDate.now().plusDays(29))

                }
                it("costo reserva para 4 asistentes y 10 fechas") {
                    // = (30000 + (30000*0.5))/4 + 1000(divulgacion app)
                    centroDeLecturaBiblioteca.fechasDeLectura.size shouldBe 10
                    centroDeLecturaBiblioteca.costoDeReserva() shouldBe 12250.00
                }
            }
        }
    })