package br.ufrn.tanalista.ui.mercado

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.ufrn.tanalista.model.Compra
import br.ufrn.tanalista.model.ItemLista
import br.ufrn.tanalista.model.ListaCompra
import br.ufrn.tanalista.model.RegistroCompra

@Composable
fun MercadoScreen(
    lista: ListaCompra?,
    itens: List<ItemLista>,
    compra: Compra?,
    onCompraChange: (Compra) -> Unit,
    onSair: () -> Unit,
) {
    var itemEmEdicao by remember { mutableStateOf<ItemLista?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextButton(onClick = onSair) { Text("Voltar para as listas") }
        Text(
            text = lista?.nome ?: "Lista não encontrada",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading() },
        )
        if (compra != null) {
            Text(
                text = "Total do carrinho · ${formatarPreco(compra.totalCentavos)}",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (itens.isEmpty()) item { Text("Adicione itens à lista para registrar a compra.") }
                items(itens, key = { it.id }) { item ->
                    ItemNoMercado(
                        item = item,
                        registro = compra.registroDoItem(item),
                        onEditar = { itemEmEdicao = item },
                        onDesmarcar = { onCompraChange(compra.desmarcar(item)) },
                    )
                }
            }
        }
    }

    val item = itemEmEdicao
    if (item != null && compra != null) {
        DialogoRegistro(
            item = item,
            registro = compra.registroDoItem(item),
            onSalvar = { registro ->
                onCompraChange(compra.marcarComprado(item, registro))
                itemEmEdicao = null
            },
            onCancelar = { itemEmEdicao = null },
        )
    }
}

@Composable
private fun ItemNoMercado(
    item: ItemLista,
    registro: RegistroCompra?,
    onEditar: () -> Unit,
    onDesmarcar: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(item.nome, style = MaterialTheme.typography.titleLarge)
            if (registro != null) {
                val quantidade = formatarQuantidade(registro.quantidadeMilesimos)
                val unidade = item.unidade?.let { " ${it.medida}" }.orEmpty()
                Text(
                    "Comprado · $quantidade$unidade × ${formatarPreco(registro.precoUnitarioCentavos.toLong())}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text("Subtotal · ${formatarPreco(registro.subtotalCentavos)}")
            } else {
                val planejado = formatarQuantidadePlanejada(item.quantidade)
                val quantidadePlanejada = listOfNotNull(planejado, item.unidade?.medida).joinToString(" ")
                if (quantidadePlanejada.isNotEmpty()) {
                    Text("Planejado · $quantidadePlanejada", style = MaterialTheme.typography.bodyLarge)
                }
            }

            val acao = if (registro == null) "Marcar como comprado" else "Editar preço e quantidade"
            Button(
                onClick = onEditar,
                modifier =
                    Modifier.fillMaxWidth().heightIn(min = 48.dp).semantics {
                        contentDescription = "$acao · ${item.nome}"
                    },
            ) {
                Text(acao)
            }
            if (registro != null) {
                TextButton(onClick = onDesmarcar, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text("Desmarcar compra")
                }
            }
        }
    }
}

@Composable
private fun DialogoRegistro(
    item: ItemLista,
    registro: RegistroCompra?,
    onSalvar: (RegistroCompra) -> Unit,
    onCancelar: () -> Unit,
) {
    var preco by remember(item.id) {
        mutableStateOf(registro?.let { formatarPreco(it.precoUnitarioCentavos.toLong()).removePrefix("R$ ") }.orEmpty())
    }
    var quantidade by remember(item.id) {
        mutableStateOf(
            registro?.let { formatarQuantidade(it.quantidadeMilesimos) }
                ?: formatarQuantidadePlanejada(item.quantidade)
                ?: "1",
        )
    }
    val precoCentavos = interpretarDecimal(preco, casas = 2)
    val quantidadeMilesimos = interpretarDecimal(quantidade, casas = 3)?.takeIf { it > 0 }
    val unidade = item.unidade?.let { " (${it.medida})" }.orEmpty()

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(item.nome) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CampoDecimal(
                    valor = preco,
                    onValorChange = { preco = it },
                    rotulo = "Preço unitário encontrado (R$)",
                    placeholder = "Informe o valor em R$",
                    erro = "Informe um preço válido com até duas casas decimais.",
                    invalido = preco.isNotBlank() && precoCentavos == null,
                )
                CampoDecimal(
                    valor = quantidade,
                    onValorChange = { quantidade = it },
                    rotulo = "Quantidade levada$unidade",
                    erro = "Informe uma quantidade maior que zero com até três casas decimais.",
                    invalido = quantidade.isNotBlank() && quantidadeMilesimos == null,
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = precoCentavos != null && quantidadeMilesimos != null,
                onClick = {
                    if (precoCentavos != null && quantidadeMilesimos != null) {
                        onSalvar(RegistroCompra(precoCentavos, quantidadeMilesimos))
                    }
                },
            ) {
                Text(if (registro == null) "Marcar comprado" else "Salvar")
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } },
    )
}

private fun formatarQuantidadePlanejada(quantidade: Double?): String? =
    quantidade
        ?.toString()
        ?.removeSuffix(".0")
        ?.replace('.', ',')

@Composable
private fun CampoDecimal(
    valor: String,
    onValorChange: (String) -> Unit,
    rotulo: String,
    placeholder: String? = null,
    erro: String,
    invalido: Boolean,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(rotulo) },
        placeholder = placeholder?.let { texto -> { Text(texto) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        isError = invalido,
        supportingText = { if (invalido) Text(erro) },
    )
}
