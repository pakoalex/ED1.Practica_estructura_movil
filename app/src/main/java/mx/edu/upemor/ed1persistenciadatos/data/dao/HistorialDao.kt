package mx.edu.upemor.ed1persistenciadatos.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import mx.edu.upemor.ed1persistenciadatos.data.entity.HistorialCalculo

@Dao
interface HistorialDao {

    @Insert
    suspend fun insertar(registro: HistorialCalculo): Long

    @Query("SELECT * FROM historial ORDER BY fecha DESC, id DESC")
    suspend fun obtenerTodos(): List<HistorialCalculo>

    @Query("DELETE FROM historial")
    suspend fun borrarTodo()
}
