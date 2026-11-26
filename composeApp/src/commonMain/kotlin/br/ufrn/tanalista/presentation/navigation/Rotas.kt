package br.ufrn.tanalista.presentation.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// A navegação usa os nomes serializados para restaurar destinos entre versões do app.
@Serializable
@SerialName("br.ufrn.tanalista.navigation.ListasRota")
object ListasRota

@Serializable
@SerialName("br.ufrn.tanalista.navigation.ListaDetalheRota")
data class ListaDetalheRota(
    val listaId: Int,
)

@Serializable
@SerialName("br.ufrn.tanalista.navigation.MercadoRota")
data class MercadoRota(
    val listaId: Int,
)
