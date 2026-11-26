package br.ufrn.tanalista.presentation.lista

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.ufrn.tanalista.domain.model.ListaCompra
import br.ufrn.tanalista.presentation.AvisoDaTela
import br.ufrn.tanalista.presentation.ConteudoPorEstado

@Composable
internal fun ListasDestino(
    viewModel: ListasViewModel,
    onAbrirLista: (ListaCompra) -> Unit,
    onAbrirMercado: (ListaCompra) -> Unit,
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    ConteudoPorEstado(estado, onTentarDeNovo = viewModel::tentarDeNovo) { conteudo ->
        ListaFormScreen(
            listas = conteudo.listas,
            nome = conteudo.nome,
            erro = conteudo.erroCriacao,
            mensagemSucesso = conteudo.mensagemSucesso,
            onNomeChange = viewModel::alterarNome,
            onCriar = viewModel::criarLista,
            onAbrirLista = onAbrirLista,
            onEditarLista = viewModel::iniciarEdicao,
            onExcluirLista = viewModel::solicitarExclusao,
            onAbrirMercado = onAbrirMercado,
            aviso = conteudo.aviso.takeIf { conteudo.dialogo == null },
        )

        when (val dialogo = conteudo.dialogo) {
            is DialogoLista.Edicao ->
                DialogoEditarLista(
                    nomeEditado = dialogo.nome,
                    erro = conteudo.erroEdicao,
                    aviso = conteudo.aviso,
                    onNomeChange = viewModel::alterarNomeEditado,
                    onSalvar = viewModel::salvarEdicao,
                    onCancelar = viewModel::fecharDialogo,
                )
            is DialogoLista.Exclusao ->
                DialogoExcluirLista(
                    lista = dialogo.lista,
                    aviso = conteudo.aviso,
                    onConfirmar = viewModel::confirmarExclusao,
                    onCancelar = viewModel::fecharDialogo,
                )
            null -> Unit
        }
    }
}

@Composable
private fun DialogoEditarLista(
    nomeEditado: String,
    erro: String?,
    aviso: String?,
    onNomeChange: (String) -> Unit,
    onSalvar: () -> Unit,
    onCancelar: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Editar nome da lista?") },
        text = {
            OutlinedTextField(
                value = nomeEditado,
                onValueChange = onNomeChange,
                label = { Text("Nome da lista") },
                supportingText = { AvisoDaTela(erro ?: aviso) },
                isError = erro != null,
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(onClick = onSalvar, enabled = erro == null) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        },
    )
}

@Composable
private fun DialogoExcluirLista(
    lista: ListaCompra,
    aviso: String?,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Excluir lista?") },
        text = {
            Column {
                Text("Tem certeza de que deseja excluir a lista \"${lista.nome}\"?")
                AvisoDaTela(aviso)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmar) { Text("Excluir") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        },
    )
}
