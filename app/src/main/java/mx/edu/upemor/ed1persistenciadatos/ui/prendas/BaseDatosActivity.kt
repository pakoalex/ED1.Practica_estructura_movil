package mx.edu.upemor.ed1persistenciadatos.ui.prendas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.tiendaApp

/** Formulario de captura/inserción y menú de operaciones (Insertar, Ver, Actualizar, Eliminar). */
class BaseDatosActivity : PrendaFormularioActivity() {

    private val repositorio by lazy { tiendaApp.prendaRepository }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_datos)
        iniciarFormulario()
    }

    fun onInsertar(v: View) {
        val prenda = obtenerPrendaValidada() ?: return
        lifecycleScope.launch {
            if (repositorio.insertar(prenda)) {
                Toast.makeText(
                    this@BaseDatosActivity,
                    getString(R.string.toast_prenda_insertada, prenda.nombre),
                    Toast.LENGTH_SHORT
                ).show()
                limpiarFormulario()
            } else {
                // El código ya existe: no se inserta.
                etCodigo.error = getString(R.string.error_codigo_existente)
                etCodigo.requestFocus()
            }
        }
    }

    fun onVer(v: View) {
        startActivity(Intent(this, VerRegistrosActivity::class.java))
    }

    fun onActualizar(v: View) {
        startActivity(Intent(this, ActualizarActivity::class.java))
    }

    fun onEliminar(v: View) {
        startActivity(Intent(this, EliminarActivity::class.java))
    }
}
