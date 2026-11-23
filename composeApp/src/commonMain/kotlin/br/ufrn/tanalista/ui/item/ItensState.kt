package br.ufrn.tanalista.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import br.ufrn.tanalista.model.ItemLista
import br.ufrn.tanalista.validation.ItemListaValidator

/** Itens de todas as listas, em memória durante a Sprint 1. */
class ItensState {
    private var proximoId = 1

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
                    id = proximoId++,
                    listaId = listaId,
                    nome = formulario.nome.trim(),
                    quantidade = formulario.quantidadeInformada,
                    unidade = formulario.unidade,
                    categoria = formulario.categoria,
                )
        }

        return podeAdicionar
    }

    fun editar(
        item: ItemLista,
        formulario: FormularioItem,
    ): Boolean {
        val atual = itens.find { it.id == item.id && it.listaId == item.listaId }
        if (atual == null || !formulario.podeAdicionar(atual.listaId, itens, ignorarItemId = atual.id)) {
            return false
        }

        val atualizado =
            atual.copy(
                nome = formulario.nome.trim(),
                quantidade = formulario.quantidadeInformada,
                unidade = formulario.unidade,
                categoria = formulario.categoria,
            )
        itens = itens.map { if (it.id == atual.id) atualizado else it }
        return true
    }

    fun remover(item: ItemLista): Boolean {
        val restantes = itens.filterNot { it.id == item.id && it.listaId == item.listaId }
        if (restantes.size == itens.size) return false

        itens = restantes
        return true
    }

    fun restaurar(item: ItemLista): Boolean {
        if (itens.any { it.id == item.id } || ItemListaValidator.nomeJaExiste(item.nome, item.listaId, itens)) {
            return false
        }

        itens = (itens + item).sortedBy { it.id }
        return true
    }

    fun removerDaLista(listaId: Int) {
        itens = itens.filterNot { it.listaId == listaId }
    }
}
