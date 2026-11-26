package br.ufrn.tanalista.data

import br.ufrn.tanalista.domain.DadosItem
import br.ufrn.tanalista.domain.FalhaRepositorio
import br.ufrn.tanalista.domain.model.ProgressoCompra
import br.ufrn.tanalista.domain.model.resumirListas
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ItensProgressoTest {
    @Test
    fun `inclusao valida muda o total somente da lista correspondente`() =
        runTest {
            val repositorio = RepositorioListasEmMemoria()
            repositorio.criarLista("Casa")
            repositorio.criarLista("Festa")
            repositorio.adicionarItem(1, DadosItem(nome = "Leite"))
            repositorio.adicionarItem(1, DadosItem(nome = "Pão"))
            repositorio.adicionarItem(2, DadosItem(nome = "Suco"))

            val dados = repositorio.dados.value
            val porId = resumirListas(dados.listas, dados.itens).associateBy { it.lista.id }

            assertEquals(ProgressoCompra(0, 2), porId.getValue(1).progresso)
            assertEquals(ProgressoCompra(0, 1), porId.getValue(2).progresso)
        }

    @Test
    fun `inclusao invalida ou duplicada nao altera o total`() =
        runTest {
            val repositorio = RepositorioListasEmMemoria()
            repositorio.criarLista("Casa")
            repositorio.adicionarItem(1, DadosItem(nome = "Leite"))
            assertFailsWith<FalhaRepositorio> { repositorio.adicionarItem(1, DadosItem(nome = " LEITE ")) }
            assertFailsWith<FalhaRepositorio> {
                repositorio.adicionarItem(1, DadosItem(nome = "Pão", quantidade = 0.0))
            }

            val dados = repositorio.dados.value
            assertEquals(ProgressoCompra(0, 1), resumirListas(dados.listas, dados.itens).single().progresso)
        }

    @Test
    fun `exclusao dos itens de uma lista nao afeta o progresso das demais`() =
        runTest {
            val repositorio = RepositorioListasEmMemoria()
            repositorio.criarLista("Casa")
            repositorio.criarLista("Festa")
            repositorio.adicionarItem(1, DadosItem(nome = "Leite"))
            repositorio.adicionarItem(2, DadosItem(nome = "Suco"))

            repositorio.excluirLista(1)

            val dados = repositorio.dados.value
            val restante = resumirListas(dados.listas, dados.itens).single()
            assertEquals(2, restante.lista.id)
            assertEquals(ProgressoCompra(0, 1), restante.progresso)
            assertEquals(listOf("Suco"), dados.itens.map { it.nome })
        }
}
