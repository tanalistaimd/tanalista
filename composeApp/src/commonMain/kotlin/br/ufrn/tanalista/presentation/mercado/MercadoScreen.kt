package br.ufrn.tanalista.presentation.mercado

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.ufrn.tanalista.domain.model.ItemLista
import br.ufrn.tanalista.presentation.AvisoDaTela
import br.ufrn.tanalista.presentation.ConteudoPorEstado

@Composable
internal fun MercadoDestino(
    viewModel: MercadoViewModel,
    onVoltar: () -> Unit,
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    ConteudoPorEstado(estado, onTentarDeNovo = viewModel::tentarDeNovo, onVoltar = onVoltar) { conteudo ->
        MercadoScreen(
            conteudo = conteudo,
            onEditar = viewModel::iniciarEdicao,
            onDesmarcar = viewModel::desmarcar,
            onVoltar = onVoltar,
        )
        conteudo.edicao?.let { edicao ->
            DialogoRegistro(
                edicao = edicao,
                aviso = conteudo.aviso,
                onPrecoChange = viewModel::alterarPreco,
                onQuantidadeChange = viewModel::alterarQuantidade,
                onSalvar = viewModel::salvarRegistro,
                onCancelar = viewModel::cancelarEdicao,
            )
        }
    }
}

@Composable
internal fun MercadoScreen(
    conteudo: ConteudoMercado,
    onEditar: (ItemLista) -> Unit,
    onDesmarcar: (ItemLista) -> Unit,
    onVoltar: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextButton(onClick = onVoltar) { Text("Voltar para as listas") }
        Text(
            text = conteudo.lista.nome,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = "Total do carrinho · ${formatarPreco(conteudo.totalCentavos)}",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
        if (conteudo.edicao == null) AvisoDaTela(conteudo.aviso)
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (conteudo.carrinho.isEmpty()) item { Text("Adicione itens à lista para registrar a compra.") }
            items(conteudo.carrinho, key = { it.item.id }) { entrada ->
                ItemNoMercado(
                    entrada = entrada,
                    onEditar = { onEditar(entrada.item) },
                    onDesmarcar = { onDesmarcar(entrada.item) },
                )
            }
        }
    }
}

@Composable
private fun ItemNoMercado(
    entrada: ItemNoCarrinho,
    onEditar: () -> Unit,
    onDesmarcar: () -> Unit,
) {
    val (item, registro, subtotal) = entrada
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
                subtotal?.let { Text("Subtotal · ${formatarPreco(it)}") }
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
    edicao: EdicaoRegistro,
    aviso: String?,
    onPrecoChange: (String) -> Unit,
    onQuantidadeChange: (String) -> Unit,
    onSalvar: () -> Unit,
    onCancelar: () -> Unit,
) {
    val unidade =
        edicao.item.unidade
            ?.let { " (${it.medida})" }
            .orEmpty()
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(edicao.item.nome) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CampoDecimal(
                    valor = edicao.preco,
                    onValorChange = onPrecoChange,
                    rotulo = "Preço unitário encontrado (R$)",
                    placeholder = "Informe o valor em R$",
                    erro = "Informe um preço válido com até duas casas decimais.",
                    invalido = edicao.precoInvalido,
                )
                CampoDecimal(
                    valor = edicao.quantidade,
                    onValorChange = onQuantidadeChange,
                    rotulo = "Quantidade levada$unidade",
                    erro = "Informe uma quantidade maior que zero com até três casas decimais.",
                    invalido = edicao.quantidadeInvalida,
                )
                AvisoDaTela(aviso)
            }
        },
        confirmButton = {
            TextButton(enabled = edicao.podeSalvar, onClick = onSalvar) {
                Text(if (edicao.jaComprado) "Salvar" else "Marcar comprado")
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } },
    )
}

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
