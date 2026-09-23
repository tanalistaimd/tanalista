package br.ufrn.tanalista.validation

import br.ufrn.tanalista.model.ListaCompra
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ListaCompraValidatorTest {
    @Test
    fun `deve normalizar nome removendo acentos maiusculas e espacos extras`() {
        val resultado =
            ListaCompraValidator.normalizarNome(
                "  COMPRAS   DO   MÊS  ",
            )

        assertEquals(
            "compras do mes",
            resultado,
        )
    }

    @Test
    fun `nome vazio deve ser invalido`() {
        assertFalse(
            ListaCompraValidator.nomeEhValido("   "),
        )
    }

    @Test
    fun `nome preenchido deve ser valido`() {
        assertTrue(
            ListaCompraValidator.nomeEhValido("Compras"),
        )
    }

    @Test
    fun `deve identificar lista duplicada pelo nome normalizado`() {
        val listasExistentes =
            listOf(
                ListaCompra(
                    id = 1,
                    nome = "Compras do Mês",
                ),
            )

        val resultado =
            ListaCompraValidator.nomeJaExiste(
                nome = "  COMPRAS   DO MES ",
                listasExistentes = listasExistentes,
            )

        assertTrue(resultado)
    }

    @Test
    fun `deve permitir nome que ainda nao existe`() {
        val listasExistentes =
            listOf(
                ListaCompra(
                    id = 1,
                    nome = "Compras do Mês",
                ),
            )

        val resultado =
            ListaCompraValidator.nomeJaExiste(
                nome = "Material Escolar",
                listasExistentes = listasExistentes,
            )

        assertFalse(resultado)
    }
}
