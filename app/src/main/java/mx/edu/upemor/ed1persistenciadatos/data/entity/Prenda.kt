package mx.edu.upemor.ed1persistenciadatos.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Prenda de la tienda de ropa. El código es la llave primaria, por lo tanto es único. */
@Entity(tableName = "prendas")
data class Prenda(
    @PrimaryKey val codigo: String,
    val nombre: String,
    val talla: String,      // XS, S, M, L, XL
    val color: String,
    val precio: Double,
    val cantidad: Int,
    val marca: String
)
