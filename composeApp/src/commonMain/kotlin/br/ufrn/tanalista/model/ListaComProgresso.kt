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

/** Exibe a lista atualizada mais recentemente primeiro, inclusive em caso de empate no relógio. */
fun ordenarPorAtualizacao(listas: List<ListaComProgresso>): List<ListaComProgresso> =
    listas.sortedWith(
        compareByDescending<ListaComProgresso> { it.lista.atualizadaEm }
            .thenByDescending { it.lista.id },
    )
