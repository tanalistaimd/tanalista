package br.ufrn.tanalista.data

import br.ufrn.tanalista.domain.DadosItem
import br.ufrn.tanalista.domain.DadosListas
import br.ufrn.tanalista.domain.FalhaRepositorio
import br.ufrn.tanalista.domain.MotivoFalha
import br.ufrn.tanalista.domain.RepositorioListas
import br.ufrn.tanalista.domain.model.Compra
import br.ufrn.tanalista.domain.model.ItemLista
import br.ufrn.tanalista.domain.model.ListaCompra
import br.ufrn.tanalista.domain.model.RegistroCompra
import br.ufrn.tanalista.domain.validation.ItemListaValidator
import br.ufrn.tanalista.domain.validation.ListaCompraValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock

/** Implementação local usada pelo app enquanto as fontes de rede e persistência são integradas. */
internal class RepositorioListasEmMemoria(
    private val agora: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) : RepositorioListas {
    private val mutex = Mutex()
    private val dadosMutaveis = MutableStateFlow(DadosListas())
    private var proximoIdLista = 1
    private var proximoIdItem = 1

    override val dados: StateFlow<DadosListas> = dadosMutaveis.asStateFlow()

    override suspend fun carregar() = Unit

    override suspend fun criarLista(nome: String) =
        alterar { atuais ->
            validarNomeLista(nome, atuais.listas)
            val lista = ListaCompra(id = proximoIdLista++, nome = nome.trim(), atualizadaEm = agora())
            atuais.copy(listas = atuais.listas + lista)
        }

    override suspend fun renomearLista(
        listaId: Int,
        nome: String,
    ) = alterar { atuais ->
        val lista = atuais.listaComId(listaId)
        validarNomeLista(nome, atuais.listas.filterNot { it.id == listaId })
        val atualizada = lista.copy(nome = nome.trim(), atualizadaEm = agora())
        atuais.copy(listas = atuais.listas.map { if (it.id == listaId) atualizada else it })
    }

    override suspend fun excluirLista(listaId: Int) =
        alterar { atuais ->
            atuais.listaComId(listaId)
            atuais.copy(
                listas = atuais.listas.filterNot { it.id == listaId },
                itens = atuais.itens.filterNot { it.listaId == listaId },
                compras = atuais.compras - listaId,
            )
        }

    override suspend fun adicionarItem(
        listaId: Int,
        item: DadosItem,
    ) = alterar { atuais ->
        atuais.listaComId(listaId)
        validarItem(item, listaId, atuais.itens)
        atuais.comItens(listaId, atuais.itens + item.comId(proximoIdItem++, listaId), agora())
    }

    override suspend fun editarItem(
        item: ItemLista,
        novosDados: DadosItem,
    ) = alterar { atuais ->
        atuais.verificarItemAtual(item)
        validarItem(novosDados, item.listaId, atuais.itens, ignorarItemId = item.id)
        val atualizado = novosDados.comId(item.id, item.listaId)
        val itens = atuais.itens.map { if (it.id == item.id) atualizado else it }
        atuais.comItens(item.listaId, itens, agora())
    }

    override suspend fun removerItem(item: ItemLista) =
        alterar { atuais ->
            atuais.verificarItemAtual(item)
            atuais.comItens(item.listaId, atuais.itens.filterNot { it.id == item.id }, agora())
        }

    override suspend fun restaurarItem(item: ItemLista) =
        alterar { atuais ->
            atuais.listaComId(item.listaId)
            if (atuais.itens.any { it.id == item.id } ||
                ItemListaValidator.nomeJaExiste(item.nome, item.listaId, atuais.itens)
            ) {
                throw FalhaRepositorio(MotivoFalha.ITEM_DUPLICADO)
            }
            atuais.comItens(item.listaId, (atuais.itens + item).sortedBy { it.id }, agora())
        }

    override suspend fun gravarRegistro(
        item: ItemLista,
        registro: RegistroCompra?,
    ) = alterar { atuais ->
        atuais.verificarItemAtual(item)
        val listaId = item.listaId
        val compra = atuais.compras[listaId] ?: Compra(listaId)
        val atualizada = if (registro == null) compra.desmarcar(item) else compra.marcarComprado(item, registro)
        atuais.copy(compras = atuais.compras + (listaId to atualizada))
    }

    private suspend fun alterar(bloco: (DadosListas) -> DadosListas) {
        mutex.withLock { dadosMutaveis.value = bloco(dadosMutaveis.value) }
    }
}

private fun DadosListas.listaComId(listaId: Int): ListaCompra =
    listas.find { it.id == listaId } ?: throw FalhaRepositorio(MotivoFalha.LISTA_INEXISTENTE)

private fun DadosListas.verificarItemAtual(item: ItemLista) {
    val atual =
        itens.find { it.listaId == item.listaId && it.id == item.id }
            ?: throw FalhaRepositorio(MotivoFalha.ITEM_INEXISTENTE)
    if (atual != item) throw FalhaRepositorio(MotivoFalha.ITEM_MODIFICADO)
}

private fun DadosListas.comItens(
    listaId: Int,
    itens: List<ItemLista>,
    instante: Long,
): DadosListas =
    copy(
        itens = itens,
        listas = listas.map { if (it.id == listaId) it.copy(atualizadaEm = instante) else it },
    )

private fun DadosItem.comId(
    itemId: Int,
    listaId: Int,
): ItemLista = ItemLista(itemId, listaId, nome.trim(), quantidade, unidade, categoria)

private fun validarNomeLista(
    nome: String,
    listas: List<ListaCompra>,
) {
    if (!ListaCompraValidator.nomeEhValido(nome)) throw FalhaRepositorio(MotivoFalha.LISTA_INVALIDA)
    if (ListaCompraValidator.nomeJaExiste(nome, listas)) throw FalhaRepositorio(MotivoFalha.LISTA_DUPLICADA)
}

private fun validarItem(
    item: DadosItem,
    listaId: Int,
    itens: List<ItemLista>,
    ignorarItemId: Int? = null,
) {
    if (!ItemListaValidator.nomeEhValido(item.nome) ||
        !ItemListaValidator.quantidadeEhValida(item.quantidade) ||
        item.quantidade?.isFinite() == false
    ) {
        throw FalhaRepositorio(MotivoFalha.ITEM_INVALIDO)
    }
    if (ItemListaValidator.nomeJaExiste(item.nome, listaId, itens, ignorarItemId)) {
        throw FalhaRepositorio(MotivoFalha.ITEM_DUPLICADO)
    }
}
