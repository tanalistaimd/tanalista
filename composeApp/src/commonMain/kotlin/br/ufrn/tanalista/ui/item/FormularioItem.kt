package br.ufrn.tanalista.ui.item

import br.ufrn.tanalista.model.Categoria
import br.ufrn.tanalista.model.ItemLista
import br.ufrn.tanalista.model.Unidade
import br.ufrn.tanalista.validation.ItemListaValidator

data class FormularioItem(
    val nome: String = "",
    val quantidade: String = "",
    val unidade: Unidade? = null,
    val categoria: Categoria? = null,
) {
    /** Aceita vírgula ou ponto como separador decimal; `null` quando o texto não é um número. */
    val quantidadeInformada: Double?
        get() =
            quantidade
                .trim()
                .replace(',', '.')
                .toDoubleOrNull()
                ?.takeIf { it.isFinite() }

    fun erroNome(
        listaId: Int,
        itensExistentes: List<ItemLista>,
    ): String? =
        when {
            nome.trim().length > ItemListaValidator.LIMITE_NOME ->
                "O nome deve ter no máximo ${ItemListaValidator.LIMITE_NOME} caracteres."

            nome.isNotBlank() && ItemListaValidator.nomeJaExiste(nome, listaId, itensExistentes) ->
                "Este item já está na lista."

            else -> null
        }

    fun erroQuantidade(): String? {
        val valida =
            quantidade.isBlank() ||
                quantidadeInformada?.let(ItemListaValidator::quantidadeEhValida) == true

        return if (valida) null else "Informe uma quantidade maior que zero."
    }

    fun podeAdicionar(
        listaId: Int,
        itensExistentes: List<ItemLista>,
    ): Boolean =
        ItemListaValidator.nomeEhValido(nome) &&
            erroNome(listaId, itensExistentes) == null &&
            erroQuantidade() == null
}
