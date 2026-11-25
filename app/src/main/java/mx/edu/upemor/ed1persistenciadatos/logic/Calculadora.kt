package mx.edu.upemor.ed1persistenciadatos.logic

import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.abs

/** Modo de interpretación de los ángulos para sen, cos y tan. */
enum class ModoAngulo { GRADOS, RADIANES }

/** Errores de cálculo. La interfaz los traduce a mensajes (esta clase no depende de Android). */
enum class ErrorCalculo {
    DIVISION_CERO,
    DOBLE_OPERADOR,
    EXPRESION_INCOMPLETA,
    SINTAXIS,
    FACTORIAL_INVALIDO,
    RAIZ_NEGATIVA,
    LOGARITMO_INVALIDO,
    TANGENTE_INDEFINIDA,
    RESULTADO_INVALIDO
}

class CalculoException(val error: ErrorCalculo) : Exception(error.name)

/**
 * Motor de cálculo independiente de la interfaz.
 *
 * Gramática (de menor a mayor precedencia):
 *   expr    := term { ('+' | '-') term }
 *   term    := unario { ('×' | '÷') unario | <multiplicación implícita> }
 *   unario  := '-' unario | '+' unario | potencia
 *   potencia:= postfijo [ '^' unario ]            (asociativa por la derecha)
 *   postfijo:= primario { '!' | '%' | '²' }
 *   primario:= NUMERO | 'π' | '(' expr ')' | FUNC '(' expr ')' | '√' primario
 *
 * El operador % es postfijo y divide entre 100 (50% = 0.5).
 */
class Calculadora(var modoAngulo: ModoAngulo = ModoAngulo.GRADOS) {

    // ---------------------------------------------------------------- API pública

    /** Evalúa la expresión. Lanza [CalculoException] si hay algún error. */
    fun evaluar(expresion: String): Double {
        val tokens = tokenizar(expresion)
        if (tokens.isEmpty()) throw CalculoException(ErrorCalculo.EXPRESION_INCOMPLETA)

        for (i in 1 until tokens.size) {
            if (tokens[i - 1] is Token.Op && tokens[i] is Token.Op) {
                throw CalculoException(ErrorCalculo.DOBLE_OPERADOR)
            }
        }

        val resultado = Parser(tokens, modoAngulo).analizar()
        if (resultado.isNaN() || resultado.isInfinite()) {
            throw CalculoException(ErrorCalculo.RESULTADO_INVALIDO)
        }
        return resultado
    }

    /** Evalúa y devuelve el resultado ya formateado para mostrarse. */
    fun calcular(expresion: String): String = formatear(evaluar(expresion))

    /** Quita ruido de punto flotante (0.1+0.2 -> "0.3") y notación científica. */
    fun formatear(valor: Double): String {
        if (valor.isNaN() || valor.isInfinite()) throw CalculoException(ErrorCalculo.RESULTADO_INVALIDO)
        if (valor == 0.0) return "0"
        return BigDecimal(valor).round(MathContext(15)).stripTrailingZeros().toPlainString()
    }

    /** Cambia el signo del último número: "5" -> "(-5)", "(-5)" -> "5", "3+4" -> "3+(-4)". */
    fun cambiarSigno(expresion: String): String {
        val negado = Regex("""\(-(\d+\.?\d*|\.\d+)\)$""").find(expresion)
        if (negado != null) {
            return expresion.substring(0, negado.range.first) + negado.groupValues[1]
        }
        val numero = Regex("""(\d+\.?\d*|\.\d+)$""").find(expresion) ?: return expresion
        return expresion.substring(0, numero.range.first) + "(-" + numero.value + ")"
    }

    companion object {
        fun factorial(n: Double): Double {
            if (n < 0.0 || n > 20.0 || n != Math.floor(n)) {
                throw CalculoException(ErrorCalculo.FACTORIAL_INVALIDO)
            }
            var r = 1.0
            for (i in 2..n.toInt()) r *= i
            return r
        }

        fun raiz(n: Double): Double {
            if (n < 0.0) throw CalculoException(ErrorCalculo.RAIZ_NEGATIVA)
            return Math.sqrt(n)
        }

        fun ln(n: Double): Double {
            if (n <= 0.0) throw CalculoException(ErrorCalculo.LOGARITMO_INVALIDO)
            return Math.log(n)
        }

        fun log10(n: Double): Double {
            if (n <= 0.0) throw CalculoException(ErrorCalculo.LOGARITMO_INVALIDO)
            return Math.log10(n)
        }

        fun potencia(base: Double, exponente: Double): Double {
            if (base == 0.0 && exponente < 0.0) throw CalculoException(ErrorCalculo.DIVISION_CERO)
            return Math.pow(base, exponente)
        }

        private fun limpiar(x: Double): Double = if (abs(x) < 1e-12) 0.0 else x
    }

    // ---------------------------------------------------------------- Tokens

    private sealed class Token {
        data class Num(val valor: Double) : Token()
        data class Op(val simbolo: Char) : Token()          // + - × ÷ ^
        data class Func(val nombre: String) : Token()       // ln log sin cos tan
        object Pi : Token()
        object ParAbre : Token()
        object ParCierra : Token()
        object Factorial : Token()
        object Porcentaje : Token()
        object Cuadrado : Token()
        object Raiz : Token()
    }

