package br.ufrn.tanalista.model

enum class Categoria(
    val classe: String,
) {
    CARNES("Carnes"),
    HORTIFRUTI("Hortifrúti"),
    LATICINIOS("Laticínios"),
    PADARIA("Padaria"),
    BEBIDAS("Bebidas"),
    HIGIENE("Higiene"),
    LIMPEZA("Limpeza"),
    OUTROS("Outros"),
}
