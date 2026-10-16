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

/** A marcação de compra será conectada na #14; por enquanto, só o total vem dos itens reais. */
fun resumirListas(
    listas: List<ListaCompra>,
    itens: List<ItemLista>,
): List<ListaComProgresso> {
    val totais = itens.groupingBy { it.listaId }.eachCount()
    return ordenarPorAtualizacao(
        listas.map { lista ->
            ListaComProgresso(lista, ProgressoCompra(itensComprados = 0, totalItens = totais[lista.id] ?: 0))
        },
    )
}

/** Exibe a lista atualizada mais recentemente primeiro, inclusive em caso de empate no relógio. */
fun ordenarPorAtualizacao(listas: List<ListaComProgresso>): List<ListaComProgresso> =
    listas.sortedWith(
        compareByDescending<ListaComProgresso> { it.lista.atualizadaEm }
            .thenByDescending { it.lista.id },
    )
