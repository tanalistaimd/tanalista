package br.ufrn.tanalista.presentation.item

import br.ufrn.tanalista.domain.DadosItem
import br.ufrn.tanalista.domain.RepositorioListas
import br.ufrn.tanalista.domain.model.ItemLista
import br.ufrn.tanalista.domain.model.ListaCompra
import br.ufrn.tanalista.presentation.EstadoTela
import br.ufrn.tanalista.presentation.TelaComRepositorioViewModel
import br.ufrn.tanalista.presentation.conteudoDisponivel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

internal class ListaDetalheViewModel(
    private val listaId: Int,
    repositorio: RepositorioListas,
) : TelaComRepositorioViewModel(repositorio) {
    private val campos = MutableStateFlow(CamposDetalhe())
    private val remocoes = Channel<ItemLista>(Channel.BUFFERED)

    val itensRemovidos = remocoes.receiveAsFlow()
    val estado: StateFlow<EstadoTela<ConteudoDetalhe>> =
        observar(campos) { dados, valores, aviso ->
            val lista =
                dados.listas.find { it.id == listaId }
                    ?: return@observar EstadoTela.Erro("Lista não encontrada")
            val itens = dados.itensDaLista(listaId)
            val edicao =
                valores.edicao?.let { atual ->
                    EdicaoItem(atual.item, atual.formulario.validar(listaId, itens, atual.item.id))
                }
            val conteudo = ConteudoDetalhe(lista, itens, valores.formulario.validar(listaId, itens), edicao, aviso)
            conteudoDisponivel(conteudo, vazio = itens.isEmpty())
        }

    fun alterarFormulario(formulario: FormularioItem) = campos.update { it.copy(formulario = formulario) }

    fun adicionarItem() {
        val formulario = campos.value.formulario
        if (!formulario.podeAdicionar(listaId, repositorio.dados.value.itensDaLista(listaId))) return
        gravar {
            repositorio.adicionarItem(listaId, formulario.dadosItem())
            campos.update { if (it.formulario == formulario) it.copy(formulario = FormularioItem()) else it }
        }
    }

    fun iniciarEdicao(item: ItemLista) {
        limparAviso()
        val formulario =
            FormularioItem(
                nome = item.nome,
                quantidade =
                    item.quantidade
                        ?.toString()
                        ?.replace('.', ',')
                        .orEmpty(),
                unidade = item.unidade,
                categoria = item.categoria,
            )
        campos.update { it.copy(edicao = RascunhoItem(item, formulario)) }
    }

    fun alterarEdicao(formulario: FormularioItem) =
        campos.update { it.copy(edicao = it.edicao?.copy(formulario = formulario)) }

    fun salvarEdicao() {
        val edicao = campos.value.edicao ?: return
        val itens = repositorio.dados.value.itensDaLista(listaId)
        if (!edicao.formulario.podeAdicionar(listaId, itens, edicao.item.id)) return
        gravar {
            repositorio.editarItem(edicao.item, edicao.formulario.dadosItem())
            campos.update { if (it.edicao == edicao) it.copy(edicao = null) else it }
        }
    }

    fun cancelarEdicao() = campos.update { it.copy(edicao = null) }

    fun removerItem(item: ItemLista) =
        gravar(aposGravar = { remocoes.send(item) }) {
            repositorio.removerItem(item)
        }

    fun restaurarItem(item: ItemLista) = gravar { repositorio.restaurarItem(item) }
}

private data class CamposDetalhe(
    val formulario: FormularioItem = FormularioItem(),
    val edicao: RascunhoItem? = null,
)

private data class RascunhoItem(
    val item: ItemLista,
    val formulario: FormularioItem,
)

internal data class ConteudoDetalhe(
    val lista: ListaCompra,
    val itens: List<ItemLista>,
    val novoItem: FormularioItemValidado,
    val edicao: EdicaoItem? = null,
    val aviso: String? = null,
)

internal data class EdicaoItem(
    val item: ItemLista,
    val rascunho: FormularioItemValidado,
)

internal data class FormularioItemValidado(
    val formulario: FormularioItem,
    val erroNome: String?,
    val erroQuantidade: String?,
    val podeSalvar: Boolean,
)

internal fun FormularioItem.validar(
    listaId: Int,
    itens: List<ItemLista>,
    ignorarItemId: Int? = null,
): FormularioItemValidado =
    FormularioItemValidado(
        formulario = this,
        erroNome = erroNome(listaId, itens, ignorarItemId),
        erroQuantidade = erroQuantidade(),
        podeSalvar = podeAdicionar(listaId, itens, ignorarItemId),
    )

private fun FormularioItem.dadosItem(): DadosItem = DadosItem(nome, quantidadeInformada, unidade, categoria)
