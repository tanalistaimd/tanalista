package br.ufrn.tanalista.presentation.mercado

import br.ufrn.tanalista.domain.RepositorioListas
import br.ufrn.tanalista.domain.model.Compra
import br.ufrn.tanalista.domain.model.ItemLista
import br.ufrn.tanalista.domain.model.ListaCompra
import br.ufrn.tanalista.domain.model.RegistroCompra
import br.ufrn.tanalista.presentation.EstadoTela
import br.ufrn.tanalista.presentation.TelaComRepositorioViewModel
import br.ufrn.tanalista.presentation.conteudoDisponivel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

internal class MercadoViewModel(
    private val listaId: Int,
    repositorio: RepositorioListas,
) : TelaComRepositorioViewModel(repositorio) {
    private val formulario = MutableStateFlow<EdicaoRegistro?>(null)

    val estado: StateFlow<EstadoTela<ConteudoMercado>> =
        observar(formulario) { dados, edicao, aviso ->
            val lista =
                dados.listas.find { it.id == listaId }
                    ?: return@observar EstadoTela.Erro("Lista não encontrada")
            val itens = dados.itensDaLista(listaId)
            val compra = (dados.compras[listaId] ?: Compra(listaId)).limitarA(itens)
            val carrinho =
                itens.map { item ->
                    val registro = compra.registroDoItem(item)
                    ItemNoCarrinho(item, registro, registro?.subtotalCentavos)
                }
            val conteudo = ConteudoMercado(lista, carrinho, compra.totalCentavos, edicao, aviso)
            conteudoDisponivel(conteudo, vazio = itens.isEmpty())
        }

    fun iniciarEdicao(item: ItemLista) {
        limparAviso()
        val registro =
            repositorio.dados.value.compras[listaId]
                ?.registroDoItem(item)
        formulario.value =
            EdicaoRegistro(
                item = item,
                jaComprado = registro != null,
                preco =
                    registro
                        ?.let {
                            formatarPreco(
                                it.precoUnitarioCentavos.toLong(),
                            ).removePrefix("R$ ")
                        }.orEmpty(),
                quantidade =
                    registro?.let { formatarQuantidade(it.quantidadeMilesimos) }
                        ?: formatarQuantidadePlanejada(item.quantidade)
                        ?: "1",
            )
    }

    fun alterarPreco(preco: String) = formulario.update { it?.copy(preco = preco) }

    fun alterarQuantidade(quantidade: String) = formulario.update { it?.copy(quantidade = quantidade) }

    fun salvarRegistro() {
        val edicao = formulario.value ?: return
        val registro = edicao.registro ?: return
        gravar {
            repositorio.gravarRegistro(edicao.item, registro)
            formulario.compareAndSet(edicao, null)
        }
    }

    fun cancelarEdicao() {
        formulario.value = null
    }

    fun desmarcar(item: ItemLista) = gravar { repositorio.gravarRegistro(item, null) }
}

internal data class ConteudoMercado(
    val lista: ListaCompra,
    val carrinho: List<ItemNoCarrinho>,
    val totalCentavos: Long,
    val edicao: EdicaoRegistro?,
    val aviso: String?,
)

internal data class ItemNoCarrinho(
    val item: ItemLista,
    val registro: RegistroCompra?,
    val subtotalCentavos: Long?,
)

internal data class EdicaoRegistro(
    val item: ItemLista,
    val jaComprado: Boolean,
    val preco: String,
    val quantidade: String,
) {
    private val precoCentavos = interpretarDecimal(preco, casas = CASAS_PRECO)
    private val quantidadeMilesimos = interpretarDecimal(quantidade, casas = CASAS_QUANTIDADE)?.takeIf { it > 0 }

    val precoInvalido: Boolean = preco.isNotBlank() && precoCentavos == null
    val quantidadeInvalida: Boolean = quantidade.isNotBlank() && quantidadeMilesimos == null
    val registro: RegistroCompra? =
        if (precoCentavos != null && quantidadeMilesimos != null) {
            RegistroCompra(precoCentavos, quantidadeMilesimos)
        } else {
            null
        }
    val podeSalvar: Boolean = registro != null
}
