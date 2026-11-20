package br.ufrn.tanalista.navigation

import kotlinx.serialization.Serializable

@Serializable
object ListasRota

@Serializable
data class ListaDetalheRota(
    val listaId: Int,
)

@Serializable
data class MercadoRota(
    val listaId: Int,
)
