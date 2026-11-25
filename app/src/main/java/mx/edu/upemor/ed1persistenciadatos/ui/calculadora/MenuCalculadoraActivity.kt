package mx.edu.upemor.ed1persistenciadatos.ui.calculadora

import android.content.Intent
import android.os.Bundle
import android.view.View
import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.ui.base.BaseActivity

/** Menú con botones a Calculadora normal, Calculadora científica e Historial. */
class MenuCalculadoraActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_calculadora)
    }

    fun irNormal(v: View) {
        startActivity(Intent(this, CalculadoraNormalActivity::class.java))
    }

    fun irCientifica(v: View) {
        startActivity(Intent(this, CalculadoraCientificaActivity::class.java))
    }

    fun irHistorial(v: View) {
        startActivity(Intent(this, HistorialActivity::class.java))
    }
}
