package br.ufrn.tanalista

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import br.ufrn.tanalista.model.ListaCompra
import br.ufrn.tanalista.model.resumirListas
import br.ufrn.tanalista.navigation.BASE_DEEP_LINK_LISTA
import br.ufrn.tanalista.navigation.ExternalUriHandler
import br.ufrn.tanalista.navigation.ListaDetalheRota
import br.ufrn.tanalista.navigation.ListasRota
import br.ufrn.tanalista.ui.item.FormularioItem
import br.ufrn.tanalista.ui.item.ItensState
import br.ufrn.tanalista.ui.item.ListaDetalheScreen
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
    val itensState = remember { ItensState() }
    val navController = rememberNavController()

    DisposableEffect(navController) {
        ExternalUriHandler.ouvinte = { uri -> navController.navigate(NavUri(uri)) }
        onDispose { ExternalUriHandler.ouvinte = null }
    }

    MaterialTheme {
        NavHost(navController = navController, startDestination = ListasRota) {
            composable<ListasRota> {
                ListasDestino(
                    state = state,
                    itensState = itensState,
                    onAbrirLista = { lista -> navController.navigate(ListaDetalheRota(lista.id)) },
                )
            }

            composable<ListaDetalheRota>(
                deepLinks = listOf(navDeepLink<ListaDetalheRota>(basePath = BASE_DEEP_LINK_LISTA)),
            ) { entrada ->
                val rota = entrada.toRoute<ListaDetalheRota>()
                var formulario by remember { mutableStateOf(FormularioItem()) }

                ListaDetalheScreen(
                    lista = state.listas.find { it.id == rota.listaId },
                    itens = itensState.daLista(rota.listaId),
                    formulario = formulario,
                    onFormularioChange = { formulario = it },
                    onAdicionar = {
                        if (itensState.adicionar(rota.listaId, formulario)) {
                            state.registrarAlteracao(rota.listaId)
                            formulario = FormularioItem()
                        }
                    },
                    onVoltar = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun ListasDestino(
    state: ListaAppState,
    itensState: ItensState,
    onAbrirLista: (ListaCompra) -> Unit,
) {
    ListaFormScreen(
        listas = resumirListas(state.listas, itensState.itens),
        nome = state.nome,
        erro = state.erroCriacao,
        mensagemSucesso = state.mensagemSucesso,
        onNomeChange = state::alterarNome,
        onCriar = state::criarLista,
        onAbrirLista = onAbrirLista,
        onEditarLista = state::iniciarEdicao,
        onExcluirLista = state::solicitarExclusao,
    )

    state.listaParaEditar?.let { lista ->
        DialogoEditarLista(
            nomeEditado = state.nomeEditado,
            erro = state.erroEdicao,
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
                itensState.removerDaLista(lista.id)
            },
            onCancelar = state::cancelarExclusao,
        )
    }
}

/**
 * Mantém e manipula o estado relacionado às listas de compras.
 *
 * Concentra as regras de criação, edição e exclusão para evitar
 * que o componente principal acumule responsabilidades.
 */
internal class ListaAppState(
    private val agora: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) {
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

    /** Atualiza a ordenação após uma alteração efetiva nos itens da lista. */
    fun registrarAlteracao(listaId: Int) {
        listas =
            listas.map { lista ->
                if (lista.id == listaId) lista.copy(atualizadaEm = agora()) else lista
            }
    }

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
                atualizadaEm = agora(),
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
    val erroEdicao: String?
        get() =
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
                            listaExistente.id != listaParaEditar?.id
                        },
                ) ->
                    "Já existe uma lista com esse nome."

                else -> null
            }

    /**
     * Salva a alteração realizada no nome da lista.
     */
    fun salvarEdicao(lista: ListaCompra) {
        if (erroEdicao != null) {
            return
        }

        listas =
            listas.map { listaExistente ->
                if (listaExistente.id == lista.id) {
                    listaExistente.copy(
                        nome = nomeEditado.trim(),
                        atualizadaEm = agora(),
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
