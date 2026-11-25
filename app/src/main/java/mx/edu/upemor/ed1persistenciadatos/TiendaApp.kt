package mx.edu.upemor.ed1persistenciadatos

import android.app.Activity
import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import mx.edu.upemor.ed1persistenciadatos.data.db.TiendaDatabase
import mx.edu.upemor.ed1persistenciadatos.data.repository.HistorialRepository
import mx.edu.upemor.ed1persistenciadatos.data.repository.PrendaRepository

/** Application: crea una sola vez la base de datos y los repositorios. */
class TiendaApp : Application() {

    /** Alcance que sobrevive a las Activities (útil para guardar el historial al girar la pantalla). */
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val database by lazy { TiendaDatabase.obtener(this) }
    val prendaRepository by lazy { PrendaRepository(database.prendaDao()) }
    val historialRepository by lazy { HistorialRepository(database.historialDao()) }
}

/** Acceso corto a la Application desde cualquier Activity. */
val Activity.tiendaApp: TiendaApp
    get() = application as TiendaApp
