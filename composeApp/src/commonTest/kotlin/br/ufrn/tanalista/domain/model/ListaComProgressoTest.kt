package br.ufrn.tanalista.domain.model

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

    @Test
    fun `resumo conta apenas itens da propria lista e inclui listas vazias`() {
        val listas = listOf(ListaCompra(1, "Casa", 10), ListaCompra(2, "Festa", 20), ListaCompra(3, "Vazia", 30))
        val itens = listOf(ItemLista(1, 1, "Leite"), ItemLista(2, 1, "Pao"), ItemLista(3, 2, "Suco"))

        val resumos = resumirListas(listas, itens)

        assertEquals(listOf(3, 2, 1), resumos.map { it.lista.id })
        assertEquals(
            listOf(ProgressoCompra(0, 0), ProgressoCompra(0, 1), ProgressoCompra(0, 2)),
            resumos.map { it.progresso },
        )
    }

    @Test
    fun `itens sem lista correspondente nao criam resumos`() {
        assertEquals(emptyList(), resumirListas(emptyList(), listOf(ItemLista(1, 1, "Leite"))))
    }

    @Test
    fun `resumo conta marcacoes da compra da lista e ignora item removido`() {
        val listas = listOf(ListaCompra(1, "Casa"), ListaCompra(2, "Festa"))
        val leite = ItemLista(1, 1, "Leite")
        val pao = ItemLista(2, 1, "Pão")
        val suco = ItemLista(3, 2, "Suco")
        val registro = RegistroCompra(600, 1_000)
        val compra = Compra(1).marcarComprado(leite, registro).marcarComprado(pao, registro)

        val resumos = resumirListas(listas, listOf(leite, suco), mapOf(1 to compra)).associateBy { it.lista.id }

        assertEquals(ProgressoCompra(1, 1), resumos[1]?.progresso)
        assertEquals(ProgressoCompra(0, 1), resumos[2]?.progresso)
        assertEquals(600L, compra.limitarA(listOf(leite)).totalCentavos)
    }
}
