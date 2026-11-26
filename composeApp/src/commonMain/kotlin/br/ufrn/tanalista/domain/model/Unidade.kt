package br.ufrn.tanalista.domain.model

enum class Unidade(
    val medida: String,
) {
    QUILO("kg"),
    LITRO("l"),
    GRAMA("g"),
    MILILITRO("ml"),
    UNIDADE("Un"),
    OUTROS("Outros"),
}
