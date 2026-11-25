package mx.edu.upemor.ed1persistenciadatos.ui.calculadora

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.tiendaApp
import mx.edu.upemor.ed1persistenciadatos.ui.base.BaseActivity

/** Historial persistente (Room) de las operaciones de ambas calculadoras. */
class HistorialActivity : BaseActivity() {

    private val repositorio by lazy { tiendaApp.historialRepository }
    private val adaptador = HistorialAdapter()
    private lateinit var tvVacio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        tvVacio = findViewById(R.id.tvHistorialVacio)
        val rv = findViewById<RecyclerView>(R.id.rvHistorial)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adaptador
    }

    override fun onResume() {
        super.onResume()
        cargar()
    }

    private fun cargar() {
        lifecycleScope.launch {
            val lista = repositorio.obtenerTodos()
            adaptador.submitList(lista)
            tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    fun onBorrarHistorial(v: View) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialogo_borrar_historial_titulo)
            .setMessage(R.string.dialogo_borrar_historial_mensaje)
            .setPositiveButton(R.string.btn_borrar_historial) { _, _ ->
                lifecycleScope.launch {
                    repositorio.borrarTodo()
                    Toast.makeText(this@HistorialActivity, R.string.toast_historial_borrado, Toast.LENGTH_SHORT).show()
                    cargar()
                }
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }
}
