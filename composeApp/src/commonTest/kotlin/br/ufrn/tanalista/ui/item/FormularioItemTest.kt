package br.ufrn.tanalista.ui.item

import br.ufrn.tanalista.model.ItemLista
import br.ufrn.tanalista.model.Unidade
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FormularioItemTest {
    private val itens = listOf(ItemLista(id = 1, listaId = 1, nome = "Leite 200g"))

    @Test
    fun `quantidade aceita virgula como separador decimal`() {
        assertEquals(1.5, FormularioItem(quantidade = " 1,5 ").quantidadeInformada)
    }

    @Test
    fun `quantidade em branco nao gera erro`() {
        assertNull(FormularioItem(nome = "Arroz").erroQuantidade())
    }

    @Test
    fun `quantidade zero ou que nao e numero gera erro`() {
        assertNotNull(FormularioItem(quantidade = "0").erroQuantidade())
        assertNotNull(FormularioItem(quantidade = "abc").erroQuantidade())
    }

    @Test
    fun `nome duplicado na lista gera erro e bloqueia a adicao`() {
        val formulario = FormularioItem(nome = "LEITE 200G")

        assertNotNull(formulario.erroNome(listaId = 1, itensExistentes = itens))
        assertFalse(formulario.podeAdicionar(listaId = 1, itensExistentes = itens))
    }

    @Test
    fun `nome em branco bloqueia a adicao sem exibir erro`() {
        val formulario = FormularioItem()

        assertNull(formulario.erroNome(listaId = 1, itensExistentes = itens))
        assertFalse(formulario.podeAdicionar(listaId = 1, itensExistentes = itens))
    }

    @Test
    fun `formulario valido pode ser adicionado`() {
        val formulario = FormularioItem(nome = "Leite 400g", quantidade = "2", unidade = Unidade.UNIDADE)

        assertTrue(formulario.podeAdicionar(listaId = 1, itensExistentes = itens))
    }

    @Test
    fun `detalhes do item omitem campos nao informados`() {
        assertEquals("", detalhesDoItem(ItemLista(id = 1, listaId = 1, nome = "Arroz")))
        assertEquals(
            "1,5 kg",
            detalhesDoItem(ItemLista(id = 2, listaId = 1, nome = "Tomate", quantidade = 1.5, unidade = Unidade.QUILO)),
        )
    }
}
