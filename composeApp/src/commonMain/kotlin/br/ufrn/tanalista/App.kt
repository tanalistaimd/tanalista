package br.ufrn.tanalista

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import br.ufrn.tanalista.model.ListaCompra
import br.ufrn.tanalista.ui.lista.ListaFormScreen
import br.ufrn.tanalista.validation.ListaCompraValidator

/**
 * Componente raiz da interface compartilhada do TáNaLista.
 *
 * Nesta etapa da aplicação, mantém em memória o estado das listas
 * e controla o formulário de criação de uma nova lista.
 *
 * Os dados permanecem em memória porque persistência e rede
 * não fazem parte da Sprint 1.
 */
@Composable
fun App() {
    // Nome atualmente digitado no formulário.
    var nome by remember { mutableStateOf("") }

    // Listas criadas durante a execução atual do aplicativo.
    var listas by remember { mutableStateOf(emptyList<ListaCompra>()) }

    // Indica se o usuário já tentou enviar o formulário.
    var tentouCriar by remember { mutableStateOf(false) }

    // Mensagem exibida após a criação bem-sucedida de uma lista.
    var mensagemSucesso by remember { mutableStateOf<String?>(null) }

    /*
     * Verifica duplicidade somente quando algum nome foi informado.
     * A comparação utiliza a normalização definida no validator.
     */
    val nomeDuplicado =
        nome.isNotBlank() &&
            ListaCompraValidator.nomeJaExiste(
                nome = nome,
                listasExistentes = listas,
            )

    /*
     * A validade do formulário é derivada do estado atual.
     * Não é criado um segundo estado apenas para armazenar erros.
     */
    val erro =
        when {
            tentouCriar && nome.isBlank() -> "O nome da lista é obrigatório."
            nomeDuplicado -> "Já existe uma lista com esse nome."
            else -> null
        }

    MaterialTheme {
        ListaFormScreen(
            nome = nome,
            erro = erro,
            mensagemSucesso = mensagemSucesso,
            onNomeChange = { novoNome ->
                nome = novoNome
                tentouCriar = false
                mensagemSucesso = null
            },
            onCriar = {
                // Marca que o usuário tentou enviar o formulário.
                tentouCriar = true

                /*
                 * A lista só é criada quando o nome é válido
                 * e não existe outra lista equivalente.
                 */
                if (
                    ListaCompraValidator.nomeEhValido(nome) &&
                    !nomeDuplicado
                ) {
                    val novaLista =
                        ListaCompra(
                            id = listas.size + 1,
                            nome = nome.trim(),
                        )

                    listas = listas + novaLista
                    nome = ""
                    tentouCriar = false
                    mensagemSucesso = "Lista criada com sucesso."
                }
            },
        )
    }
}
