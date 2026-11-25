package mx.edu.upemor.ed1persistenciadatos.ui.calculadora

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.launch
import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.data.entity.HistorialCalculo
import mx.edu.upemor.ed1persistenciadatos.logic.CalculoException
import mx.edu.upemor.ed1persistenciadatos.logic.Calculadora
import mx.edu.upemor.ed1persistenciadatos.logic.ErrorCalculo
import mx.edu.upemor.ed1persistenciadatos.tiendaApp
import mx.edu.upemor.ed1persistenciadatos.ui.base.BaseActivity

/**
 * Comportamiento común de las dos calculadoras. Todos los botones se enlazan desde el XML con
 * android:onClick; aquí solo se arma la expresión y se delega el cálculo en [Calculadora].
 */
abstract class CalculadoraBaseActivity : BaseActivity() {

    protected abstract val layoutId: Int
    protected abstract val tipoCalculo: String

    protected val calculadora = Calculadora()

    private var expresion = ""
    private var recienCalculado = false
    private lateinit var tvExpresion: TextView
    private lateinit var tvPantalla: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        tvExpresion = findViewById(R.id.tvExpresion)
        tvPantalla = findViewById(R.id.tvPantalla)

        if (savedInstanceState != null) { // restaura el estado al girar la pantalla
            expresion = savedInstanceState.getString(CLAVE_EXPRESION, "")
            recienCalculado = savedInstanceState.getBoolean(CLAVE_CALCULADO, false)
            tvExpresion.text = savedInstanceState.getString(CLAVE_LINEA, "")
        }
        actualizarPantalla()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CLAVE_EXPRESION, expresion)
        outState.putBoolean(CLAVE_CALCULADO, recienCalculado)
        outState.putString(CLAVE_LINEA, tvExpresion.text.toString())
    }

    // ------------------------------------------------------------ onClick (XML)

    fun onDigito(v: View) {
        nuevaEntrada()
        expresion += (v as Button).text
        actualizarPantalla()
    }

    fun onPunto(v: View) {
        nuevaEntrada()
        val numeroActual = Regex("""[\d.]*$""").find(expresion)?.value ?: ""
        if (numeroActual.contains('.')) return
        expresion += if (numeroActual.isEmpty()) "0." else "."
        actualizarPantalla()
    }

    fun onOperador(v: View) {
        agregarOperador((v as Button).text.toString())
    }

    fun onSigno(v: View) {
        if (expresion.isEmpty()) return
        expresion = calculadora.cambiarSigno(expresion)
        actualizarPantalla()
    }

    fun onPorcentaje(v: View) {
        agregarPostfijo("%")
    }

    fun onBorrar(v: View) {
        if (expresion.isEmpty()) return
        recienCalculado = false
        // Si el final es una función "sin(", se borra completa; en otro caso, un solo carácter.
        val funcion = Regex("""(sin|cos|tan|log|ln)\($""").find(expresion)
        expresion = if (funcion != null) expresion.substring(0, funcion.range.first) else expresion.dropLast(1)
        actualizarPantalla()
    }

    fun onLimpiar(v: View) {
        expresion = ""
        recienCalculado = false
        tvExpresion.text = ""
        actualizarPantalla()
    }

    fun onIgual(v: View) {
        if (expresion.isEmpty()) return
        try {
            val operacion = expresion
            val resultado = calculadora.calcular(operacion)
            tvExpresion.text = getString(R.string.calc_linea_expresion, operacion)
            guardarEnHistorial(operacion, resultado)
            expresion = resultado
            recienCalculado = true
            actualizarPantalla()
        } catch (e: CalculoException) {
            mostrarError(e.error)
        } catch (e: Exception) { // la app nunca debe cerrarse por una expresión inválida
            mostrarError(ErrorCalculo.SINTAXIS)
        }
    }

    // ------------------------------------------------------------ utilidades para las subclases

    /** Después de "=", escribir un número empieza una operación nueva. */
    private fun nuevaEntrada() {
        if (recienCalculado) {
            expresion = ""
            tvExpresion.text = ""
            recienCalculado = false
        }
    }

    protected fun agregarInicio(texto: String) {
        nuevaEntrada()
        expresion += texto
        actualizarPantalla()
    }

    protected fun agregarOperador(simbolo: String) {
        recienCalculado = false
        val ultimo = expresion.lastOrNull()
        when {
            expresion.isEmpty() && simbolo != "−" -> mostrarError(ErrorCalculo.EXPRESION_INCOMPLETA)
            ultimo != null && ultimo in OPERADORES -> mostrarError(ErrorCalculo.DOBLE_OPERADOR)
            ultimo == '(' && simbolo != "−" -> mostrarError(ErrorCalculo.EXPRESION_INCOMPLETA)
            else -> {
                expresion += simbolo
                actualizarPantalla()
            }
        }
    }

    protected fun agregarPostfijo(simbolo: String) {
        if (!terminaEnOperando()) {
            mostrarError(ErrorCalculo.EXPRESION_INCOMPLETA)
            return
        }
        recienCalculado = false
        expresion += simbolo
        actualizarPantalla()
    }

    protected fun terminaEnOperando(): Boolean {
        val ultimo = expresion.lastOrNull() ?: return false
        return ultimo.isDigit() || ultimo in "π)%²!"
    }

    protected fun parentesisAbiertos(): Int = expresion.count { it == '(' } - expresion.count { it == ')' }

    protected fun mostrarError(error: ErrorCalculo) {
        Toast.makeText(this, mensajeDe(error), Toast.LENGTH_SHORT).show()
    }

    private fun mensajeDe(error: ErrorCalculo): Int = when (error) {
        ErrorCalculo.DIVISION_CERO -> R.string.calc_error_division_cero
        ErrorCalculo.DOBLE_OPERADOR -> R.string.calc_error_doble_operador
        ErrorCalculo.EXPRESION_INCOMPLETA -> R.string.calc_error_incompleta
        ErrorCalculo.SINTAXIS -> R.string.calc_error_sintaxis
        ErrorCalculo.FACTORIAL_INVALIDO -> R.string.calc_error_factorial
        ErrorCalculo.RAIZ_NEGATIVA -> R.string.calc_error_raiz
        ErrorCalculo.LOGARITMO_INVALIDO -> R.string.calc_error_logaritmo
        ErrorCalculo.TANGENTE_INDEFINIDA -> R.string.calc_error_tangente
        ErrorCalculo.RESULTADO_INVALIDO -> R.string.calc_error_resultado
    }

    private fun actualizarPantalla() {
        tvPantalla.text = if (expresion.isEmpty()) getString(R.string.calc_cero) else expresion
    }

    private fun guardarEnHistorial(operacion: String, resultado: String) {
        val app = tiendaApp
        app.appScope.launch {
            app.historialRepository.insertar(
                HistorialCalculo(
                    expresion = operacion,
                    resultado = resultado,
                    tipo = tipoCalculo,
                    fecha = System.currentTimeMillis()
                )
            )
        }
    }

    private companion object {
        const val OPERADORES = "+−×÷^"
        const val CLAVE_EXPRESION = "calc_expresion"
        const val CLAVE_CALCULADO = "calc_calculado"
        const val CLAVE_LINEA = "calc_linea"
    }
}
