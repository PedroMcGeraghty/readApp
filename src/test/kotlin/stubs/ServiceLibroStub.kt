package stubs

import ServiceLibros

class ServiceLibroStub : ServiceLibros {

    var json = """
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
         }
        ]
    """.trimIndent()

    override fun getLibros(): String {
        return json
    }
}