package mx.edu.upemor.ed1persistenciadatos.ui.calculadora

import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.data.entity.HistorialCalculo

/** Operaciones básicas: todo el comportamiento está en [CalculadoraBaseActivity]. */
class CalculadoraNormalActivity : CalculadoraBaseActivity() {
    override val layoutId: Int get() = R.layout.activity_calculadora_normal
    override val tipoCalculo: String get() = HistorialCalculo.TIPO_NORMAL
}
