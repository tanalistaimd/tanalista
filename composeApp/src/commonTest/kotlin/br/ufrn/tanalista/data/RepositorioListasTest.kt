package br.ufrn.tanalista.data

import br.ufrn.tanalista.domain.DadosItem
import br.ufrn.tanalista.domain.FalhaRepositorio
import br.ufrn.tanalista.domain.MotivoFalha
import br.ufrn.tanalista.domain.model.resumirListas
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RepositorioListasTest {
    @Test
    fun `alteracao dos itens faz a lista antiga subir sem alterar as demais`() =
        runTest {
            var instante = 10L
            val repositorio = RepositorioListasEmMemoria(agora = { instante })
            repositorio.criarLista("Casa")
            instante = 20L
            repositorio.criarLista("Festa")
            val antes = repositorio.dados.value
            assertEquals(listOf(2, 1), resumirListas(antes.listas, antes.itens).map { it.lista.id })

            instante = 30L
            repositorio.adicionarItem(1, DadosItem(nome = "Leite"))

            val depois = repositorio.dados.value
            assertEquals(listOf(1, 2), resumirListas(depois.listas, depois.itens).map { it.lista.id })
            assertEquals(listOf(30L, 20L), depois.listas.map { it.atualizadaEm })
        }

    @Test
    fun `renomear lista tambem atualiza sua posicao`() =
        runTest {
            var instante = 10L
            val repositorio = RepositorioListasEmMemoria(agora = { instante })
            repositorio.criarLista("Casa")
            instante = 20L
            repositorio.criarLista("Festa")

            instante = 30L
            repositorio.renomearLista(1, "Compras de casa")

            val primeira = resumirListas(repositorio.dados.value.listas, emptyList()).first().lista
            assertEquals(1, primeira.id)
            assertEquals("Compras de casa", primeira.nome)
            assertEquals(30L, primeira.atualizadaEm)
        }

    @Test
    fun `registrar alteracao de lista inexistente nao cria nem modifica listas`() =
        runTest {
            val repositorio = RepositorioListasEmMemoria()
            repositorio.criarLista("Casa")
            val antes = repositorio.dados.value

            val falha =
                assertFailsWith<FalhaRepositorio> {
                    repositorio.adicionarItem(2, DadosItem(nome = "Leite"))
                }

            assertEquals(MotivoFalha.LISTA_INEXISTENTE, falha.motivo)
            assertEquals(antes, repositorio.dados.value)
        }
}
