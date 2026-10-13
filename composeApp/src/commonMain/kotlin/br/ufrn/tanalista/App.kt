package br.ufrn.tanalista

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import br.ufrn.tanalista.model.ListaComProgresso
import br.ufrn.tanalista.model.ListaCompra
import br.ufrn.tanalista.model.ProgressoCompra
import br.ufrn.tanalista.model.ordenarPorAtualizacao
import br.ufrn.tanalista.ui.lista.ListaFormScreen
import br.ufrn.tanalista.validation.ListaCompraValidator
import kotlin.time.Clock

/**
 * Componente raiz da interface compartilhada do TáNaLista.
 *
 * Mantém o estado das listas em memória durante a Sprint 1
 * e conecta a interface às ações de criação, edição e exclusão.
 */
@Composable
fun App() {
    val state = remember { ListaAppState() }

    MaterialTheme {
        ListaFormScreen(
            listas = state.listasComProgresso,
            nome = state.nome,
            erro = state.erroCriacao,
            mensagemSucesso = state.mensagemSucesso,
            onNomeChange = state::alterarNome,
            onCriar = state::criarLista,
            onEditarLista = state::iniciarEdicao,
            onExcluirLista = state::solicitarExclusao,
        )

        state.listaParaEditar?.let { lista ->
            DialogoEditarLista(
                nomeEditado = state.nomeEditado,
                erro = state.erroEdicao(lista),
                onNomeChange = state::alterarNomeEditado,
                onSalvar = {
                    state.salvarEdicao(lista)
                },
                onCancelar = state::cancelarEdicao,
            )
        }

        state.listaParaExcluir?.let { lista ->
            DialogoExcluirLista(
                lista = lista,
                onConfirmar = {
                    state.confirmarExclusao(lista)
                },
                onCancelar = state::cancelarExclusao,
            )
        }
    }
}

/**
 * Mantém e manipula o estado relacionado às listas de compras.
 *
 * Concentra as regras de criação, edição e exclusão para evitar
 * que o componente principal acumule responsabilidades.
 */
private class ListaAppState {
    var nome by mutableStateOf("")
        private set

    var listas by mutableStateOf(emptyList<ListaCompra>())
        private set

    var tentouCriar by mutableStateOf(false)
        private set

    var mensagemSucesso by mutableStateOf<String?>(null)
        private set

    var listaParaExcluir by mutableStateOf<ListaCompra?>(null)
        private set

    var listaParaEditar by mutableStateOf<ListaCompra?>(null)
        private set

    var nomeEditado by mutableStateOf("")
        private set

    /**
     * Listas preparadas para exibição na interface.
     */
    val listasComProgresso: List<ListaComProgresso>
        get() =
            ordenarPorAtualizacao(
                listas.map { lista ->
                    ListaComProgresso(
                        lista = lista,
                        progresso =
                            ProgressoCompra(
                                itensComprados = 0,
                                totalItens = 0,
                            ),
                    )
                },
            )

    /**
     * Verifica se o nome informado já pertence a outra lista.
     */
    private val nomeDuplicado: Boolean
        get() =
            nome.isNotBlank() &&
                ListaCompraValidator.nomeJaExiste(
                    nome = nome,
                    listasExistentes = listas,
                )

    /**
     * Retorna o erro atual do formulário de criação.
     */
    val erroCriacao: String?
        get() =
            when {
                tentouCriar && nome.isBlank() ->
                    "O nome da lista é obrigatório."

                nome.trim().length > ListaCompraValidator.LIMITE_NOME ->
                    "O nome deve ter no máximo " +
                        "${ListaCompraValidator.LIMITE_NOME} caracteres."

                nomeDuplicado ->
                    "Já existe uma lista com esse nome."

                else -> null
            }

    /**
     * Atualiza o nome digitado no formulário de criação.
     */
    fun alterarNome(novoNome: String) {
        nome = novoNome
        tentouCriar = false
        mensagemSucesso = null
    }

    /**
     * Cria uma nova lista quando os dados são válidos.
     */
    fun criarLista() {
        tentouCriar = true

        if (!ListaCompraValidator.nomeEhValido(nome) || nomeDuplicado) {
            return
        }

        val novaLista =
            ListaCompra(
                id = (listas.maxOfOrNull { it.id } ?: 0) + 1,
                nome = nome.trim(),
                atualizadaEm =
                    Clock.System
                        .now()
                        .toEpochMilliseconds(),
            )

        listas = listas + novaLista
        nome = ""
        tentouCriar = false
        mensagemSucesso = "Lista criada com sucesso."
    }

