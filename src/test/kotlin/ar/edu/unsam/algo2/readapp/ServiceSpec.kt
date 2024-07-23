package ar.edu.unsam.algo2.readapp

import ActualizadorLibro
import LibroBuilder
import ar.edu.unsam.algo2.readapp.libro.Libro
import ar.edu.unsam.algo2.readapp.repositorios.Repositorio
import excepciones.BusinessException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import stubs.ServiceLibroStub

class ServiceSpec : DescribeSpec({

    isolationMode = IsolationMode.SingleInstance


    val servicio =
        ServiceLibroStub()
    val libro1 = LibroBuilder()
        .ediciones(6)
        .ventasSemanales(15000)
        .build()

    val libro2 = LibroBuilder()
        .ediciones(1)
        .ventasSemanales(100)
        .build()

    describe("Objeto actualizador puede interpretar un json") {
        val RepositorioLibros = Repositorio<Libro>()
        RepositorioLibros.create(libro1)
        RepositorioLibros.create(libro2)
        ActualizadorLibro.actualizar(servicio, RepositorioLibros)

        it("Se actuailiza el libro 1") {
            RepositorioLibros.getByID(libro1.id).ediciones shouldBe 10
            RepositorioLibros.getByID(libro1.id).ventasSemanales shouldBe 100
        }

        it("Se actuailiza el libro 2") {
            RepositorioLibros.getByID(libro2.id).ediciones shouldBe 25
            RepositorioLibros.getByID(libro2.id).ventasSemanales shouldBe 0
        }

        it("No se actualiza el libro 5 porque no existe") {
            servicio.json = """
        [
         {
           "id": 1,
           "ediciones": 10,
           "ventasSemanales": 100
         },
         {
           "id": 2,
           "ediciones": 25,
           "ventasSemanales": 0
         },
         {
           "id": 5,
           "ediciones": 25,
           "ventasSemanales": 0
         }
        ]
    """.trimIndent()
            shouldThrow<BusinessException> { ActualizadorLibro.actualizar(servicio, RepositorioLibros) }
        }

        it("No se actualiza el libro porque las ediciones son menores que cero") {
            servicio.json = """
        [
         {
           "id": 1,
           "ediciones": 0,
           "ventasSemanales": 10
         },
         {
           "id": 2,
           "ediciones": 25,
           "ventasSemanales": 0
         }
        ]
    """.trimIndent()
            shouldThrow<BusinessException> { ActualizadorLibro.actualizar(servicio, RepositorioLibros) }
        }

        it("No se actualiza el libro porque las ventasSemanalaes son menores que cero o 0") {
            servicio.json = """
        [
         {
           "id": 1,
           "ediciones": 100,
           "ventasSemanales": -1
         },
         {
           "id": 2,
           "ediciones": 25,
           "ventasSemanales": 0
         }
        ]
    """.trimIndent()
            shouldThrow<BusinessException> { ActualizadorLibro.actualizar(servicio, RepositorioLibros) }
        }
    }
})