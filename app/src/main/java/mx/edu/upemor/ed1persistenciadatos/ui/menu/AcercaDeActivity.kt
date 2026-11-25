package mx.edu.upemor.ed1persistenciadatos.ui.menu

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.ui.base.BaseActivity

class AcercaDeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acerca_de)
        findViewById<TextView>(R.id.tvVersion).text =
            getString(R.string.acerca_version, obtenerVersion())
    }

    private fun obtenerVersion(): String {
        return try {
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
            }
            info.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }
}
