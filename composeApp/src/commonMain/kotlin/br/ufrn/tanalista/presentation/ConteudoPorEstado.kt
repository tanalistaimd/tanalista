package br.ufrn.tanalista.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
internal fun <T : Any> ConteudoPorEstado(
    estado: EstadoTela<T>,
    onTentarDeNovo: () -> Unit,
    onVoltar: (() -> Unit)? = null,
    conteudo: @Composable (T) -> Unit,
) {
    val dados =
        when (estado) {
            EstadoTela.Carregando -> {
                PainelSemDados(onVoltar) {
                    CircularProgressIndicator(Modifier.semantics { contentDescription = "Carregando listas e itens" })
                    Text("Carregando…")
                }
                null
            }
            is EstadoTela.Erro -> {
                PainelSemDados(onVoltar) {
                    AvisoDaTela(estado.mensagem)
                    Button(onClick = onTentarDeNovo) { Text("Tentar de novo") }
                }
                null
            }
            is EstadoTela.Vazio -> estado.conteudo
            is EstadoTela.Sucesso -> estado.conteudo
        }

    if (dados != null) conteudo(dados)
}

@Composable
internal fun AvisoDaTela(mensagem: String?) {
    if (mensagem != null) {
        Text(
            text = mensagem,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
    }
}

@Composable
private fun PainelSemDados(
    onVoltar: (() -> Unit)?,
    conteudo: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        conteudo()
        if (onVoltar != null) {
            TextButton(onClick = onVoltar) { Text("Voltar para as listas") }
        }
    }
}
