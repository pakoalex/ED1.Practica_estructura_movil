package mx.edu.upemor.ed1persistenciadatos.data.repository

import android.database.sqlite.SQLiteConstraintException
import mx.edu.upemor.ed1persistenciadatos.data.dao.PrendaDao
import mx.edu.upemor.ed1persistenciadatos.data.entity.Prenda

/** Única puerta de acceso a los datos de prendas: las Activities no conocen SQL ni Room. */
class PrendaRepository(private val dao: PrendaDao) {

    /** @return false si el código ya existe (no se inserta). */
    suspend fun insertar(prenda: Prenda): Boolean =
        try {
            if (dao.existeCodigo(prenda.codigo) > 0) {
                false
            } else {
                dao.insertar(prenda)
                true
            }
        } catch (e: SQLiteConstraintException) {
            false
        }

    suspend fun actualizar(prenda: Prenda): Boolean = dao.actualizar(prenda) > 0

    suspend fun eliminar(prenda: Prenda): Boolean = dao.eliminar(prenda) > 0

    suspend fun obtenerPorCodigo(codigo: String): Prenda? = dao.obtenerPorCodigo(codigo)

    suspend fun buscarPorNombre(texto: String): List<Prenda> =
        if (texto.isBlank()) dao.obtenerTodas() else dao.buscarPorNombre(texto.trim())
}
