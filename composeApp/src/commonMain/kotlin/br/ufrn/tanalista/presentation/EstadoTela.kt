package br.ufrn.tanalista.presentation

internal sealed interface EstadoTela<out T> {
    data object Carregando : EstadoTela<Nothing>

    data class Vazio<T>(
        val conteudo: T,
    ) : EstadoTela<T>

    data class Sucesso<T>(
        val conteudo: T,
    ) : EstadoTela<T>

    data class Erro(
        val mensagem: String,
    ) : EstadoTela<Nothing>
}

internal fun <T> conteudoDisponivel(
    conteudo: T,
    vazio: Boolean,
): EstadoTela<T> = if (vazio) EstadoTela.Vazio(conteudo) else EstadoTela.Sucesso(conteudo)
