package br.ufrn.tanalista.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.ufrn.tanalista.model.ListaComProgresso
import br.ufrn.tanalista.model.ListaCompra
import br.ufrn.tanalista.model.ProgressoCompra
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Tela responsável por criar e visualizar listas de compras.
 *
 * O estado é elevado: a tela apenas recebe os dados atuais e comunica
 * as ações do usuário ao componente responsável pelo estado.
 *
 * @param listas Listas ordenadas por atualização, com progresso da compra atual.
 * @param nome Nome atualmente digitado no formulário.
 * @param erro Mensagem de erro de validação ou `null` quando não há erro.
 * @param mensagemSucesso Mensagem exibida após uma criação bem-sucedida.
 * @param onNomeChange Evento disparado quando o nome é alterado.
 * @param onCriar Evento disparado quando o usuário solicita a criação.
 * @param modifier Modificador opcional aplicado à tela.
 * @param onEditarLista Evento disparado quando o usuário solicita editar uma lista.
 * @param onExcluirLista Evento disparado quando o usuário solicita excluir uma lista.
 */
@Composable
fun ListaFormScreen(
    listas: List<ListaComProgresso>,
    nome: String,
    erro: String?,
    mensagemSucesso: String?,
    onNomeChange: (String) -> Unit,
    onCriar: () -> Unit,
    onEditarLista: (ListaCompra) -> Unit,
    onExcluirLista: (ListaCompra) -> Unit,
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

        Text("Minhas listas", style = MaterialTheme.typography.titleLarge)
        ListaResumoContent(
            listas = listas,
            onEditarLista = onEditarLista,
            onExcluirLista = onExcluirLista,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * Exibe as listas de compras criadas pelo usuário.
 *
 * @param listas Listas que serão apresentadas na tela.
 * @param onEditarLista Evento disparado quando o usuário solicita editar uma lista.
 * @param onExcluirLista Evento disparado quando o usuário solicita excluir uma lista.
 * @param modifier Modificador opcional aplicado à listagem.
 */
@Composable
private fun ListaResumoContent(
    listas: List<ListaComProgresso>,
    onEditarLista: (ListaCompra) -> Unit,
    onExcluirLista: (ListaCompra) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (listas.isEmpty()) {
        Text("Nenhuma lista criada ainda.")
    } else {
        LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listas, key = { it.lista.id }) { resumo ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            text = resumo.lista.nome,
                            style = MaterialTheme.typography.titleMedium,
                        )

                        Text(
                            text = "${resumo.progresso.itensComprados}/${resumo.progresso.totalItens} itens comprados",
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        /*
                         * Ações disponíveis para cada lista.
                         *
                         * A tela não realiza diretamente a edição ou exclusão:
                         * apenas comunica a ação ao componente responsável pelo estado.
                         */
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(
                                onClick = {
                                    onEditarLista(resumo.lista)
                                },
                            ) {
                                Text("Editar")
                            }

                            TextButton(
                                onClick = {
                                    onExcluirLista(resumo.lista)
                                },
                            ) {
                                Text("Excluir")
                            }
                        }
                    }
                }
            }
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
            listas =
                listOf(
                    ListaComProgresso(
                        ListaCompra(1, "Compras do mês"),
                        ProgressoCompra(
                            itensComprados = 2,
                            totalItens = 5,
                        ),
                    ),
                ),
            nome = "Compras do mês",
            erro = null,
            mensagemSucesso = null,
            onNomeChange = {},
            onCriar = {},
            onEditarLista = {},
            onExcluirLista = {},
        )
    }
}
