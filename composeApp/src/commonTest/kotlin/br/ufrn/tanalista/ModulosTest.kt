package br.ufrn.tanalista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufrn.tanalista.domain.DadosItem
import br.ufrn.tanalista.domain.RepositorioListas
import br.ufrn.tanalista.presentation.EstadoTela
import br.ufrn.tanalista.presentation.item.ConteudoDetalhe
import br.ufrn.tanalista.presentation.item.ListaDetalheViewModel
import br.ufrn.tanalista.presentation.lista.ConteudoListas
import br.ufrn.tanalista.presentation.lista.ListasViewModel
import br.ufrn.tanalista.presentation.mercado.ConteudoMercado
import br.ufrn.tanalista.presentation.mercado.MercadoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class ModulosTest {
    @Test
    fun `injeta o mesmo repositorio e respeita o id de cada destino`() =
        runTest {
            val aplicacao = koinApplication { modules(moduloTaNaLista) }
            val viewModels = mutableListOf<ViewModel>()
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val listas = aplicacao.koin.get<ListasViewModel>().also { viewModels.add(it) }
                val detalhe = aplicacao.koin.get<ListaDetalheViewModel> { parametersOf(1) }.also { viewModels.add(it) }
                val mercado = aplicacao.koin.get<MercadoViewModel> { parametersOf(2) }.also { viewModels.add(it) }
                val repositorio = aplicacao.koin.get<RepositorioListas>()
                repositorio.criarLista("Casa")
                repositorio.criarLista("Festa")
                repositorio.adicionarItem(1, DadosItem(nome = "Leite"))
                repositorio.adicionarItem(2, DadosItem(nome = "Suco"))
                runCurrent()

                val resumos = assertIs<EstadoTela.Sucesso<ConteudoListas>>(listas.estado.value).conteudo.listas
                assertEquals(setOf("Casa", "Festa"), resumos.map { it.lista.nome }.toSet())
                val itens = assertIs<EstadoTela.Sucesso<ConteudoDetalhe>>(detalhe.estado.value).conteudo.itens
                assertEquals("Leite", itens.single().nome)
                val carrinho = assertIs<EstadoTela.Sucesso<ConteudoMercado>>(mercado.estado.value).conteudo.carrinho
                assertEquals("Suco", carrinho.single().item.nome)
            } finally {
                viewModels.forEach { it.viewModelScope.cancel() }
                aplicacao.close()
                Dispatchers.resetMain()
            }
        }
}
