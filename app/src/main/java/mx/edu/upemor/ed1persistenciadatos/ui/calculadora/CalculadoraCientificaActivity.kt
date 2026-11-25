package mx.edu.upemor.ed1persistenciadatos.ui.calculadora

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.data.entity.HistorialCalculo
import mx.edu.upemor.ed1persistenciadatos.logic.ErrorCalculo
import mx.edu.upemor.ed1persistenciadatos.logic.ModoAngulo

/** Calculadora normal + funciones científicas y selector de grados / radianes. */
class CalculadoraCientificaActivity : CalculadoraBaseActivity() {

    override val layoutId: Int get() = R.layout.activity_calculadora_cientifica
    override val tipoCalculo: String get() = HistorialCalculo.TIPO_CIENTIFICA

    private lateinit var rgAngulo: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rgAngulo = findViewById(R.id.rgAngulo)

        val modo = savedInstanceState?.getString(CLAVE_MODO)
            ?.let { ModoAngulo.valueOf(it) } ?: ModoAngulo.GRADOS
        calculadora.modoAngulo = modo
        rgAngulo.check(if (modo == ModoAngulo.GRADOS) R.id.rbGrados else R.id.rbRadianes)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CLAVE_MODO, calculadora.modoAngulo.name)
    }

    /** sin, cos, tan, ln, log y √: se escribe el nombre seguido de "(". */
    fun onFuncion(v: View) {
        val nombre = (v as Button).text.toString()
        agregarInicio("$nombre(")
    }

    fun onFactorial(v: View) {
        agregarPostfijo("!")
    }

    fun onCuadrado(v: View) {
        agregarPostfijo("²")
    }

    /** xʸ: el usuario escribe la base, pulsa xʸ y escribe el exponente. */
    fun onPotencia(v: View) {
        agregarOperador("^")
    }

    fun onPi(v: View) {
        agregarInicio("π")
    }

    fun onParentesis(v: View) {
        val simbolo = (v as Button).text.toString()
        if (simbolo == ")" && (parentesisAbiertos() <= 0 || !terminaEnOperando())) {
            mostrarError(ErrorCalculo.SINTAXIS)
            return
        }
        agregarInicio(simbolo)
    }

    /** android:onClick de los RadioButton Grados / Radianes. */
    fun onModoAngulo(v: View) {
        calculadora.modoAngulo =
            if (v.id == R.id.rbGrados) ModoAngulo.GRADOS else ModoAngulo.RADIANES
    }

    private companion object {
        const val CLAVE_MODO = "calc_modo_angulo"
    }
}
