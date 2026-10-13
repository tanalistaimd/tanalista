package br.ufrn.tanalista.validation

import br.ufrn.tanalista.model.ItemLista
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ItemListaValidatorTest {
    private val itens =
        listOf(
            ItemLista(id = 1, listaId = 1, nome = "Leite 200g"),
            ItemLista(id = 2, listaId = 1, nome = "Pão Francês"),
            ItemLista(id = 3, listaId = 2, nome = "Arroz"),
        )

    @Test
    fun `nome vazio deve ser invalido`() {
        assertFalse(ItemListaValidator.nomeEhValido("   "))
    }

    @Test
    fun `nome preenchido deve ser valido`() {
        assertTrue(ItemListaValidator.nomeEhValido("Leite"))
    }

    @Test
    fun `nome com mais de 60 caracteres deve ser invalido`() {
        assertFalse(ItemListaValidator.nomeEhValido("a".repeat(61)))
    }

    @Test
    fun `quantidade nao informada deve ser valida`() {
        assertTrue(ItemListaValidator.quantidadeEhValida(null))
    }

    @Test
    fun `quantidade positiva deve ser valida`() {
        assertTrue(ItemListaValidator.quantidadeEhValida(1.5))
    }

    @Test
    fun `quantidade zero ou negativa deve ser invalida`() {
        assertFalse(ItemListaValidator.quantidadeEhValida(0.0))
        assertFalse(ItemListaValidator.quantidadeEhValida(-2.0))
    }

    @Test
    fun `deve bloquear item duplicado pelo nome normalizado na mesma lista`() {
        assertTrue(
            ItemListaValidator.nomeJaExiste(
                nome = "  PAO   FRANCES ",
                listaId = 1,
                itensExistentes = itens,
            ),
        )
    }

    @Test
    fun `deve aceitar variacoes do mesmo produto como itens diferentes`() {
        assertFalse(
            ItemListaValidator.nomeJaExiste(
                nome = "Leite 400g",
                listaId = 1,
                itensExistentes = itens,
            ),
        )
    }

    @Test
    fun `deve aceitar o mesmo nome em outra lista`() {
        assertFalse(
            ItemListaValidator.nomeJaExiste(
                nome = "Arroz",
                listaId = 1,
                itensExistentes = itens,
            ),
        )
    }

    @Test
    fun `edicao nao deve acusar duplicidade com o proprio item`() {
        assertFalse(
            ItemListaValidator.nomeJaExiste(
                nome = "leite 200g",
                listaId = 1,
                itensExistentes = itens,
                ignorarItemId = 1,
            ),
        )
    }
}
