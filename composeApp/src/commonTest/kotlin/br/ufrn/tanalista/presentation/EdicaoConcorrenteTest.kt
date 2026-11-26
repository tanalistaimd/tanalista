package br.ufrn.tanalista.presentation

import androidx.lifecycle.viewModelScope
import br.ufrn.tanalista.data.RepositorioListasEmMemoria
import br.ufrn.tanalista.domain.DadosItem
import br.ufrn.tanalista.domain.model.Unidade
import br.ufrn.tanalista.presentation.item.ConteudoDetalhe
import br.ufrn.tanalista.presentation.item.FormularioItem
import br.ufrn.tanalista.presentation.item.ListaDetalheViewModel
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EdicaoConcorrenteTest {
    @Test
    fun `rascunho de item nao sobrescreve alteracao de outro destino`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            val repositorio = repositorioComItem()
            val viewModel = ListaDetalheViewModel(1, repositorio)
            try {
                val item =
                    repositorio.dados.value.itens
                        .single()
                viewModel.iniciarEdicao(item)
                viewModel.alterarEdicao(FormularioItem(nome = "Bebida", quantidade = "1", unidade = Unidade.LITRO))

                repositorio.editarItem(item, DadosItem(nome = "Leite", quantidade = 2.0, unidade = Unidade.LITRO))
                viewModel.salvarEdicao()
                runCurrent()

                val conteudo = assertIs<EstadoTela.Sucesso<ConteudoDetalhe>>(viewModel.estado.value).conteudo
                assertEquals(
                    item.copy(quantidade = 2.0),
                    repositorio.dados.value.itens
                        .single(),
                )
                assertNotNull(conteudo.aviso)
            } finally {
                viewModel.viewModelScope.cancel()
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `rascunho de compra nao salva preco baseado na unidade antiga`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            val repositorio = repositorioComItem()
            val viewModel = MercadoViewModel(1, repositorio)
            try {
                val item =
                    repositorio.dados.value.itens
                        .single()
                viewModel.iniciarEdicao(item)
                viewModel.alterarPreco("6,50")

                repositorio.editarItem(item, DadosItem(nome = "Leite", quantidade = 1.0, unidade = Unidade.UNIDADE))
                viewModel.salvarRegistro()
                runCurrent()

                val conteudo = assertIs<EstadoTela.Sucesso<ConteudoMercado>>(viewModel.estado.value).conteudo
                assertTrue(
                    repositorio.dados.value.compras
                        .isEmpty(),
                )
                assertNotNull(conteudo.aviso)
            } finally {
                viewModel.viewModelScope.cancel()
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `fila de avisos de remocao nao impede novas gravacoes`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            val repositorio = repositorioComItem()
            val viewModel = ListaDetalheViewModel(1, repositorio)
            try {
                repeat(70) { numero -> repositorio.adicionarItem(1, DadosItem(nome = "Item $numero")) }
                repositorio.dados.value.itens
                    .forEach(viewModel::removerItem)
                runCurrent()

                viewModel.alterarFormulario(FormularioItem(nome = "Arroz"))
                viewModel.adicionarItem()
                runCurrent()

                assertEquals(
                    listOf("Arroz"),
                    repositorio.dados.value.itens
                        .map { it.nome },
                )
            } finally {
                viewModel.viewModelScope.cancel()
                Dispatchers.resetMain()
            }
        }

    private suspend fun repositorioComItem(): RepositorioListasEmMemoria =
        RepositorioListasEmMemoria().also { repositorio ->
            repositorio.criarLista("Casa")
            repositorio.adicionarItem(1, DadosItem(nome = "Leite", quantidade = 1.0, unidade = Unidade.LITRO))
        }
}
