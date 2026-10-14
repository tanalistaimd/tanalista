package br.ufrn.tanalista.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import br.ufrn.tanalista.model.ItemLista

/** Itens de todas as listas, em memória durante a Sprint 1. */
class ItensState {
    var itens by mutableStateOf(emptyList<ItemLista>())
        private set

    fun daLista(listaId: Int): List<ItemLista> = itens.filter { it.listaId == listaId }

    /** Devolve `false`, sem alterar nada, quando o formulário é inválido. */
    fun adicionar(
        listaId: Int,
        formulario: FormularioItem,
    ): Boolean {
        val podeAdicionar = formulario.podeAdicionar(listaId, itens)

        if (podeAdicionar) {
            itens = itens +
                ItemLista(
                    id = (itens.maxOfOrNull { it.id } ?: 0) + 1,
                    listaId = listaId,
                    nome = formulario.nome.trim(),
                    quantidade = formulario.quantidadeInformada,
                    unidade = formulario.unidade,
                    categoria = formulario.categoria,
                )
        }

        return podeAdicionar
    }

    fun removerDaLista(listaId: Int) {
        itens = itens.filterNot { it.listaId == listaId }
    }
}
