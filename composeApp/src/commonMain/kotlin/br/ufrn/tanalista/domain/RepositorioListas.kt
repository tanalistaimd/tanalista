package br.ufrn.tanalista.domain

import br.ufrn.tanalista.domain.model.Categoria
import br.ufrn.tanalista.domain.model.Compra
import br.ufrn.tanalista.domain.model.ItemLista
import br.ufrn.tanalista.domain.model.ListaCompra
import br.ufrn.tanalista.domain.model.RegistroCompra
import br.ufrn.tanalista.domain.model.Unidade
import kotlinx.coroutines.flow.StateFlow

data class DadosListas(
    val listas: List<ListaCompra> = emptyList(),
    val itens: List<ItemLista> = emptyList(),
    val compras: Map<Int, Compra> = emptyMap(),
) {
    fun itensDaLista(listaId: Int): List<ItemLista> = itens.filter { it.listaId == listaId }
}

data class DadosItem(
    val nome: String,
    val quantidade: Double? = null,
    val unidade: Unidade? = null,
    val categoria: Categoria? = null,
)

/** Fonte compartilhada. Cada escrita bem-sucedida publica os dados atualizados em [dados]. */
interface RepositorioListas {
    val dados: StateFlow<DadosListas>

    suspend fun carregar()

    suspend fun criarLista(nome: String)

    suspend fun renomearLista(
        listaId: Int,
        nome: String,
    )

    suspend fun excluirLista(listaId: Int)

    suspend fun adicionarItem(
        listaId: Int,
        item: DadosItem,
    )

    /** Confere se [item] ainda é a versão atual antes de aplicar a edição. */
    suspend fun editarItem(
        item: ItemLista,
        novosDados: DadosItem,
    )

    suspend fun removerItem(item: ItemLista)

    suspend fun restaurarItem(item: ItemLista)

    /** Um registro nulo desmarca o item. A gravação usa os dados atuais de [item]. */
    suspend fun gravarRegistro(
        item: ItemLista,
        registro: RegistroCompra?,
    )
}
