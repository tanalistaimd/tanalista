package br.ufrn.tanalista.validation

import br.ufrn.tanalista.model.ListaCompra

object ListaCompraValidator {
    /**
     * Normaliza o nome de uma lista para permitir comparações consistentes.
     *
     * Remove espaços extras, converte o texto para minúsculas e substitui
     * caracteres acentuados por versões sem acento.
     *
     * @param nome Nome informado pelo usuário.
     * @return Nome normalizado para comparação.
     */
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

    /**
     * Verifica se o nome informado contém algum conteúdo válido.
     *
     * @param nome Nome digitado no formulário.
     * @return `true` quando o nome possui conteúdo diferente de espaços.
     */
    fun nomeEhValido(nome: String): Boolean = nome.isNotBlank()

    /**
     * Verifica se já existe uma lista com o mesmo nome normalizado.
     *
     * A comparação ignora diferenças de maiúsculas, acentos e espaços extras.
     *
     * @param nome Nome que será verificado.
     * @param listasExistentes Listas já cadastradas.
     * @return `true` quando já existe uma lista equivalente.
     */

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
