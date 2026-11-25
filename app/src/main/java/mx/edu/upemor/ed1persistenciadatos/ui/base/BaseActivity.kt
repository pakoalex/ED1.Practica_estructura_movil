package mx.edu.upemor.ed1persistenciadatos.ui.base

import androidx.appcompat.app.AppCompatActivity

/**
 * Activity base de las pantallas secundarias: muestra la flecha de regreso en la barra
 * y, al pulsarla, cierra la pantalla con finish() para volver a la anterior.
 */
open class BaseActivity : AppCompatActivity() {

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
