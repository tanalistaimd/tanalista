package br.ufrn.tanalista.domain.model

data class ItemLista(
    val id: Int,
    val listaId: Int,
    val nome: String,
    val quantidade: Double? = null,
    val unidade: Unidade? = null,
    val categoria: Categoria? = null,
)
