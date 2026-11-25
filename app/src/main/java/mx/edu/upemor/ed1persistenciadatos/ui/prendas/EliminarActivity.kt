package mx.edu.upemor.ed1persistenciadatos.ui.prendas

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.data.entity.Prenda
import mx.edu.upemor.ed1persistenciadatos.tiendaApp
import mx.edu.upemor.ed1persistenciadatos.ui.base.BaseActivity
import mx.edu.upemor.ed1persistenciadatos.util.Extras

/** Búsqueda por ID, vista previa del registro y confirmación (AlertDialog) antes de eliminar. */
class EliminarActivity : BaseActivity() {

    private val repositorio by lazy { tiendaApp.prendaRepository }

    private lateinit var etBuscarCodigo: EditText
    private lateinit var tvVistaPrevia: TextView
    private lateinit var btnEliminar: Button
    private var prendaCargada: Prenda? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eliminar)

        etBuscarCodigo = findViewById(R.id.etBuscarCodigo)
        tvVistaPrevia = findViewById(R.id.tvVistaPrevia)
        btnEliminar = findViewById(R.id.btnConfirmarEliminar)
        btnEliminar.isEnabled = false

        val codigoInicial = if (savedInstanceState == null) {
            intent.getStringExtra(Extras.CODIGO_PRENDA)
        } else {
            savedInstanceState.getString(CLAVE_CODIGO)
        }
        if (!codigoInicial.isNullOrBlank()) {
            etBuscarCodigo.setText(codigoInicial)
            buscar(codigoInicial)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CLAVE_CODIGO, prendaCargada?.codigo)
    }

    fun onBuscar(v: View) {
        val codigo = etBuscarCodigo.text.toString().trim().uppercase()
        if (codigo.isEmpty()) {
            etBuscarCodigo.error = getString(R.string.error_obligatorio)
            return
        }
        buscar(codigo)
    }

    private fun buscar(codigo: String) {
        lifecycleScope.launch {
            val prenda = repositorio.obtenerPorCodigo(codigo.trim().uppercase())
            prendaCargada = prenda
            if (prenda == null) {
                tvVistaPrevia.text = ""
                btnEliminar.isEnabled = false
                etBuscarCodigo.error = getString(R.string.toast_id_no_existe)
                Toast.makeText(this@EliminarActivity, R.string.toast_id_no_existe, Toast.LENGTH_SHORT).show()
            } else {
                tvVistaPrevia.text = getString(
                    R.string.vista_previa_prenda,
                    prenda.codigo, prenda.nombre, prenda.talla,
                    prenda.color, prenda.precio, prenda.cantidad, prenda.marca
                )
                btnEliminar.isEnabled = true
            }
        }
    }

    fun onEliminar(v: View) {
        val prenda = prendaCargada ?: return
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialogo_eliminar_titulo)
            .setMessage(getString(R.string.dialogo_eliminar_mensaje, prenda.nombre, prenda.codigo))
            .setPositiveButton(R.string.btn_eliminar) { _, _ -> eliminar(prenda) }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }

    private fun eliminar(prenda: Prenda) {
        lifecycleScope.launch {
            if (repositorio.eliminar(prenda)) {
                Toast.makeText(
                    this@EliminarActivity,
                    getString(R.string.toast_prenda_eliminada, prenda.nombre),
                    Toast.LENGTH_SHORT
                ).show()
                if (intent.hasExtra(Extras.CODIGO_PRENDA)) {
                    finish() // venía de la lista: se regresa a ella
                } else {
                    prendaCargada = null
                    etBuscarCodigo.setText("")
                    tvVistaPrevia.text = ""
                    btnEliminar.isEnabled = false
                }
            } else {
                Toast.makeText(this@EliminarActivity, R.string.toast_error_generico, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private companion object {
        const val CLAVE_CODIGO = "codigo_cargado"
    }
}
