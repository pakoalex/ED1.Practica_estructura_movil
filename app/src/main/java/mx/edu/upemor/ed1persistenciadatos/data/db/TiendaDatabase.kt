package mx.edu.upemor.ed1persistenciadatos.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mx.edu.upemor.ed1persistenciadatos.data.dao.HistorialDao
import mx.edu.upemor.ed1persistenciadatos.data.dao.PrendaDao
import mx.edu.upemor.ed1persistenciadatos.data.entity.HistorialCalculo
import mx.edu.upemor.ed1persistenciadatos.data.entity.Prenda

@Database(
    entities = [Prenda::class, HistorialCalculo::class],
    version = 1,
    exportSchema = false
)
abstract class TiendaDatabase : RoomDatabase() {

    abstract fun prendaDao(): PrendaDao
    abstract fun historialDao(): HistorialDao

    companion object {
        private const val NOMBRE_BD = "tienda.db"

        @Volatile
        private var instancia: TiendaDatabase? = null

        fun obtener(context: Context): TiendaDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    TiendaDatabase::class.java,
                    NOMBRE_BD
                ).build().also { instancia = it }
            }
    }
}
