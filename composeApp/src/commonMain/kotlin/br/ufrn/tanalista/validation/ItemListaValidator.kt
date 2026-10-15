package br.ufrn.tanalista.validation

import br.ufrn.tanalista.model.ItemLista

object ItemListaValidator {
    const val LIMITE_NOME = 60

    fun nomeEhValido(nome: String): Boolean = nome.isNotBlank() && nome.trim().length <= LIMITE_NOME

    fun quantidadeEhValida(quantidade: Double?): Boolean = quantidade == null || quantidade > 0

    /** [ignorarItemId] exclui da comparação o próprio item que está sendo editado. */
    fun nomeJaExiste(
        nome: String,
        listaId: Int,
        itensExistentes: List<ItemLista>,
        ignorarItemId: Int? = null,
    ): Boolean {
        val nomeNormalizado = ListaCompraValidator.normalizarNome(nome)

        return itensExistentes.any { item ->
            item.listaId == listaId &&
                item.id != ignorarItemId &&
                ListaCompraValidator.normalizarNome(item.nome) == nomeNormalizado
        }
    }
}
