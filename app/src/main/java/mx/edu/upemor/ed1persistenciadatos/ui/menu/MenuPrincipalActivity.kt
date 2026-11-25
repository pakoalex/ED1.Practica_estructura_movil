package mx.edu.upemor.ed1persistenciadatos.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.ui.calculadora.MenuCalculadoraActivity
import mx.edu.upemor.ed1persistenciadatos.ui.prendas.BaseDatosActivity

/** Primer menú. Es la raíz de la navegación, por eso no muestra flecha de regreso. */
class MenuPrincipalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)
    }

    fun irBaseDatos(v: View) {
        startActivity(Intent(this, BaseDatosActivity::class.java))
    }

    fun irCalculadora(v: View) {
        startActivity(Intent(this, MenuCalculadoraActivity::class.java))
    }

    fun irAcercaDe(v: View) {
        startActivity(Intent(this, AcercaDeActivity::class.java))
    }
}
