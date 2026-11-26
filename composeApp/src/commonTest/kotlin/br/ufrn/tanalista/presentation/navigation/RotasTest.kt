package br.ufrn.tanalista.presentation.navigation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSerializationApi::class)
class RotasTest {
    @Test
    fun `preserva os destinos salvos pelas versoes anteriores do app`() {
        assertEquals("br.ufrn.tanalista.navigation.ListasRota", ListasRota.serializer().descriptor.serialName)
        assertEquals(
            "br.ufrn.tanalista.navigation.ListaDetalheRota",
            ListaDetalheRota.serializer().descriptor.serialName,
        )
        assertEquals("br.ufrn.tanalista.navigation.MercadoRota", MercadoRota.serializer().descriptor.serialName)
    }
}
