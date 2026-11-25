package mx.edu.upemor.ed1persistenciadatos.data.repository

import mx.edu.upemor.ed1persistenciadatos.data.dao.HistorialDao
import mx.edu.upemor.ed1persistenciadatos.data.entity.HistorialCalculo

class HistorialRepository(private val dao: HistorialDao) {

    suspend fun insertar(registro: HistorialCalculo) {
        dao.insertar(registro)
    }

    suspend fun obtenerTodos(): List<HistorialCalculo> = dao.obtenerTodos()

    suspend fun borrarTodo() = dao.borrarTodo()
}
