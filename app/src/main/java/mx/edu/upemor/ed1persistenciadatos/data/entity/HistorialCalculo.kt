package mx.edu.upemor.ed1persistenciadatos.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Operación realizada en alguna de las calculadoras (historial persistente). */
@Entity(tableName = "historial")
data class HistorialCalculo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val expresion: String,
    val resultado: String,
    val tipo: String,       // TIPO_NORMAL o TIPO_CIENTIFICA
    val fecha: Long         // milisegundos desde epoch
) {
    companion object {
        const val TIPO_NORMAL = "NORMAL"
        const val TIPO_CIENTIFICA = "CIENTIFICA"
    }
}
