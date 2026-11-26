package br.ufrn.tanalista.domain.model

data class ListaCompra(
    val id: Int,
    val nome: String,
    val atualizadaEm: Long = 0L,
)
