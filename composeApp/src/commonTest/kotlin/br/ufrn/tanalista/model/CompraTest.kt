package br.ufrn.tanalista.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class CompraTest {
    private val tomate = ItemLista(1, 7, "Tomate", 2.0, Unidade.QUILO)
    private val alho = ItemLista(2, 7, "Alho", 1.0, Unidade.QUILO)

    @Test
    fun `soma o preco encontrado pela quantidade levada somente dos itens marcados`() {
        val inicial = Compra(listaId = 7)
        val compra = inicial.marcarComprado(tomate, RegistroCompra(800, 1_500))

        assertEquals(0L, inicial.totalCentavos)
        assertEquals(1_200L, compra.registroDoItem(tomate)?.subtotalCentavos)
        assertNull(compra.registroDoItem(alho))
        assertEquals(1_200L, compra.totalCentavos)
        assertEquals(1, compra.itensComprados)

        val comDoisItens = compra.marcarComprado(alho, RegistroCompra(600, 250))
        assertEquals(1_350L, comDoisItens.totalCentavos)
        assertEquals(2, comDoisItens.itensComprados)
    }

    @Test
    fun `atualizar o registro substitui os valores e desmarcar remove o subtotal`() {
        val inicial = Compra(listaId = 7).marcarComprado(tomate, RegistroCompra(800, 1_500))
        val precoCorrigido = inicial.marcarComprado(tomate, RegistroCompra(900, 1_500))
        val quantidadeCorrigida = precoCorrigido.marcarComprado(tomate, RegistroCompra(900, 2_000))
        val desmarcada = quantidadeCorrigida.desmarcar(tomate)

        assertEquals(1_350L, precoCorrigido.totalCentavos)
        assertEquals(1_800L, quantidadeCorrigida.totalCentavos)
        assertEquals(1, quantidadeCorrigida.itensComprados)
        assertNull(desmarcada.registroDoItem(tomate))
        assertEquals(0L, desmarcada.totalCentavos)
        assertEquals(0, desmarcada.itensComprados)
    }

    @Test
    fun `arredonda cada subtotal para centavos antes de somar`() {
        val metade = RegistroCompra(101, 500)
        val compra =
            Compra(listaId = 7)
                .marcarComprado(tomate, metade)
                .marcarComprado(alho, metade)

        assertEquals(51L, metade.subtotalCentavos)
        assertEquals(102L, compra.totalCentavos)
        assertEquals(50L, RegistroCompra(101, 499).subtotalCentavos)
    }

    @Test
    fun `rejeita preco negativo e quantidade nao positiva`() {
        assertFailsWith<IllegalArgumentException> { RegistroCompra(-1, 1_000) }
        assertFailsWith<IllegalArgumentException> { RegistroCompra(100, 0) }
        assertFailsWith<IllegalArgumentException> { RegistroCompra(100, -1) }
        assertEquals(0L, RegistroCompra(0, 1_000).subtotalCentavos)
    }

    @Test
    fun `impede registrar item que pertence a outra lista`() {
        val compra = Compra(listaId = 7)
        val itemDeOutraLista = tomate.copy(listaId = 8)

        assertFailsWith<IllegalArgumentException> {
            compra.marcarComprado(itemDeOutraLista, RegistroCompra(800, 1_500))
        }
        assertEquals(0L, compra.totalCentavos)
    }
}
