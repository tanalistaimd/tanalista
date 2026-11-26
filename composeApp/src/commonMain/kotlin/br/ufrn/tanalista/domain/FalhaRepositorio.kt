package br.ufrn.tanalista.domain

enum class MotivoFalha {
    SEM_CONEXAO,
    RESPOSTA_INVALIDA,
    LISTA_INEXISTENTE,
    LISTA_INVALIDA,
    LISTA_DUPLICADA,
    ITEM_INEXISTENTE,
    ITEM_MODIFICADO,
    ITEM_INVALIDO,
    ITEM_DUPLICADO,
}

class FalhaRepositorio(
    val motivo: MotivoFalha,
    causa: Throwable? = null,
) : Exception(causa)
