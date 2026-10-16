package br.ufrn.tanalista

import br.ufrn.tanalista.model.resumirListas
import kotlin.test.Test
import kotlin.test.assertEquals

class ListaAppStateTest {
    @Test
    fun `alteracao dos itens faz a lista antiga subir sem alterar as demais`() {
        var instante = 10L
        val state = ListaAppState(agora = { instante })
        state.alterarNome("Casa")
        state.criarLista()
        instante = 20L
        state.alterarNome("Festa")
        state.criarLista()
        assertEquals(listOf(2, 1), resumirListas(state.listas, emptyList()).map { it.lista.id })

        instante = 30L
        state.registrarAlteracao(1)

        assertEquals(listOf(1, 2), resumirListas(state.listas, emptyList()).map { it.lista.id })
        assertEquals(listOf(30L, 20L), state.listas.map { it.atualizadaEm })
    }

    @Test
    fun `renomear lista tambem atualiza sua posicao`() {
        var instante = 10L
        val state = ListaAppState(agora = { instante })
        state.alterarNome("Casa")
        state.criarLista()
        instante = 20L
        state.alterarNome("Festa")
        state.criarLista()

        val lista = state.listas.first()
        state.iniciarEdicao(lista)
        state.alterarNomeEditado("Compras de casa")
        instante = 30L
        state.salvarEdicao(lista)

        val primeira = resumirListas(state.listas, emptyList()).first().lista
        assertEquals(1, primeira.id)
        assertEquals("Compras de casa", primeira.nome)
        assertEquals(30L, primeira.atualizadaEm)
    }

    @Test
    fun `registrar alteracao de lista inexistente nao cria nem modifica listas`() {
        var instante = 10L
        val state = ListaAppState(agora = { instante })
        state.alterarNome("Casa")
        state.criarLista()
        val antes = state.listas

        instante = 20L
        state.registrarAlteracao(2)

        assertEquals(antes, state.listas)
    }
}
