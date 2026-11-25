package mx.edu.upemor.ed1persistenciadatos.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.ui.menu.MenuPrincipalActivity

/** Pantalla de bienvenida con autenticación biométrica (huella dactilar). */
class Huella_Dactilar : AppCompatActivity() {

    private lateinit var tvEstado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_huella_dactilar)
        tvEstado = findViewById(R.id.tvEstadoHuella)

        // Solo se lanza automáticamente la primera vez (no al girar la pantalla).
        if (savedInstanceState == null) autenticar()
    }

    /** android:onClick del botón "Usar huella dactilar". */
    fun onAutenticar(v: View) {
        autenticar()
    }

    private fun autenticar() {
        val manager = BiometricManager.from(this)
        when (manager.canAuthenticate(BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> mostrarPrompt()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                tvEstado.setText(R.string.huella_sin_hardware)
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                tvEstado.setText(R.string.huella_hardware_no_disponible)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                tvEstado.setText(R.string.huella_sin_registrar)
            else -> tvEstado.setText(R.string.huella_error_generico)
        }
    }

    private fun mostrarPrompt() {
        val ejecutor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(this, ejecutor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                tvEstado.setText(R.string.huella_exito)
                irAlMenu()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                tvEstado.text = getString(R.string.huella_error_prompt, errString)
            }

            override fun onAuthenticationFailed() {
                tvEstado.setText(R.string.huella_no_reconocida)
            }
        })

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.huella_prompt_titulo))
            .setSubtitle(getString(R.string.huella_prompt_subtitulo))
            .setNegativeButtonText(getString(R.string.cancelar))
            .setAllowedAuthenticators(BIOMETRIC_WEAK)
            .build()

        prompt.authenticate(info)
    }

    private fun irAlMenu() {
        startActivity(Intent(this, MenuPrincipalActivity::class.java))
        finish() // la pantalla de huella no debe quedar en la pila
    }
}
