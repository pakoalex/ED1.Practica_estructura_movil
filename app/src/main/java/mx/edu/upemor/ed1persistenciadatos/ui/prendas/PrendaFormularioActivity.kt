package mx.edu.upemor.ed1persistenciadatos.ui.prendas

import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.data.entity.Prenda
import mx.edu.upemor.ed1persistenciadatos.ui.base.BaseActivity
import mx.edu.upemor.ed1persistenciadatos.util.Validador
import mx.edu.upemor.ed1persistenciadatos.util.Validador.Campo
import java.util.Locale

/**
 * Comportamiento común del formulario de prendas (layout include_formulario_prenda):
 * lo comparten BaseDatosActivity (insertar) y ActualizarActivity (modificar), así las
 * validaciones y los mensajes setError son exactamente los mismos.
 */
abstract class PrendaFormularioActivity : BaseActivity() {

    protected lateinit var etCodigo: EditText
    protected lateinit var etNombre: EditText
    protected lateinit var spTalla: Spinner
    protected lateinit var etColor: EditText
    protected lateinit var etPrecio: EditText
    protected lateinit var etCantidad: EditText
    protected lateinit var etMarca: EditText

    /** Llamar después de setContentView(). */
    protected fun iniciarFormulario() {
        etCodigo = findViewById(R.id.etCodigo)
        etNombre = findViewById(R.id.etNombre)
        spTalla = findViewById(R.id.spTalla)
        etColor = findViewById(R.id.etColor)
        etPrecio = findViewById(R.id.etPrecio)
        etCantidad = findViewById(R.id.etCantidad)
        etMarca = findViewById(R.id.etMarca)

        val adaptador = ArrayAdapter.createFromResource(
            this, R.array.tallas, android.R.layout.simple_spinner_item
        )
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTalla.adapter = adaptador
    }

    /** Lee y valida el formulario. Si hay errores los muestra con setError y devuelve null. */
    protected fun obtenerPrendaValidada(): Prenda? {
        limpiarErrores()

        val codigo = etCodigo.text.toString().trim().uppercase()
        val nombre = etNombre.text.toString().trim()
        val color = etColor.text.toString().trim()
        val precio = etPrecio.text.toString()
        val cantidad = etCantidad.text.toString()
        val marca = etMarca.text.toString().trim()

        val errores = Validador.validarPrenda(
            codigo, nombre, spTalla.selectedItemPosition, color, precio, cantidad, marca
        )
        if (errores.isNotEmpty()) {
            mostrarErrores(errores)
            return null
        }

        return Prenda(
            codigo = codigo,
            nombre = nombre,
            talla = spTalla.selectedItem.toString(),
            color = color,
            precio = Validador.parsePrecio(precio) ?: 0.0,
            cantidad = cantidad.trim().toIntOrNull() ?: 0,
            marca = marca
        )
    }

    private fun mostrarErrores(errores: Map<Campo, Int>) {
        var primero: EditText? = null
        for ((campo, recurso) in errores) {
            val mensaje = getString(recurso)
            val vista: EditText? = when (campo) {
                Campo.CODIGO -> etCodigo
                Campo.NOMBRE -> etNombre
                Campo.COLOR -> etColor
                Campo.PRECIO -> etPrecio
                Campo.CANTIDAD -> etCantidad
                Campo.MARCA -> etMarca
                Campo.TALLA -> null
            }
            if (vista != null) {
                vista.error = mensaje
                if (primero == null) primero = vista
            } else {
                (spTalla.selectedView as? TextView)?.error = mensaje
            }
        }
        primero?.requestFocus()
        Toast.makeText(this, R.string.toast_corrige_errores, Toast.LENGTH_SHORT).show()
    }

    protected fun limpiarErrores() {
        listOf(etCodigo, etNombre, etColor, etPrecio, etCantidad, etMarca).forEach { it.error = null }
        (spTalla.selectedView as? TextView)?.error = null
    }

    protected fun mostrarPrenda(prenda: Prenda) {
        etCodigo.setText(prenda.codigo)
        etNombre.setText(prenda.nombre)
        etColor.setText(prenda.color)
        etPrecio.setText(String.format(Locale.US, "%.2f", prenda.precio))
        etCantidad.setText(prenda.cantidad.toString())
        etMarca.setText(prenda.marca)
        val indice = resources.getStringArray(R.array.tallas).indexOf(prenda.talla)
        spTalla.setSelection(if (indice >= 0) indice else 0)
    }

    protected fun limpiarFormulario() {
        listOf(etCodigo, etNombre, etColor, etPrecio, etCantidad, etMarca).forEach { it.setText("") }
        spTalla.setSelection(0)
        limpiarErrores()
        etCodigo.requestFocus()
    }
}
