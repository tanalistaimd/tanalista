package br.ufrn.tanalista.ui.mercado

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DecimalMercadoTest {
    @Test
    fun `aceita virgula ou ponto e converte sem ponto flutuante`() {
        assertEquals(800, interpretarDecimal("8,00", casas = 2))
        assertEquals(800, interpretarDecimal(" 8 ", casas = 2))
        assertEquals(1_500, interpretarDecimal("1.5", casas = 3))
        assertEquals(1, interpretarDecimal("0,001", casas = 3))
        assertEquals(0, interpretarDecimal("0,00", casas = 2))
    }

    @Test
    fun `rejeita entradas invalidas em vez de arredondar o texto`() {
        assertNull(interpretarDecimal("8,001", casas = 2))
        assertNull(interpretarDecimal("1.000,00", casas = 2))
        assertNull(interpretarDecimal("-", casas = 2))
        assertNull(interpretarDecimal("0,", casas = 2))
        assertNull(interpretarDecimal("21474836,48", casas = 2))
    }

    @Test
    fun `exibe centavos completos e quantidade sem zeros extras`() {
        assertEquals("R$ 0,05", formatarPreco(5))
        assertEquals("R$ 12,00", formatarPreco(1_200))
        assertEquals("1,5", formatarQuantidade(1_500))
        assertEquals("1", formatarQuantidade(1_000))
    }
}
