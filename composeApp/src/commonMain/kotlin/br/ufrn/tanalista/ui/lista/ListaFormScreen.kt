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
 * O estado da tela é elevado: este componente não armazena o nome digitado
 * internamente. Ele apenas recebe os dados atuais e informa os eventos
 * de alteração e criação para o componente que o utiliza.
 *
 * @param nome Nome atualmente digitado no formulário.
 * @param erro Mensagem de erro exibida no campo. Quando nula, não há erro.
 * @param onNomeChange Evento chamado sempre que o usuário altera o nome.
 * @param onCriar Evento chamado quando o usuário pressiona o botão de criação.
 * @param modifier Modificador opcional aplicado ao layout principal da tela.
 */
@Composable
fun ListaFormScreen(
    nome: String,
    erro: String?,
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

        /*
         * O botão permanece desabilitado enquanto:
         * - o nome estiver vazio;
         * - existir algum erro de validação.
         */
        Button(
            onClick = onCriar,
            enabled = erro == null && nome.isNotBlank(),
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
            onNomeChange = {},
            onCriar = {},
        )
    }
}
