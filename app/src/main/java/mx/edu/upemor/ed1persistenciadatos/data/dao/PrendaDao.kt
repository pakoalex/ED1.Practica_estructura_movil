package mx.edu.upemor.ed1persistenciadatos.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import mx.edu.upemor.ed1persistenciadatos.data.entity.Prenda

@Dao
interface PrendaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(prenda: Prenda)

    @Update
    suspend fun actualizar(prenda: Prenda): Int

    @Delete
    suspend fun eliminar(prenda: Prenda): Int

    @Query("SELECT * FROM prendas ORDER BY nombre ASC")
    suspend fun obtenerTodas(): List<Prenda>

    @Query("SELECT * FROM prendas WHERE nombre LIKE '%' || :texto || '%' ORDER BY nombre ASC")
    suspend fun buscarPorNombre(texto: String): List<Prenda>

    @Query("SELECT * FROM prendas WHERE codigo = :codigo LIMIT 1")
    suspend fun obtenerPorCodigo(codigo: String): Prenda?

    @Query("SELECT COUNT(*) FROM prendas WHERE codigo = :codigo")
    suspend fun existeCodigo(codigo: String): Int
}
