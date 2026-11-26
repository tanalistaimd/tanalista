package br.ufrn.tanalista.presentation

import androidx.lifecycle.viewModelScope
import br.ufrn.tanalista.data.RepositorioListasEmMemoria
import br.ufrn.tanalista.domain.FalhaRepositorio
import br.ufrn.tanalista.domain.MotivoFalha
import br.ufrn.tanalista.domain.RepositorioListas
import br.ufrn.tanalista.presentation.lista.ConteudoListas
import br.ufrn.tanalista.presentation.lista.ListasViewModel
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

@OptIn(ExperimentalCoroutinesApi::class)
class EstadoDasListasTest {
    @Test
    fun `retenta uma carga com erro e reflete novas listas do repositorio`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            val memoria = RepositorioListasEmMemoria()
            val repositorio = RepositorioComFalhaInicial(memoria)
            val viewModel = ListasViewModel(repositorio)
            try {
                assertIs<EstadoTela.Carregando>(viewModel.estado.value)
                runCurrent()
                assertIs<EstadoTela.Erro>(viewModel.estado.value)

                viewModel.tentarDeNovo()
                runCurrent()
                assertIs<EstadoTela.Vazio<*>>(viewModel.estado.value)

                memoria.criarLista("Compras do mês")
                runCurrent()
                val conteudo = assertIs<EstadoTela.Sucesso<ConteudoListas>>(viewModel.estado.value).conteudo
                assertEquals(
                    "Compras do mês",
                    conteudo.listas
                        .single()
                        .lista.nome,
                )
            } finally {
                viewModel.viewModelScope.cancel()
                Dispatchers.resetMain()
            }
        }
}

private class RepositorioComFalhaInicial(
    memoria: RepositorioListas,
) : RepositorioListas by memoria {
    private var falhou = false

    override suspend fun carregar() {
        if (!falhou) {
            falhou = true
            throw FalhaRepositorio(MotivoFalha.SEM_CONEXAO)
        }
    }
}