    private fun tokenizar(expresion: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        while (i < expresion.length) {
            val c = expresion[i]
            when {
                c.isWhitespace() -> i++
                c.isDigit() || c == '.' -> {
                    val inicio = i
                    var puntos = 0
                    while (i < expresion.length && (expresion[i].isDigit() || expresion[i] == '.')) {
                        if (expresion[i] == '.') puntos++
                        i++
                    }
                    val texto = expresion.substring(inicio, i)
                    if (puntos > 1 || texto == ".") throw CalculoException(ErrorCalculo.SINTAXIS)
                    tokens.add(Token.Num(texto.toDouble()))
                }
                c == '+' -> { tokens.add(Token.Op('+')); i++ }
                c == '-' || c == '−' -> { tokens.add(Token.Op('-')); i++ }
                c == '×' || c == '*' -> { tokens.add(Token.Op('×')); i++ }
                c == '÷' || c == '/' -> { tokens.add(Token.Op('÷')); i++ }
                c == '^' -> { tokens.add(Token.Op('^')); i++ }
                c == '(' -> { tokens.add(Token.ParAbre); i++ }
                c == ')' -> { tokens.add(Token.ParCierra); i++ }
                c == 'π' -> { tokens.add(Token.Pi); i++ }
                c == '!' -> { tokens.add(Token.Factorial); i++ }
                c == '%' -> { tokens.add(Token.Porcentaje); i++ }
                c == '²' -> { tokens.add(Token.Cuadrado); i++ }
                c == '√' -> { tokens.add(Token.Raiz); i++ }
                c.isLetter() -> {
                    val inicio = i
                    while (i < expresion.length && expresion[i].isLetter()) i++
                    val nombre = expresion.substring(inicio, i).lowercase()
                    if (nombre !in FUNCIONES) throw CalculoException(ErrorCalculo.SINTAXIS)
                    tokens.add(Token.Func(nombre))
                }
                else -> throw CalculoException(ErrorCalculo.SINTAXIS)
            }
        }
        return tokens
    }

    private val FUNCIONES = setOf("ln", "log", "sin", "cos", "tan")

    // ---------------------------------------------------------------- Analizador

    private class Parser(private val tokens: List<Token>, private val modo: ModoAngulo) {
        private var pos = 0

        private fun actual(): Token? = tokens.getOrNull(pos)

        fun analizar(): Double {
            val valor = expresion()
            if (pos < tokens.size) throw CalculoException(ErrorCalculo.SINTAXIS)
            return valor
        }

        private fun expresion(): Double {
            var v = termino()
            while (true) {
                val t = actual()
                if (t is Token.Op && (t.simbolo == '+' || t.simbolo == '-')) {
                    pos++
                    val r = termino()
                    v = if (t.simbolo == '+') v + r else v - r
                } else break
            }
            return v
        }

        private fun termino(): Double {
            var v = unario()
            while (true) {
                val t = actual()
                if (t is Token.Op && (t.simbolo == '×' || t.simbolo == '÷')) {
                    pos++
                    val r = unario()
                    if (t.simbolo == '×') {
                        v *= r
                    } else {
                        if (r == 0.0) throw CalculoException(ErrorCalculo.DIVISION_CERO)
                        v /= r
                    }
                } else if (iniciaPrimario(t)) {
                    v *= unario()               // multiplicación implícita: 2π, 3(4+1)
                } else break
            }
            return v
        }

        private fun iniciaPrimario(t: Token?): Boolean =
            t is Token.Num || t is Token.Func || t === Token.Pi || t === Token.ParAbre || t === Token.Raiz

        private fun unario(): Double {
            val t = actual()
            if (t is Token.Op && t.simbolo == '-') { pos++; return -unario() }
            if (t is Token.Op && t.simbolo == '+') { pos++; return unario() }
            return potenciaNivel()
        }

        private fun potenciaNivel(): Double {
            val base = postfijo()
            val t = actual()
            if (t is Token.Op && t.simbolo == '^') {
                pos++
                val exponente = unario()
                return potencia(base, exponente)
            }
            return base
        }

        private fun postfijo(): Double {
            var v = primario()
            loop@ while (true) {
                when (actual()) {
                    Token.Factorial -> { pos++; v = factorial(v) }
                    Token.Porcentaje -> { pos++; v /= 100.0 }
                    Token.Cuadrado -> { pos++; v *= v }
                    else -> break@loop
                }
            }
            return v
        }

        private fun primario(): Double {
            val t = actual() ?: throw CalculoException(ErrorCalculo.EXPRESION_INCOMPLETA)
            pos++
            return when (t) {
                is Token.Num -> t.valor
                Token.Pi -> Math.PI
                Token.ParAbre -> {
                    val v = expresion()
                    consumir(Token.ParCierra)
                    v
                }
                is Token.Func -> {
                    consumir(Token.ParAbre)
                    val argumento = expresion()
                    consumir(Token.ParCierra)
                    aplicarFuncion(t.nombre, argumento)
                }
                Token.Raiz -> raiz(primario())
                else -> throw CalculoException(ErrorCalculo.SINTAXIS)
            }
        }

        private fun consumir(esperado: Token) {
            val t = actual() ?: throw CalculoException(ErrorCalculo.EXPRESION_INCOMPLETA)
            if (t !== esperado) throw CalculoException(ErrorCalculo.SINTAXIS)
            pos++
        }

        private fun aplicarFuncion(nombre: String, x: Double): Double {
            return when (nombre) {
                "ln" -> ln(x)
                "log" -> log10(x)
                "sin" -> limpiar(Math.sin(aRadianes(x)))
                "cos" -> limpiar(Math.cos(aRadianes(x)))
                "tan" -> {
                    val rad = aRadianes(x)
                    if (abs(Math.cos(rad)) < 1e-12) throw CalculoException(ErrorCalculo.TANGENTE_INDEFINIDA)
                    limpiar(Math.tan(rad))
                }
                else -> throw CalculoException(ErrorCalculo.SINTAXIS)
            }
        }

        private fun aRadianes(x: Double): Double =
            if (modo == ModoAngulo.GRADOS) Math.toRadians(x) else x
    }
}
