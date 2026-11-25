package mx.edu.upemor.ed1persistenciadatos.ui.prendas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.data.entity.Prenda
import mx.edu.upemor.ed1persistenciadatos.tiendaApp
import mx.edu.upemor.ed1persistenciadatos.ui.base.BaseActivity
import mx.edu.upemor.ed1persistenciadatos.util.Extras

/** Lista de prendas con RecyclerView y buscador por nombre. */
class VerRegistrosActivity : BaseActivity() {

    private val repositorio by lazy { tiendaApp.prendaRepository }
    private val adaptador = PrendaAdapter(
        alEditar = { abrirActualizar(it) },
        alEliminar = { abrirEliminar(it) }
    )

    private lateinit var etBuscar: EditText
    private lateinit var tvVacio: TextView
    private var busqueda: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_registros)

        etBuscar = findViewById(R.id.etBuscar)
        tvVacio = findViewById(R.id.tvVacio)
        val rv = findViewById<RecyclerView>(R.id.rvPrendas)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adaptador

        etBuscar.doAfterTextChanged { cargar() }
    }

    /** Se recarga al volver de Actualizar/Eliminar para reflejar los cambios. */
    override fun onResume() {
        super.onResume()
        cargar()
    }

    private fun cargar() {
        busqueda?.cancel()
        busqueda = lifecycleScope.launch {
            val lista = repositorio.buscarPorNombre(etBuscar.text.toString())
            adaptador.submitList(lista)
            tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun abrirActualizar(prenda: Prenda) {
        startActivity(
            Intent(this, ActualizarActivity::class.java).putExtra(Extras.CODIGO_PRENDA, prenda.codigo)
        )
    }

    private fun abrirEliminar(prenda: Prenda) {
        startActivity(
            Intent(this, EliminarActivity::class.java).putExtra(Extras.CODIGO_PRENDA, prenda.codigo)
        )
    }
}
