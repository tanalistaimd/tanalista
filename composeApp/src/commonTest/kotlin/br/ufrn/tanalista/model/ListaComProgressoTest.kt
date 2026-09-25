package br.ufrn.tanalista.model

import kotlin.test.Test
import kotlin.test.assertEquals

class ListaComProgressoTest {
    @Test
    fun `listas sao ordenadas por atualizacao mais recente com desempate por id`() {
        val resumos =
            listOf(
                ListaComProgresso(ListaCompra(1, "Antiga", 10), ProgressoCompra(1, 3)),
                ListaComProgresso(ListaCompra(2, "Recente", 20), ProgressoCompra(2, 3)),
                ListaComProgresso(ListaCompra(3, "Empate", 20), ProgressoCompra(0, 0)),
            )

        assertEquals(listOf(3, 2, 1), ordenarPorAtualizacao(resumos).map { it.lista.id })
        assertEquals(ProgressoCompra(1, 3), ordenarPorAtualizacao(resumos).last().progresso)
    }

    @Test
    fun `lista vazia nao produz progresso`() {
        assertEquals(emptyList(), ordenarPorAtualizacao(emptyList()))
    }
}
