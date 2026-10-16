package br.ufrn.tanalista.navigation

const val BASE_DEEP_LINK_LISTA = "tanalista://lista"

/**
 * Repassa à navegação os endereços recebidos pela plataforma com o app já aberto.
 *
 * Guarda o último endereço enquanto não houver ouvinte, para não perdê-lo
 * se ele chegar antes de o grafo de navegação ser montado.
 */
object ExternalUriHandler {
    private var pendente: String? = null

    var ouvinte: ((uri: String) -> Unit)? = null
        set(valor) {
            field = valor
            if (valor != null) {
                pendente?.let(valor)
                pendente = null
            }
        }

    fun receber(uri: String) {
        val atual = ouvinte
        if (atual != null) atual(uri) else pendente = uri
    }
}
