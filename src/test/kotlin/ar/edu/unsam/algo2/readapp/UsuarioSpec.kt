package ar.edu.unsam.algo2.readapp


import LibroBuilder
import ar.edu.unsam.algo2.readapp.builders.AutorBuilder
import ar.edu.unsam.algo2.readapp.builders.UsuarioBuilder
import ar.edu.unsam.algo2.readapp.libro.Lenguaje
import ar.edu.unsam.algo2.readapp.usuario.Ansioso
import ar.edu.unsam.algo2.readapp.usuario.Fanatico
import ar.edu.unsam.algo2.readapp.usuario.Promedio
import ar.edu.unsam.algo2.readapp.usuario.Recurrente
import excepciones.RecomendacionException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import java.time.LocalDate


class UsuarioSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest
    //Arrange usuarios
    val usuario = UsuarioBuilder()
        .nombre("manuel")
        .apellido("fernandez")
        .alias("manu")
        .fechaNacimiento(LocalDate.now().minusYears(31))
        .velocidadLectura(10)
        .build()
    val usuario2 = UsuarioBuilder()
        .nombre("carla")
        .apellido("gomez")
        .alias("carlita")
        .fechaNacimiento(LocalDate.now().minusYears(25))
        .velocidadLectura(10)
        .build()
    val usuario3 = UsuarioBuilder()
        .nombre("carlos")
        .apellido("gutierrez")
        .alias("carlitos")
        .fechaNacimiento(LocalDate.now().minusYears(20))
        .velocidadLectura(10)
        .build()

    //Arrange libros
    val libroSoloLargo = LibroBuilder().cantidadPalabras(400).build()
    val libroSoloComplejo = LibroBuilder().cantidadPalabras(5000).complejo().build()


    val librocomun = LibroBuilder().cantidadPalabras(900).build()
    val libroNoDesafiante = LibroBuilder().cantidadPalabras(400).build()
    val libroDesafiante = LibroBuilder().cantidadPalabras(5000).complejo().build()


    //Act

    usuario.leer(libroSoloLargo)
    usuario.leer(libroSoloComplejo)
    usuario.leer(librocomun)

    usuario.crearRecomendacion("Recomendacion", mutableSetOf(libroSoloLargo, libroSoloComplejo), "Algo", true)

   // usuario.agregarAmigo(usuario2)
    describe("dado un usuario") {

        //Assert
        it("se verifica el calculo de la edad") {
            usuario.edad() shouldBe 31
        }
    }

    describe("dado un libro desafiante") {

        //Assert
        it("se verifica que aumento el tiempo de lectura promedio") {

            usuario.tiempoLecturaBase(libroDesafiante) shouldBe 1000.0

        }
    }

    describe("dado un libro no desafiante") {

        //Assert
        it("se verifica que mantiene el tiempo de lectura promedio") {

            usuario.tiempoLecturaBase(libroNoDesafiante) shouldBe 40.0

        }
    }

    describe("Dada una recomendacion") {
        val libroA1 = LibroBuilder().build()
        val libroB1 = LibroBuilder().build()

        val usuarioA = UsuarioBuilder()
            .nombre("A")
            .apellido("A")
            .alias("AA")
            .fechaNacimiento(LocalDate.now().minusYears(1))
            .velocidadLectura(1)
            .build()
        val usuarioB = UsuarioBuilder()
            .nombre("B")
            .apellido("B")
            .alias("BB")
            .fechaNacimiento(LocalDate.now().minusYears(1))
            .velocidadLectura(1)
            .build()
        val usuarioC = UsuarioBuilder()
            .nombre("C")
            .apellido("C")
            .alias("CC")
            .fechaNacimiento(LocalDate.now().minusYears(1))
            .velocidadLectura(1)
            .build()

        val librosRecomendadosA = mutableSetOf(libroA1)
        val librosRecomendadosB = mutableSetOf(libroB1)

        describe("Crear recomendacion") {
            describe("Debe haber leidos los libros") {
                describe("Leidos") {
                    usuarioA.leido(libroA1) shouldBe (false)
                    usuarioA.leer(libroA1)
                    usuarioA.leido(libroA1) shouldBe (true)
                    it("OK") {
                        usuarioA.recomendaciones.size shouldBe 0
                        usuarioA.crearRecomendacion(
                            titulo = "A",
                            librosParaRecomendar = librosRecomendadosA,
                            contenido = "blabla"
                        )
                        usuarioA.recomendaciones.size shouldBeGreaterThan 0
                    }
                }
                describe("NO Leidos") {
                    usuarioA.leido(libroA1) shouldBe (false)
                    it("Throw Error") {
                        shouldThrow<RecomendacionException> {
                            usuarioA.crearRecomendacion(
                                titulo = "A",
                                librosParaRecomendar = librosRecomendadosA,
                                "blabla"
                            )
                        }
                    }
                }
            }

            describe("Un usuario no puede editar por que no son amigos") {
                it("Throw Error") {
                    usuario2.leer(librocomun)
                    shouldThrow<RecomendacionException> {
                        usuario2.editarRecomendacionDe(
                            usuario2,
                            usuario.recomendaciones.first(),
                            mutableSetOf(librocomun)
                        )

                    }
                }

                describe("Accesibilidad") {
                    usuarioB.leer(libroB1)
                    usuarioB.crearRecomendacion(titulo = "B", librosParaRecomendar = librosRecomendadosB, "blabla")
                    usuarioB.recomendaciones.size shouldBeGreaterThan 0
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
                        usuarioB.recomendaciones.first().cambiarAcceso()
                        usuarioB.recomendaciones.first().publica shouldBe (false)
                        describe("solo amigos") {
                            usuarioB.agregarAmigo(usuarioC)
                            //USUARIO A NO PUEDE ACCEDER
                            usuarioB.recomendaciones.first().esAccesiblePara(usuarioA) shouldBe (false)
                            usuarioA.buscarRecomendacionesPor(usuarioB) shouldBeEqual listOf()
                            //USUARIO C PUEDE ACCEDER
                            usuarioB.recomendaciones.first().esAccesiblePara(usuarioC) shouldBe (true)
                            usuarioC.buscarRecomendacionesPor(usuarioB) shouldBeEqual listOf(usuarioB.recomendaciones.first())
                        }
                    }
                }
            }


            describe("Editar") {

            }

            describe("Tipo de lector") {

                //Usuario generico, ya que puede ir variando su manera de leer
                val usuario = UsuarioBuilder().velocidadLectura(20).build()

                //Libros genericos
                val libroLargo = LibroBuilder().largo().autor(AutorBuilder().lenguaNativa(Lenguaje.INGLES).build()).build()
                val libroNoLargo = LibroBuilder().cantidadPaginas(600).autor(AutorBuilder().lenguaNativa(Lenguaje.INGLES).build()).build()
                val libroPopular = LibroBuilder().cantidadPalabras(1002).ediciones(3).build()
                val libroNoPopular = LibroBuilder().cantidadPalabras(1003).ediciones(2).build()
                val libroBestSeller = LibroBuilder().cantidadPalabras(1004).ediciones(3).ventasSemanales(10000).build()
                val libroNoBestSeller = LibroBuilder().cantidadPalabras(1005).ediciones(2).ventasSemanales(9999).build()

                it("Puede cambiar de tipo cuando el usuario quiera") {
                    usuario.cambioTipoLector(Ansioso)
                    usuario.tipoDeLector shouldBe Ansioso
                }

                describe("PROMEDIO") {
                    it("PROMEDIO POR DEFAULT") {
                        usuario.tipoDeLector shouldBeEqual Promedio
                    }
                    describe("Dado un libro") {
                        it("Mismo tiempo, sin importar el tipo de libro") {
                            usuario.tiempoLecturaFinal(libro = libroLargo) shouldBeEqual usuario.tiempoLecturaBase(
                                libroLargo
                            )
                            usuario.tiempoLecturaFinal(libro = libroNoLargo) shouldBeEqual usuario.tiempoLecturaBase(
                                libroNoLargo
                            )
                            usuario.tiempoLecturaFinal(libro = libroPopular) shouldBeEqual usuario.tiempoLecturaBase(
                                libroPopular
                            )
                            usuario.tiempoLecturaFinal(libro = libroNoPopular) shouldBeEqual usuario.tiempoLecturaBase(
                                libroNoPopular
                            )
                            usuario.tiempoLecturaFinal(libro = libroBestSeller) shouldBeEqual usuario.tiempoLecturaBase(
                                libroBestSeller
                            )
                            usuario.tiempoLecturaFinal(libro = libroNoBestSeller) shouldBeEqual usuario.tiempoLecturaBase(
                                libroNoBestSeller
                            )
                        }
                    }
                }

                describe("ANSIOSO") {
                    it("DEBE MODIFICARSE A ANSIOSO") {
                        usuario.tipoDeLector shouldBeEqual Promedio
                        usuario.cambioTipoLector(Ansioso)
                        usuario.tipoDeLector shouldBeEqual Ansioso
                    }
                    describe("Dado libroS") {
                        usuario.cambioTipoLector(Ansioso)
                        describe("BEST SELLER") {
                            it("Reduce 50% tiempoLectura") {
                                usuario.tiempoLecturaFinal(libro = libroBestSeller) shouldBeEqual usuario.tiempoLecturaBase(
                                    libroBestSeller
                                ) * Ansioso.ESCALAR_BEST_SELLER
                            }
                        }
                        describe("Cualquiera - NO best seller") {
                            it("Reduce 20% tiempoLectura") {
                                usuario.tiempoLecturaFinal(libro = libroLargo) shouldBeEqual
                                        usuario.tiempoLecturaBase(libroLargo) - usuario.tiempoLecturaBase(libroLargo) * Ansioso.ESCALAR_DEFAULT

                                usuario.tiempoLecturaFinal(libro = libroNoLargo) shouldBeEqual
                                        usuario.tiempoLecturaBase(libroNoLargo) - usuario.tiempoLecturaBase(libroNoLargo) * Ansioso.ESCALAR_DEFAULT

                                usuario.tiempoLecturaFinal(libro = libroPopular) shouldBeEqual
                                        usuario.tiempoLecturaBase(libroPopular) - usuario.tiempoLecturaBase(libroPopular) * Ansioso.ESCALAR_DEFAULT

                                usuario.tiempoLecturaFinal(libro = libroNoPopular) shouldBeEqual
                                        usuario.tiempoLecturaBase(libroNoPopular) - usuario.tiempoLecturaBase(libroNoPopular) * Ansioso.ESCALAR_DEFAULT

                                usuario.tiempoLecturaFinal(libro = libroNoBestSeller) shouldBeEqual
                                        usuario.tiempoLecturaBase(libroNoBestSeller) - usuario.tiempoLecturaBase(libroNoBestSeller) * Ansioso.ESCALAR_DEFAULT
                            }
                        }
                    }
                }


                describe("Usuario lector fanático") {
                    val autorIngles = AutorBuilder().lenguaNativa(Lenguaje.INGLES).build() // Instanciar a los autores ya que Autor(Lenguaje.Ingles) es una refencia abstracta
                    val lotrCorto = LibroBuilder().cantidadPalabras(500).cantidadPaginas(500).autor(autorIngles).build()

                    it("DEBE MODIFICARSE A FANATICO") {
                        usuario.tipoDeLector shouldBeEqual Promedio
                        usuario.cambioTipoLector(Fanatico)
                        usuario.tipoDeLector shouldBeEqual Fanatico
                    }
                    usuario.cambioTipoLector(Fanatico)
                    it("Si leyo o NO es autor fav, devuelve velcidadLecturaBase") {
                        //val LOTR = Libro(autores = setOf(Autor.INGLES), cantidadPaginas = 500)
                        usuario.leer(lotrCorto)
                        usuario.leido(lotrCorto) shouldBe (true)
                        usuario.autoresPreferidos shouldNotContain autorIngles
                        usuario.palabrasPorMinutos shouldBe 20
                        lotrCorto.cantidadPalabras shouldBe 500
                        lotrCorto.esDesafiante() shouldBe false
                        usuario.tiempoLecturaBase(lotrCorto) shouldBe 25 //Equivale a 500/20 => cantcantidadPalabras/cantidadPalabrasMinuto
                        usuario.tiempoLecturaFinal(libro = lotrCorto) shouldBe usuario.tiempoLecturaBase(lotrCorto)
                    }
                    it("Usuario lector fanático libro corto") {
                        usuario.agregarAutorFavorito(autorIngles)
                        usuario.tiempoLecturaFinal(libro = lotrCorto) shouldBe usuario.tiempoLecturaBase(lotrCorto) + Fanatico.tiempoDeLectura(usuario, lotrCorto)
                    }
                    it("Usuario lector fanático libro largo") {
                        val lotrLargo = LibroBuilder().cantidadPalabras(605).cantidadPalabras(600).autor(autorIngles).build()
                        usuario.agregarAutorFavorito(autorIngles)
                        usuario.tiempoLecturaFinal(libro = lotrLargo) shouldBe usuario.tiempoLecturaBase(lotrLargo) + Fanatico.tiempoDeLectura(usuario, lotrLargo)
                    }
                }

                describe("RECURRENTE") {
                    val libroGenerico = LibroBuilder().cantidadPalabras(1000).build()
                    val libroGenerico2 = LibroBuilder().cantidadPalabras(1000).build()
                    it("DEBE MODIFICARSE A FANATICO") {
                        usuario.tipoDeLector shouldBeEqual Promedio
                        usuario.cambioTipoLector(Recurrente)
                        usuario.tipoDeLector shouldBeEqual Recurrente
                    }
                    usuario.cambioTipoLector(Recurrente)
                    it("SI no leyo, tiempoLectura = tiempoLecturaBase") {
                        usuario.leido(libroGenerico) shouldBe (false)
                        usuario.tiempoLecturaFinal(libro = libroGenerico) shouldBe usuario.tiempoLecturaBase(
                            libroGenerico
                        )
                    }
                    it("Cada lectura reduce 1% el tiempoLectura") {
                        usuario.leer(libroGenerico)
                        usuario.tiempoLecturaFinal(libro = libroGenerico) shouldBe usuario.tiempoLecturaBase(
                            libroGenerico
                        ) * 0.99
                        usuario.leer(libroGenerico)
                        usuario.tiempoLecturaFinal(libro = libroGenerico) shouldBe usuario.tiempoLecturaBase(
                            libroGenerico
                        ) * 0.98
                        usuario.leer(libroGenerico)
                        usuario.tiempoLecturaFinal(libro = libroGenerico) shouldBe usuario.tiempoLecturaBase(
                            libroGenerico
                        ) * 0.97
                        usuario.leer(libroGenerico)
                        usuario.tiempoLecturaFinal(libro = libroGenerico) shouldBe usuario.tiempoLecturaBase(
                            libroGenerico
                        ) * 0.96
                        usuario.leer(libroGenerico)
                        usuario.tiempoLecturaFinal(libro = libroGenerico) shouldBe usuario.tiempoLecturaBase(
                            libroGenerico
                        ) * 0.95
                        usuario.leer(libroGenerico)
                        usuario.tiempoLecturaFinal(libro = libroGenerico) shouldBe usuario.tiempoLecturaBase(
                            libroGenerico
                        ) * 0.95
                    }
                }
            }



            describe("velocidad de lectura, sin libros leidos") {
                describe("dada una recomendacion, sin libros leidos se calculara el tiempo de lectura") {

                    usuario2.leido(libroSoloLargo) shouldBe false
                    usuario2.leido(libroSoloComplejo) shouldBe false
                    usuario.crearRecomendacion(librosParaRecomendar =  mutableSetOf(libroSoloComplejo,libroSoloLargo))

                   }
                    it("no ahorra tiempo") {
                        val recomendacion = usuario.recomendaciones[0]
                        recomendacion.tiempoQueSePuedeAhorrar(usuario2) shouldBe 0.0
                       // usuario2.tiempoQueSePuedeAhorrar(usuario.recomendaciones.first()) shouldBe 0.0
                    }
                    it("tiempo neto") {
                        //el tiempo neto deberia ser igual al tiempo de lectura  de los libros recomendados
                        val recomendacion = usuario.recomendaciones[0]
                        recomendacion.tiempoNetoLectura(usuario2) shouldBe 1040.0
                    }
                }

            }

            describe("velocidad de lectura, con libros leidos") {
                describe("dada una recomendacion con libros leidos se calculara el tiempo de lectura") {
                    // se leen libros
                    usuario2.leer(libroSoloLargo)
                    usuario.crearRecomendacion(librosParaRecomendar =  mutableSetOf(libroSoloComplejo,libroSoloLargo))

                    val recomendacion = usuario.recomendaciones[1]


                    it("ahorra tiempo 40.0") {

                        usuario2.leido(libroSoloLargo) shouldBe true
                        //ahora si ahorra tiempo
                        recomendacion.tiempoQueSePuedeAhorrar(usuario2) shouldBe 40.0

                    }
                    it("tiempo neto, teniendo en cuenta el tiempo ahorrado") {
                        //se tiene en cuenta el tiempo del libro leido
                      recomendacion.tiempoNetoLectura(usuario2) shouldBe 1000.00

                    }
                }

            }
        }

})