    /**
     * Inicia a edição da lista selecionada.
     */
    fun iniciarEdicao(lista: ListaCompra) {
        listaParaEditar = lista
        nomeEditado = lista.nome
        mensagemSucesso = null
    }

    /**
     * Atualiza o nome temporário usado durante a edição.
     */
    fun alterarNomeEditado(novoNome: String) {
        nomeEditado = novoNome
    }

    /**
     * Valida o nome informado durante a edição.
     */
    fun erroEdicao(lista: ListaCompra): String? =
        when {
            nomeEditado.isBlank() ->
                "O nome da lista é obrigatório."

            nomeEditado.trim().length > ListaCompraValidator.LIMITE_NOME ->
                "O nome deve ter no máximo " +
                    "${ListaCompraValidator.LIMITE_NOME} caracteres."

            ListaCompraValidator.nomeJaExiste(
                nome = nomeEditado,
                listasExistentes =
                    listas.filter { listaExistente ->
                        listaExistente.id != lista.id
                    },
            ) ->
                "Já existe uma lista com esse nome."

            else -> null
        }

    /**
     * Salva a alteração realizada no nome da lista.
     */
    fun salvarEdicao(lista: ListaCompra) {
        if (erroEdicao(lista) != null) {
            return
        }

        listas =
            listas.map { listaExistente ->
                if (listaExistente.id == lista.id) {
                    listaExistente.copy(
                        nome = nomeEditado.trim(),
                        atualizadaEm =
                            Clock.System
                                .now()
                                .toEpochMilliseconds(),
                    )
                } else {
                    listaExistente
                }
            }

        cancelarEdicao()
        mensagemSucesso = "Lista editada com sucesso."
    }

    /**
     * Cancela a edição atualmente aberta.
     */
    fun cancelarEdicao() {
        listaParaEditar = null
        nomeEditado = ""
    }

    /**
     * Seleciona uma lista para exclusão.
     */
    fun solicitarExclusao(lista: ListaCompra) {
        listaParaExcluir = lista
        mensagemSucesso = null
    }

    /**
     * Exclui uma lista após confirmação do usuário.
     */
    fun confirmarExclusao(lista: ListaCompra) {
        listas =
            listas.filterNot { listaExistente ->
                listaExistente.id == lista.id
            }

        cancelarExclusao()
        mensagemSucesso = "Lista excluída com sucesso."
    }

    /**
     * Cancela a exclusão atualmente aberta.
     */
    fun cancelarExclusao() {
        listaParaExcluir = null
    }
}

/**
 * Diálogo responsável pela alteração do nome de uma lista.
 *
 * @param nomeEditado Nome atualmente informado pelo usuário.
 * @param erro Mensagem de validação ou `null` quando não há erro.
 * @param onNomeChange Evento disparado quando o nome é alterado.
 * @param onSalvar Evento disparado ao confirmar a alteração.
 * @param onCancelar Evento disparado ao cancelar a alteração.
 */
@Composable
private fun DialogoEditarLista(
    nomeEditado: String,
    erro: String?,
    onNomeChange: (String) -> Unit,
    onSalvar: () -> Unit,
    onCancelar: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            Text("Editar nome da lista?")
        },
        text = {
            OutlinedTextField(
                value = nomeEditado,
                onValueChange = onNomeChange,
                label = {
                    Text("Nome da lista")
                },
                supportingText = {
                    if (erro != null) {
                        Text(erro)
                    }
                },
                isError = erro != null,
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = onSalvar,
                enabled = erro == null,
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancelar,
            ) {
                Text("Cancelar")
            }
        },
    )
}

/**
 * Diálogo responsável pela confirmação da exclusão de uma lista.
 *
 * @param lista Lista selecionada para exclusão.
 * @param onConfirmar Evento disparado ao confirmar a exclusão.
 * @param onCancelar Evento disparado ao cancelar a exclusão.
 */
@Composable
private fun DialogoExcluirLista(
    lista: ListaCompra,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            Text("Excluir lista?")
        },
        text = {
            Text(
                "Tem certeza de que deseja excluir a lista \"${lista.nome}\"?",
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmar,
            ) {
                Text("Excluir")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancelar,
            ) {
                Text("Cancelar")
            }
        },
    )
}
