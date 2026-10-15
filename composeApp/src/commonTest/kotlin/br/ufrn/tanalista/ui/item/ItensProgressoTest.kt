package br.ufrn.tanalista.ui.item

import br.ufrn.tanalista.model.ListaCompra
import br.ufrn.tanalista.model.ProgressoCompra
import br.ufrn.tanalista.model.resumirListas
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ItensProgressoTest {
    @Test
    fun `inclusao valida muda o total somente da lista correspondente`() {
        val state = ItensState()
        val listas = listOf(ListaCompra(1, "Casa"), ListaCompra(2, "Festa"))
        assertTrue(state.adicionar(1, FormularioItem(nome = "Leite")))
        assertTrue(state.adicionar(1, FormularioItem(nome = "Pao")))
        assertTrue(state.adicionar(2, FormularioItem(nome = "Suco")))

        val porId = resumirListas(listas, state.itens).associateBy { it.lista.id }

        assertEquals(ProgressoCompra(0, 2), porId.getValue(1).progresso)
        assertEquals(ProgressoCompra(0, 1), porId.getValue(2).progresso)
    }

    @Test
    fun `inclusao invalida ou duplicada nao altera o total`() {
        val state = ItensState()
        assertTrue(state.adicionar(1, FormularioItem(nome = "Leite")))
        assertFalse(state.adicionar(1, FormularioItem(nome = " LEITE ")))
        assertFalse(state.adicionar(1, FormularioItem(nome = "Pao", quantidade = "0")))

        val resumo = resumirListas(listOf(ListaCompra(1, "Casa")), state.itens).single()

        assertEquals(ProgressoCompra(0, 1), resumo.progresso)
    }

    @Test
    fun `exclusao dos itens de uma lista nao afeta o progresso das demais`() {
        val state = ItensState()
        state.adicionar(1, FormularioItem(nome = "Leite"))
        state.adicionar(2, FormularioItem(nome = "Suco"))

        state.removerDaLista(1)

        val resumos = resumirListas(listOf(ListaCompra(1, "Nova"), ListaCompra(2, "Festa")), state.itens)
        val porId = resumos.associateBy { it.lista.id }
        assertEquals(ProgressoCompra(0, 0), porId.getValue(1).progresso)
        assertEquals(ProgressoCompra(0, 1), porId.getValue(2).progresso)
    }
}
