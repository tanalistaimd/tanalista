package br.ufrn.tanalista.ui.item

import br.ufrn.tanalista.model.Categoria
import br.ufrn.tanalista.model.Unidade
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ItensStateTest {
    @Test
    fun `edita os campos e aceita manter o proprio nome`() {
        val state = ItensState()
        state.adicionar(1, FormularioItem(nome = "Leite", quantidade = "1"))
        val item = state.daLista(1).single()
        val edicao =
            FormularioItem(
                nome = " LEITE ",
                quantidade = "2,5",
                unidade = Unidade.LITRO,
                categoria = Categoria.LATICINIOS,
            )

        assertTrue(state.editar(item, edicao))
        assertEquals(
            item.copy(nome = "LEITE", quantidade = 2.5, unidade = Unidade.LITRO, categoria = Categoria.LATICINIOS),
            state.daLista(1).single(),
        )
        assertTrue(state.editar(item, FormularioItem(nome = "Bebida")))
        assertEquals(item.copy(nome = "Bebida", quantidade = null), state.daLista(1).single())
    }

    @Test
    fun `rejeita nome duplicado e quantidade invalida na edicao`() {
        val state = ItensState()
        state.adicionar(1, FormularioItem(nome = "Café"))
        state.adicionar(1, FormularioItem(nome = "Leite"))
        val antes = state.daLista(1)
        val item = antes.last()

        assertFalse(state.editar(item, FormularioItem(nome = " cafe ")))
        assertFalse(state.editar(item, FormularioItem(nome = "Leite", quantidade = "0")))
        assertEquals(antes, state.daLista(1))
    }

    @Test
    fun `desfaz a remocao sem colidir com o id de um novo item`() {
        val state = ItensState()
        state.adicionar(1, FormularioItem(nome = "Leite"))
        val item = state.daLista(1).single()

        assertTrue(state.remover(item))
        assertTrue(state.daLista(1).isEmpty())
        assertFalse(state.editar(item, FormularioItem(nome = "Suco")))
        assertTrue(state.adicionar(1, FormularioItem(nome = "Pão")))
        assertTrue(state.restaurar(item))
        assertFalse(state.restaurar(item))
        assertEquals(listOf("Leite", "Pão"), state.daLista(1).map { it.nome })
        assertEquals(
            2,
            state
                .daLista(1)
                .map { it.id }
                .distinct()
                .size,
        )
    }

    @Test
    fun `desfazer respeita a unicidade de nome na lista`() {
        val state = ItensState()
        state.adicionar(1, FormularioItem(nome = "Café"))
        val item = state.daLista(1).single()

        assertTrue(state.remover(item))
        assertTrue(state.adicionar(1, FormularioItem(nome = "Cafe")))
        assertFalse(state.restaurar(item))
        assertEquals("Cafe", state.daLista(1).single().nome)
    }
}
