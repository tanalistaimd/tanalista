package br.ufrn.tanalista.data

import br.ufrn.tanalista.domain.DadosItem
import br.ufrn.tanalista.domain.model.RegistroCompra
import br.ufrn.tanalista.domain.model.resumirListas
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RepositorioComprasTest {
    @Test
    fun `remover e restaurar item comprado atualiza o total da lista`() =
        runTest {
            val repositorio = RepositorioListasEmMemoria()
            repositorio.criarLista("Mercado")
            val lista =
                repositorio.dados.value.listas
                    .single()
            repositorio.adicionarItem(lista.id, DadosItem(nome = "Arroz"))
            val item =
                repositorio.dados.value.itens
                    .single()
            repositorio.gravarRegistro(item, RegistroCompra(750, 2000))

            repositorio.removerItem(item)
            val semItem = repositorio.dados.value
            assertEquals(
                0,
                resumirListas(semItem.listas, semItem.itens, semItem.compras).single().progresso.itensComprados,
            )

            repositorio.restaurarItem(item)
            val restaurada = repositorio.dados.value
            assertEquals(
                1500L,
                restaurada.compras
                    .getValue(lista.id)
                    .limitarA(restaurada.itens)
                    .totalCentavos,
            )

            repositorio.excluirLista(lista.id)
            assertTrue(
                repositorio.dados.value.listas
                    .isEmpty(),
            )
            assertTrue(
                repositorio.dados.value.itens
                    .isEmpty(),
            )
            assertTrue(
                repositorio.dados.value.compras
                    .isEmpty(),
            )
        }
}
