package ar.edu.unsam.algo2.readapp

import LibroBuilder
import ar.edu.unsam.algo2.readapp.builders.AutorBuilder
import ar.edu.unsam.algo2.readapp.builders.UsuarioBuilder
import ar.edu.unsam.algo2.readapp.features.Recomendacion
import ar.edu.unsam.algo2.readapp.features.Valoracion
import ar.edu.unsam.algo2.readapp.libro.Lenguaje
import excepciones.BusinessException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class ValoracionSpec : DescribeSpec({
    //Arragne
    val autorRuso = AutorBuilder().lenguaNativa(Lenguaje.RUSO).build()
    val autorAleman = AutorBuilder().lenguaNativa(Lenguaje.ALEMAN).build()
    val creador = UsuarioBuilder()
        .nombre("Juan")
        .apellido("Morales")
        .alias("JuanMora")
        .fechaNacimiento(LocalDate.now().minusYears(25))
        .velocidadLectura(10)
        .build()

    val usuario = UsuarioBuilder()
        .nombre("Matias")
        .apellido("Rodriguez")
        .alias("MatiRodri")
        .fechaNacimiento(LocalDate.now().minusYears(24))
        .velocidadLectura(20)
        .build()

    val libro1 = LibroBuilder().autor(autorRuso).build()
    val libro2 = LibroBuilder().autor(autorRuso).build()
    val libro3 = LibroBuilder().autor(autorAleman).build()
    val libro4 = LibroBuilder().autor(autorAleman).build()


    //Act
    creador.leer(libro1)
    creador.leer(libro2)
    creador.leer(libro3)

    usuario.leer(libro1)
    usuario.leer(libro2)
    usuario.leer(libro3)
    usuario.autoresPreferidos.add(autorRuso)
    val recomendacion = Recomendacion(creador, mutableSetOf(libro1, libro2), "recomendaciones", "libros Varios", false)
    val valoracion = Valoracion(3,"Buenas recomendacion",creador)
    //Assert


    describe("dado un usuario saber si puede dejar una valoracion") {

        it("Si es creador no puede dejar una valoracion") {
            recomendacion.puedeDejarValoracion(creador) shouldBe false
        }
        it("Si no es creador y leyo los libros puede dejar una valoracion") {
            recomendacion.puedeDejarValoracion(usuario) shouldBe true
        }
        usuario.librosLeidos.clear()
        it("Si el usuario no leyo los libros no podra dejar una valoracion") {
            recomendacion.usuarioLeyoRecomendados(usuario) shouldBe false
        }

        it("Si los libros son de un unico autor y esta en la lista de preferidos del usuario puede dejar una valoracion") {
            recomendacion.condicionesAutor(usuario) shouldBe true
        }
        it("Los libros tienen un solo autor") {
            recomendacion.unicoAutor() shouldBe true
        }

        it("Si los libros tienen mas de un autor no podra dejar la valoracion") {
            recomendacion.librosRecomendados.add(libro3)
            recomendacion.unicoAutor() shouldBe false
        }
        recomendacion.librosRecomendados.clear()
        recomendacion.librosRecomendados.add(libro4)
        it("Si los libros no son del autor preferido no puede dejar valoracion") {
            recomendacion.sonDeAutorPreferido(usuario)
        }

        recomendacion.librosRecomendados.clear()
        recomendacion.librosRecomendados.add(libro1)
        recomendacion.librosRecomendados.add(libro2)
        it("Si son del autor preferido puede dejar la valoracion ") {
            recomendacion.sonDeAutorPreferido(usuario)
        }

    }
    describe("Si ya creo una valoracion sobre una recomendacion no podra realizar otra sobre la misma recomendacion") {
        usuario.valorarRecomendacion(recomendacion, 4, "Buena recomendacion")
        it("throw error") {
            shouldThrow<BusinessException> {
                usuario.valorarRecomendacion(recomendacion, 2, "Mala recomendacion")
            }
        }
        it("valoro una recomendacion") {
            usuario.recomendoValoracion(recomendacion) shouldBe true
        }
        recomendacion.valoraciones.clear()
        it("no emitio valoracion") {
            usuario.recomendoValoracion(recomendacion) shouldBe false
        }
    }

    describe("saber si puede editar una valoracion") {
        it("Si no emitio una valoracion no puede ediatarla") {
            shouldThrow<BusinessException> {
                valoracion.editarValoracion(recomendacion,2, "Mala recomendacion",usuario)
            }
        }
        it("El puntaje no puede ser menor a 0 ni mayor que 5") {
            shouldThrow<Exception> {
                valoracion.validarValoracion(6)
            }
            shouldThrow<Exception> {
                valoracion.validarValoracion(-2)
            }
        }

    }


})

