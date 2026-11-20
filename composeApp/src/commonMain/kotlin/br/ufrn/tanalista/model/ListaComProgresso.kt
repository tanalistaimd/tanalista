package br.ufrn.tanalista.model

/** Resumo da compra em andamento, sem alterar os itens da lista reutilizável. */
data class ProgressoCompra(
    val itensComprados: Int,
    val totalItens: Int,
)

data class ListaComProgresso(
    val lista: ListaCompra,
    val progresso: ProgressoCompra,
)

fun resumirListas(
    listas: List<ListaCompra>,
    itens: List<ItemLista>,
    compras: Map<Int, Compra> = emptyMap(),
): List<ListaComProgresso> {
    val itensPorLista = itens.groupBy { it.listaId }
    return ordenarPorAtualizacao(
        listas.map { lista ->
            val itensDaLista = itensPorLista[lista.id].orEmpty()
            val comprados = compras[lista.id]?.limitarA(itensDaLista)?.itensComprados ?: 0
            ListaComProgresso(lista, ProgressoCompra(itensComprados = comprados, totalItens = itensDaLista.size))
        },
    )
}

/** Exibe a lista atualizada mais recentemente primeiro, inclusive em caso de empate no relógio. */
fun ordenarPorAtualizacao(listas: List<ListaComProgresso>): List<ListaComProgresso> =
    listas.sortedWith(
        compareByDescending<ListaComProgresso> { it.lista.atualizadaEm }
            .thenByDescending { it.lista.id },
    )
