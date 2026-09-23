package br.ufrn.tanalista.validation

import br.ufrn.tanalista.model.ListaCompra

object ListaCompraValidator {


    fun normalizarNome(nome: String): String =
        nome
            .trim()
            .lowercase()
            .map { caractere ->
                when (caractere) {
                    'á', 'à', 'â', 'ã', 'ä' -> 'a'
                    'é', 'è', 'ê', 'ë' -> 'e'
                    'í', 'ì', 'î', 'ï' -> 'i'
                    'ó', 'ò', 'ô', 'õ', 'ö' -> 'o'
                    'ú', 'ù', 'û', 'ü' -> 'u'
                    'ç' -> 'c'
                    else -> caractere
                }
            }.joinToString("")
            .replace(Regex("\\s+"), " ")

    fun nomeEhValido(nome: String): Boolean = nome.isNotBlank()

    fun nomeJaExiste(
        nome: String,
        listasExistentes: List<ListaCompra>,
    ): Boolean =
        normalizarNome(nome).let { nomeNormalizado ->
            listasExistentes.any { lista ->
                normalizarNome(lista.nome) == nomeNormalizado
            }
        }
}
