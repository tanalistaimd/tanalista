package br.ufrn.tanalista.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Tela responsável pelo formulário de criação de uma nova lista de compras.
 *
 * O estado é elevado: a tela apenas recebe os dados atuais e comunica
 * as ações do usuário ao componente responsável pelo estado.
 *
 * @param nome Nome atualmente digitado no formulário.
 * @param erro Mensagem de erro de validação ou `null` quando não há erro.
 * @param mensagemSucesso Mensagem exibida após uma criação bem-sucedida.
 * @param onNomeChange Evento disparado quando o nome é alterado.
 * @param onCriar Evento disparado quando o usuário solicita a criação.
 * @param modifier Modificador opcional aplicado à tela.
 */
@Composable
fun ListaFormScreen(
    nome: String,
    erro: String?,
    mensagemSucesso: String?,
    onNomeChange: (String) -> Unit,
    onCriar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Título principal da tela.
        Text(
            text = "Nova lista",
            style = MaterialTheme.typography.headlineMedium,
        )

        // Campo em que o usuário informa o nome da nova lista.
        OutlinedTextField(
            value = nome,
            onValueChange = onNomeChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Ex.: Compras do mês")
            },
            label = {
                Text("Nome da lista")
            },
            supportingText = {
                // A mensagem só aparece quando existe algum erro de validação.
                if (erro != null) {
                    Text(erro)
                }
            },
            isError = erro != null,
            singleLine = true,
        )
        if (mensagemSucesso != null) {
            Text(
                text = mensagemSucesso,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        /*
         * O botão permanece desabilitado enquanto:
         * - o nome estiver vazio;
         * - existir algum erro de validação.
         */
        Button(
            onClick = onCriar,
            enabled = erro == null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Criar lista")
        }
    }
}

/**
 * Preview da tela de criação de lista.
 *
 * Utiliza dados fixos apenas para permitir visualizar o componente
 * diretamente pelo Android Studio sem precisar executar todo o aplicativo.
 */
@Preview
@Composable
private fun ListaFormScreenPreview() {
    MaterialTheme {
        ListaFormScreen(
            nome = "Compras do mês",
            erro = null,
            mensagemSucesso = null,
            onNomeChange = {},
            onCriar = {},
        )
    }
}
