package br.ufrn.tanalista.navigation

import kotlinx.serialization.Serializable

@Serializable
object ListasRota

@Serializable
data class ListaDetalheRota(
    val listaId: Int,
)
