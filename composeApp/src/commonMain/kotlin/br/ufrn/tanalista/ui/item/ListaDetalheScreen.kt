package br.ufrn.tanalista.ui.item

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.ufrn.tanalista.model.Categoria
import br.ufrn.tanalista.model.ItemLista
import br.ufrn.tanalista.model.ListaCompra
import br.ufrn.tanalista.model.Unidade
import br.ufrn.tanalista.ui.tema.TaNaListaTema
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

/** [lista] é `null` quando o id da rota não corresponde a nenhuma lista existente. */
@Composable
fun ListaDetalheScreen(
    lista: ListaCompra?,
    itens: List<ItemLista>,
    formulario: FormularioItem,
    onFormularioChange: (FormularioItem) -> Unit,
    onAdicionar: () -> Unit,
    onEditar: (ItemLista, FormularioItem) -> Boolean,
    onRemover: (ItemLista) -> Boolean,
    onRestaurar: (ItemLista) -> Boolean,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var itemEmEdicao by remember { mutableStateOf<ItemLista?>(null) }
    val avisos = remember { SnackbarHostState() }
    val escopo = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        ListaDetalheConteudo(
            lista = lista,
            itens = itens,
            formulario = formulario,
            onFormularioChange = onFormularioChange,
            onAdicionar = onAdicionar,
            onEditar = { itemEmEdicao = it },
            onRemover = { item ->
                if (onRemover(item)) {
                    escopo.launch {
                        val resultado =
                            avisos.showSnackbar(
                                "${item.nome} removido.",
                                actionLabel = "Desfazer",
                                withDismissAction = true,
                                duration = SnackbarDuration.Long,
                            )
                        if (resultado == SnackbarResult.ActionPerformed && !onRestaurar(item)) {
                            avisos.showSnackbar("Não foi possível restaurar ${item.nome}.")
                        }
                    }
                }
            },
            onVoltar = onVoltar,
        )
        SnackbarHost(hostState = avisos, modifier = Modifier.align(Alignment.BottomCenter))
    }

    itemEmEdicao?.let { item ->
        DialogoEditarItem(
            item = item,
            itens = itens,
            onSalvar = { edicao ->
                if (onEditar(item, edicao)) itemEmEdicao = null
            },
            onCancelar = { itemEmEdicao = null },
        )
    }
}

@Composable
private fun ListaDetalheConteudo(
    lista: ListaCompra?,
    itens: List<ItemLista>,
    formulario: FormularioItem,
    onFormularioChange: (FormularioItem) -> Unit,
    onAdicionar: () -> Unit,
    onEditar: (ItemLista) -> Unit,
    onRemover: (ItemLista) -> Unit,
    onVoltar: () -> Unit,
) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            TextButton(
                onClick = onVoltar,
                modifier = Modifier.semantics { contentDescription = "Voltar para as listas" },
            ) {
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CamposDoItem(
                        formulario = formulario,
                        erroNome = formulario.erroNome(lista.id, itens),
                        erroQuantidade = formulario.erroQuantidade(),
                        onFormularioChange = onFormularioChange,
                    )
                    Button(
                        onClick = onAdicionar,
                        enabled = formulario.podeAdicionar(lista.id, itens),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Adicionar item")
                    }
                }
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
                    ItemListaCard(item, onEditar = { onEditar(item) }, onRemover = { onRemover(item) })
                }
            }
        }
    }
}

@Composable
private fun CamposDoItem(
    formulario: FormularioItem,
    erroNome: String?,
    erroQuantidade: String?,
    onFormularioChange: (FormularioItem) -> Unit,
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
    }
}

@Composable
private fun DialogoEditarItem(
    item: ItemLista,
    itens: List<ItemLista>,
    onSalvar: (FormularioItem) -> Unit,
    onCancelar: () -> Unit,
) {
    var formulario by remember(item.id) {
        mutableStateOf(
            FormularioItem(
                nome = item.nome,
                quantidade =
                    item.quantidade
                        ?.toString()
                        ?.replace('.', ',')
                        .orEmpty(),
                unidade = item.unidade,
                categoria = item.categoria,
            ),
        )
    }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Editar item") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                CamposDoItem(
                    formulario = formulario,
                    erroNome = formulario.erroNome(item.listaId, itens, ignorarItemId = item.id),
                    erroQuantidade = formulario.erroQuantidade(),
                    onFormularioChange = { formulario = it },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSalvar(formulario) },
                enabled = formulario.podeAdicionar(item.listaId, itens, ignorarItemId = item.id),
            ) {
                Text("Salvar")
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } },
    )
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
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.semantics { heading() },
        )
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
private fun ItemListaCard(
    item: ItemLista,
    onEditar: () -> Unit,
    onRemover: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.nome, style = MaterialTheme.typography.titleMedium)

            val detalhes = detalhesDoItem(item)
            if (detalhes.isNotEmpty()) {
                Text(text = detalhes, style = MaterialTheme.typography.bodyMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = onEditar,
                    modifier = Modifier.semantics { contentDescription = "Editar ${item.nome}" },
                ) {
                    Text("Editar")
                }
                TextButton(
                    onClick = onRemover,
                    modifier = Modifier.semantics { contentDescription = "Remover ${item.nome}" },
                ) {
                    Text("Remover")
                }
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
    TaNaListaTema(escuro = false) {
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
            onEditar = { _, _ -> false },
            onRemover = { false },
            onRestaurar = { false },
            onVoltar = {},
        )
    }
}

@Preview
@Composable
private fun ListaDetalheScreenVaziaPreview() {
    TaNaListaTema(escuro = false) {
        ListaDetalheScreen(
            lista = ListaCompra(1, "Compras do mês"),
            itens = emptyList(),
            formulario = FormularioItem(),
            onFormularioChange = {},
            onAdicionar = {},
            onEditar = { _, _ -> false },
            onRemover = { false },
            onRestaurar = { false },
            onVoltar = {},
        )
    }
}

@Preview
@Composable
private fun ListaDetalheScreenEscuroPreview() {
    TaNaListaTema(escuro = true) {
        ListaDetalheScreen(
            lista = ListaCompra(1, "Compras do mês"),
            itens = listOf(ItemLista(1, 1, "Tomate", 1.5, Unidade.QUILO, Categoria.HORTIFRUTI)),
            formulario = FormularioItem(nome = "tomate", quantidade = "0"),
            onFormularioChange = {},
            onAdicionar = {},
            onEditar = { _, _ -> false },
            onRemover = { false },
            onRestaurar = { false },
            onVoltar = {},
        )
    }
}
