package mx.edu.upemor.ed1persistenciadatos.ui.prendas

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.tiendaApp
import mx.edu.upemor.ed1persistenciadatos.util.Extras

/** Búsqueda por ID (código), carga de datos en el formulario y guardado de cambios. */
class ActualizarActivity : PrendaFormularioActivity() {

    private val repositorio by lazy { tiendaApp.prendaRepository }

    private lateinit var etBuscarCodigo: EditText
    private lateinit var btnGuardar: Button
    private var codigoCargado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar)
        iniciarFormulario()

        etBuscarCodigo = findViewById(R.id.etBuscarCodigo)
        btnGuardar = findViewById(R.id.btnGuardarCambios)
        habilitarFormulario(false)

        // 1) Si viene de la lista, el código llega como extra del Intent.
        // 2) Si se giró la pantalla, se vuelve a cargar el registro que estaba abierto.
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
        outState.putString(CLAVE_CODIGO, codigoCargado)
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
            if (prenda == null) {
                codigoCargado = null
                limpiarFormulario()
                habilitarFormulario(false)
                etBuscarCodigo.error = getString(R.string.toast_id_no_existe)
                Toast.makeText(this@ActualizarActivity, R.string.toast_id_no_existe, Toast.LENGTH_SHORT).show()
            } else {
                codigoCargado = prenda.codigo
                mostrarPrenda(prenda)
                habilitarFormulario(true)
            }
        }
    }

    fun onGuardarCambios(v: View) {
        if (codigoCargado == null) return
        val prenda = obtenerPrendaValidada() ?: return
        lifecycleScope.launch {
            if (repositorio.actualizar(prenda)) {
                Toast.makeText(
                    this@ActualizarActivity,
                    getString(R.string.toast_prenda_actualizada, prenda.nombre),
                    Toast.LENGTH_SHORT
                ).show()
                if (intent.hasExtra(Extras.CODIGO_PRENDA)) {
                    finish() // venía de la lista: se regresa a ella
                } else {
                    codigoCargado = null
                    etBuscarCodigo.setText("")
                    limpiarFormulario()
                    habilitarFormulario(false)
                }
            } else {
                Toast.makeText(this@ActualizarActivity, R.string.toast_error_generico, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun habilitarFormulario(habilitar: Boolean) {
        listOf(etNombre, etColor, etPrecio, etCantidad, etMarca).forEach { it.isEnabled = habilitar }
        spTalla.isEnabled = habilitar
        btnGuardar.isEnabled = habilitar
        etCodigo.isEnabled = false // el código es la llave primaria: no se modifica
    }

    private companion object {
        const val CLAVE_CODIGO = "codigo_cargado"
    }
}
