package br.ufrn.tanalista.ui.item

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.ufrn.tanalista.model.Categoria
import br.ufrn.tanalista.model.ItemLista
import br.ufrn.tanalista.model.ListaCompra
import br.ufrn.tanalista.model.Unidade
import org.jetbrains.compose.ui.tooling.preview.Preview

/** [lista] é `null` quando o id da rota não corresponde a nenhuma lista existente. */
@Composable
fun ListaDetalheScreen(
    lista: ListaCompra?,
    itens: List<ItemLista>,
    formulario: FormularioItem,
    onFormularioChange: (FormularioItem) -> Unit,
    onAdicionar: () -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            TextButton(onClick = onVoltar) {
                Text("Voltar")
            }
            Text(
                text = lista?.nome ?: "Lista não encontrada",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() },
            )
        }

        if (lista != null) {
            item {
                FormularioItemContent(
                    formulario = formulario,
                    erroNome = formulario.erroNome(lista.id, itens),
                    erroQuantidade = formulario.erroQuantidade(),
                    podeAdicionar = formulario.podeAdicionar(lista.id, itens),
                    onFormularioChange = onFormularioChange,
                    onAdicionar = onAdicionar,
                )
            }

            item {
                Text(
                    text = "Itens",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.semantics { heading() },
                )
            }

            if (itens.isEmpty()) {
                item {
                    Text("Nenhum item adicionado ainda.")
                }
            } else {
                items(itens, key = { it.id }) { item ->
                    ItemListaCard(item)
                }
            }
        }
    }
}

@Composable
private fun FormularioItemContent(
    formulario: FormularioItem,
    erroNome: String?,
    erroQuantidade: String?,
    podeAdicionar: Boolean,
    onFormularioChange: (FormularioItem) -> Unit,
    onAdicionar: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = formulario.nome,
            onValueChange = { onFormularioChange(formulario.copy(nome = it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nome do item") },
            placeholder = { Text("Ex.: Leite 200g") },
            supportingText = { erroNome?.let { Text(it) } },
            isError = erroNome != null,
            singleLine = true,
        )

        OutlinedTextField(
            value = formulario.quantidade,
            onValueChange = { onFormularioChange(formulario.copy(quantidade = it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Quantidade (opcional)") },
            supportingText = { erroQuantidade?.let { Text(it) } },
            isError = erroQuantidade != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
        )

        SeletorOpcional(
            titulo = "Unidade (opcional)",
            opcoes = Unidade.entries,
            selecionada = formulario.unidade,
            rotulo = { it.medida },
            onSelecionar = { onFormularioChange(formulario.copy(unidade = it)) },
        )

        SeletorOpcional(
            titulo = "Categoria (opcional)",
            opcoes = Categoria.entries,
            selecionada = formulario.categoria,
            rotulo = { it.classe },
            onSelecionar = { onFormularioChange(formulario.copy(categoria = it)) },
        )

        Button(
            onClick = onAdicionar,
            enabled = podeAdicionar,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Adicionar item")
        }
    }
}

/** Tocar na opção já selecionada desfaz a seleção, porque o campo é opcional. */
@Composable
private fun <T> SeletorOpcional(
    titulo: String,
    opcoes: List<T>,
    selecionada: T?,
    rotulo: (T) -> String,
    onSelecionar: (T?) -> Unit,
) {
    Column {
        Text(text = titulo, style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            opcoes.forEach { opcao ->
                val marcada = opcao == selecionada

                FilterChip(
                    selected = marcada,
                    onClick = { onSelecionar(if (marcada) null else opcao) },
                    label = { Text(rotulo(opcao)) },
                )
            }
        }
    }
}

@Composable
private fun ItemListaCard(item: ItemLista) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.nome, style = MaterialTheme.typography.titleMedium)

            val detalhes = detalhesDoItem(item)
            if (detalhes.isNotEmpty()) {
                Text(text = detalhes, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

internal fun detalhesDoItem(item: ItemLista): String {
    val quantidade =
        item.quantidade?.let { valor ->
            if (valor % 1.0 == 0.0) valor.toLong().toString() else valor.toString().replace('.', ',')
        }

    return listOfNotNull(
        listOfNotNull(quantidade, item.unidade?.medida).joinToString(" ").ifEmpty { null },
        item.categoria?.classe,
    ).joinToString(" · ")
}

@Preview
@Composable
private fun ListaDetalheScreenPreview() {
    MaterialTheme {
        ListaDetalheScreen(
            lista = ListaCompra(1, "Compras do mês"),
            itens =
                listOf(
                    ItemLista(1, 1, "Leite 200g", 2.0, Unidade.UNIDADE, Categoria.LATICINIOS),
                    ItemLista(2, 1, "Tomate", 1.5, Unidade.QUILO, Categoria.HORTIFRUTI),
                    ItemLista(3, 1, "Detergente"),
                ),
            formulario = FormularioItem(nome = "leite 200G", quantidade = "0"),
            onFormularioChange = {},
            onAdicionar = {},
            onVoltar = {},
        )
    }
}

@Preview
@Composable
private fun ListaDetalheScreenVaziaPreview() {
    MaterialTheme {
        ListaDetalheScreen(
            lista = ListaCompra(1, "Compras do mês"),
            itens = emptyList(),
            formulario = FormularioItem(),
            onFormularioChange = {},
            onAdicionar = {},
            onVoltar = {},
        )
    }
}
