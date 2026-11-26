package br.ufrn.tanalista.data

import br.ufrn.tanalista.domain.DadosItem
import br.ufrn.tanalista.domain.FalhaRepositorio
import br.ufrn.tanalista.domain.model.Categoria
import br.ufrn.tanalista.domain.model.Unidade
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RepositorioItensTest {
    @Test
    fun `edita os campos e aceita manter o proprio nome`() =
        runTest {
            val repositorio = repositorioComLista()
            repositorio.adicionarItem(1, DadosItem(nome = "Leite", quantidade = 1.0))
            val item =
                repositorio.dados.value.itens
                    .single()
            val edicao =
                DadosItem(
                    nome = " LEITE ",
                    quantidade = 2.5,
                    unidade = Unidade.LITRO,
                    categoria = Categoria.LATICINIOS,
                )

            repositorio.editarItem(item, edicao)
            assertEquals(
                item.copy(nome = "LEITE", quantidade = 2.5, unidade = Unidade.LITRO, categoria = Categoria.LATICINIOS),
                repositorio.dados.value.itens
                    .single(),
            )

            repositorio.editarItem(
                repositorio.dados.value.itens
                    .single(),
                DadosItem(nome = "Bebida"),
            )
            assertEquals(
                item.copy(nome = "Bebida", quantidade = null),
                repositorio.dados.value.itens
                    .single(),
            )
        }

    @Test
    fun `rejeita nome duplicado e quantidade invalida na edicao`() =
        runTest {
            val repositorio = repositorioComLista()
            repositorio.adicionarItem(1, DadosItem(nome = "Café"))
            repositorio.adicionarItem(1, DadosItem(nome = "Leite"))
            val antes = repositorio.dados.value
            val item = antes.itens.last()

            assertFailsWith<FalhaRepositorio> { repositorio.editarItem(item, DadosItem(nome = " cafe ")) }
            assertFailsWith<FalhaRepositorio> {
                repositorio.editarItem(item, DadosItem(nome = "Leite", quantidade = 0.0))
            }
            assertEquals(antes, repositorio.dados.value)
        }

    @Test
    fun `desfaz a remocao sem colidir com o id de um novo item`() =
        runTest {
            val repositorio = repositorioComLista()
            repositorio.adicionarItem(1, DadosItem(nome = "Leite"))
            val item =
                repositorio.dados.value.itens
                    .single()

            repositorio.removerItem(item)
            assertTrue(
                repositorio.dados.value.itens
                    .isEmpty(),
            )
            assertFailsWith<FalhaRepositorio> { repositorio.editarItem(item, DadosItem(nome = "Suco")) }

            repositorio.adicionarItem(1, DadosItem(nome = "Pão"))
            repositorio.restaurarItem(item)
            assertFailsWith<FalhaRepositorio> { repositorio.restaurarItem(item) }
            assertEquals(
                listOf("Leite", "Pão"),
                repositorio.dados.value.itens
                    .map { it.nome },
            )
            assertEquals(
                2,
                repositorio.dados.value.itens
                    .map { it.id }
                    .distinct()
                    .size,
            )
        }

    @Test
    fun `desfazer respeita a unicidade de nome na lista`() =
        runTest {
            val repositorio = repositorioComLista()
            repositorio.adicionarItem(1, DadosItem(nome = "Café"))
            val item =
                repositorio.dados.value.itens
                    .single()

            repositorio.removerItem(item)
            repositorio.adicionarItem(1, DadosItem(nome = "Cafe"))

            assertFailsWith<FalhaRepositorio> { repositorio.restaurarItem(item) }
            assertEquals(
                "Cafe",
                repositorio.dados.value.itens
                    .single()
                    .nome,
            )
        }

    private suspend fun repositorioComLista(): RepositorioListasEmMemoria =
        RepositorioListasEmMemoria().also { it.criarLista("Casa") }
}
