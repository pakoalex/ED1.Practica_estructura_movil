package mx.edu.upemor.ed1persistenciadatos.util

import mx.edu.upemor.ed1persistenciadatos.R

/** Validaciones del formulario de prendas. Devuelve, por campo, el id del string de error. */
object Validador {

    enum class Campo { CODIGO, NOMBRE, TALLA, COLOR, PRECIO, CANTIDAD, MARCA }

    private val REGEX_CODIGO = Regex("^[A-Za-z0-9]{3,10}$")
    private val REGEX_LETRAS = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]+$")

    fun parsePrecio(texto: String): Double? = texto.trim().replace(',', '.').toDoubleOrNull()

    /**
     * @param tallaPosicion posición del Spinner (0 = "Seleccione talla", no válido)
     * @return mapa vacío si todo es válido; en otro caso, campo -> recurso de mensaje.
     */
    fun validarPrenda(
        codigo: String,
        nombre: String,
        tallaPosicion: Int,
        color: String,
        precio: String,
        cantidad: String,
        marca: String
    ): Map<Campo, Int> {
        val errores = linkedMapOf<Campo, Int>()

        if (codigo.isBlank()) errores[Campo.CODIGO] = R.string.error_obligatorio
        else if (!REGEX_CODIGO.matches(codigo)) errores[Campo.CODIGO] = R.string.error_codigo_formato

        if (nombre.isBlank()) errores[Campo.NOMBRE] = R.string.error_obligatorio
        else if (nombre.length !in 2..40) errores[Campo.NOMBRE] = R.string.error_nombre_longitud

        if (tallaPosicion <= 0) errores[Campo.TALLA] = R.string.error_talla

        if (color.isBlank()) errores[Campo.COLOR] = R.string.error_obligatorio
        else if (!REGEX_LETRAS.matches(color)) errores[Campo.COLOR] = R.string.error_color

        if (precio.isBlank()) errores[Campo.PRECIO] = R.string.error_obligatorio
        else {
            val valor = parsePrecio(precio)
            if (valor == null) errores[Campo.PRECIO] = R.string.error_precio_numero
            else if (valor <= 0.0) errores[Campo.PRECIO] = R.string.error_precio
        }

        if (cantidad.isBlank()) errores[Campo.CANTIDAD] = R.string.error_obligatorio
        else {
            val valor = cantidad.trim().toIntOrNull()
            if (valor == null) errores[Campo.CANTIDAD] = R.string.error_cantidad_numero
            else if (valor < 0) errores[Campo.CANTIDAD] = R.string.error_cantidad
        }

        if (marca.isBlank()) errores[Campo.MARCA] = R.string.error_obligatorio
        else if (marca.length !in 2..30) errores[Campo.MARCA] = R.string.error_marca_longitud

        return errores
    }
}
