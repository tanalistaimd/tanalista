package br.ufrn.tanalista.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufrn.tanalista.domain.DadosListas
import br.ufrn.tanalista.domain.FalhaRepositorio
import br.ufrn.tanalista.domain.MotivoFalha
import br.ufrn.tanalista.domain.RepositorioListas
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal abstract class TelaComRepositorioViewModel(
    protected val repositorio: RepositorioListas,
) : ViewModel() {
    private val carga = MutableStateFlow<Carga>(Carga.Carregando)
    private val aviso = MutableStateFlow<String?>(null)
    private val gravacoes = Mutex()
    private var tarefaDeCarga: Job? = null

    init {
        tentarDeNovo()
    }

    fun tentarDeNovo() {
        if (tarefaDeCarga?.isActive == true) return
        carga.value = Carga.Carregando
        aviso.value = null
        tarefaDeCarga =
            viewModelScope.launch {
                carga.value =
                    try {
                        repositorio.carregar()
                        Carga.Disponivel
                    } catch (falha: FalhaRepositorio) {
                        Carga.Erro(falha.motivo.mensagem())
                    }
            }
    }

    protected fun <Campos, Conteudo> observar(
        campos: StateFlow<Campos>,
        montar: (DadosListas, Campos, String?) -> EstadoTela<Conteudo>,
    ): StateFlow<EstadoTela<Conteudo>> =
        combine(carga, repositorio.dados, campos, aviso) { estado, dados, valores, mensagem ->
            when (estado) {
                Carga.Carregando -> EstadoTela.Carregando
                is Carga.Erro -> EstadoTela.Erro(estado.mensagem)
                Carga.Disponivel -> montar(dados, valores, mensagem)
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, EstadoTela.Carregando)

    protected fun gravar(
        aposGravar: suspend () -> Unit = {},
        acao: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            val gravou =
                gravacoes.withLock {
                    aviso.value = null
                    try {
                        acao()
                        true
                    } catch (falha: FalhaRepositorio) {
                        aviso.value = falha.motivo.mensagem()
                        false
                    }
                }
            if (gravou) aposGravar()
        }
    }

    protected fun limparAviso() {
        aviso.value = null
    }
}

private sealed interface Carga {
    data object Carregando : Carga

    data object Disponivel : Carga

    data class Erro(
        val mensagem: String,
    ) : Carga
}

private fun MotivoFalha.mensagem(): String =
    when (this) {
        MotivoFalha.SEM_CONEXAO -> "Sem conexão. Confira a rede e tente de novo."
        MotivoFalha.RESPOSTA_INVALIDA -> "O servidor retornou uma resposta inválida."
        MotivoFalha.LISTA_INEXISTENTE -> "Lista não encontrada"
        MotivoFalha.LISTA_INVALIDA -> "O nome da lista é inválido."
        MotivoFalha.LISTA_DUPLICADA -> "Já existe uma lista com esse nome."
        MotivoFalha.ITEM_INEXISTENTE -> "Item não encontrado."
        MotivoFalha.ITEM_MODIFICADO -> "O item mudou. Abra a edição novamente para continuar."
        MotivoFalha.ITEM_INVALIDO -> "Confira o nome e a quantidade do item."
        MotivoFalha.ITEM_DUPLICADO -> "Já existe um item com esse nome na lista."
    }
