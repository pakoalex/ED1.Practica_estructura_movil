package mx.edu.upemor.ed1persistenciadatos.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class CalculadoraTest {

    private val calc = Calculadora()

    private fun errorDe(expresion: String): ErrorCalculo {
        try {
            calc.evaluar(expresion)
        } catch (e: CalculoException) {
            return e.error
        }
        fail("Se esperaba un error para: $expresion")
        throw IllegalStateException()
    }

    // ---- Una prueba por operación ----
    @Test fun suma() = assertEquals("8", calc.calcular("5+3"))
    @Test fun resta() = assertEquals("2", calc.calcular("5−3"))
    @Test fun multiplicacion() = assertEquals("15", calc.calcular("5×3"))
    @Test fun division() = assertEquals("3.5", calc.calcular("7÷2"))
    @Test fun decimalesSinRuidoDePuntoFlotante() = assertEquals("0.3", calc.calcular("0.1+0.2"))

    // ---- Jerarquía de operaciones ----
    @Test fun jerarquiaMultiplicacionAntesQueSuma() = assertEquals("27", calc.calcular("12+5×3"))
    @Test fun parentesisCambianLaJerarquia() = assertEquals("51", calc.calcular("(12+5)×3"))
    @Test fun potenciaEsAsociativaPorLaDerecha() = assertEquals("512", calc.calcular("2^3^2"))

    // ---- Porcentaje, signo, cuadrado ----
    @Test fun porcentaje() = assertEquals("0.5", calc.calcular("50%"))
    @Test fun cuadrado() = assertEquals("25", calc.calcular("5²"))
    @Test fun cambiarSigno() {
        assertEquals("(-5)", calc.cambiarSigno("5"))
        assertEquals("5", calc.cambiarSigno("(-5)"))
        assertEquals("3+(-4)", calc.cambiarSigno("3+4"))
    }

    // ---- Manejo de errores (la app no debe cerrarse) ----
    @Test fun divisionEntreCero() = assertEquals(ErrorCalculo.DIVISION_CERO, errorDe("8÷0"))
    @Test fun dobleOperadorConsecutivo() = assertEquals(ErrorCalculo.DOBLE_OPERADOR, errorDe("5++3"))
    @Test fun expresionIncompleta() = assertEquals(ErrorCalculo.EXPRESION_INCOMPLETA, errorDe("5+"))
    @Test fun parentesisSinCerrar() = assertEquals(ErrorCalculo.EXPRESION_INCOMPLETA, errorDe("(5+3"))

    // ---- Funciones científicas ----
    @Test fun factorialValido() = assertEquals("120", calc.calcular("5!"))
    @Test fun factorialDeVeinteEsExacto() = assertEquals("2432902008176640000", calc.calcular("20!"))
    @Test fun factorialFueraDeRango() = assertEquals(ErrorCalculo.FACTORIAL_INVALIDO, errorDe("21!"))
    @Test fun factorialDeNoEntero() = assertEquals(ErrorCalculo.FACTORIAL_INVALIDO, errorDe("2.5!"))
    @Test fun raizCuadrada() = assertEquals("3", calc.calcular("√(9)"))
    @Test fun raizNegativa() = assertEquals(ErrorCalculo.RAIZ_NEGATIVA, errorDe("√(-4)"))
    @Test fun potenciaXY() = assertEquals("1024", calc.calcular("2^10"))
    @Test fun piUsaMathPI() = assertEquals(Math.PI, calc.evaluar("π"), 1e-12)
    @Test fun multiplicacionImplicitaConPi() = assertEquals(2 * Math.PI, calc.evaluar("2π"), 1e-12)
    @Test fun logaritmoNaturalDeE() = assertEquals(1.0, calc.evaluar("ln(${Math.E})"), 1e-9)
    @Test fun logaritmoBase10() = assertEquals("2", calc.calcular("log(100)"))
    @Test fun logaritmoInvalido() {
        assertEquals(ErrorCalculo.LOGARITMO_INVALIDO, errorDe("ln(0)"))
        assertEquals(ErrorCalculo.LOGARITMO_INVALIDO, errorDe("log(-5)"))
    }

    // ---- Grados / radianes ----
    @Test fun senoEnGrados() {
        calc.modoAngulo = ModoAngulo.GRADOS
        assertEquals("0.5", calc.calcular("sin(30)"))
        assertEquals("0", calc.calcular("sin(180)"))
    }

    @Test fun senoEnRadianes() {
        calc.modoAngulo = ModoAngulo.RADIANES
        assertEquals(1.0, calc.evaluar("sin(π÷2)"), 1e-12)
    }

    @Test fun tangenteIndefinidaEnNoventaGrados() {
        calc.modoAngulo = ModoAngulo.GRADOS
        assertEquals(ErrorCalculo.TANGENTE_INDEFINIDA, errorDe("tan(90)"))
    }
}
