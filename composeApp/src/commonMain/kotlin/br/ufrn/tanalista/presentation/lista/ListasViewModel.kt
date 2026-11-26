package br.ufrn.tanalista.presentation.lista

import br.ufrn.tanalista.domain.RepositorioListas
import br.ufrn.tanalista.domain.model.ListaComProgresso
import br.ufrn.tanalista.domain.model.ListaCompra
import br.ufrn.tanalista.domain.model.resumirListas
import br.ufrn.tanalista.domain.validation.ListaCompraValidator
import br.ufrn.tanalista.presentation.EstadoTela
import br.ufrn.tanalista.presentation.TelaComRepositorioViewModel
import br.ufrn.tanalista.presentation.conteudoDisponivel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

internal class ListasViewModel(
    repositorio: RepositorioListas,
) : TelaComRepositorioViewModel(repositorio) {
    private val campos = MutableStateFlow(CamposListas())

    val estado: StateFlow<EstadoTela<ConteudoListas>> =
        observar(campos) { dados, valores, aviso ->
            val edicao = valores.dialogo as? DialogoLista.Edicao
            val listas = resumirListas(dados.listas, dados.itens, dados.compras)
            val conteudo =
                ConteudoListas(
                    listas = listas,
                    nome = valores.nome,
                    erroCriacao = erroNomeLista(valores.nome, dados.listas, valores.tentouCriar),
                    mensagemSucesso = valores.mensagemSucesso,
                    dialogo = valores.dialogo,
                    erroEdicao =
                        edicao?.let {
                            erroNomeLista(
                                it.nome,
                                dados.listas.filterNot { l ->
                                    l.id == it.lista.id
                                },
                            )
                        },
                    aviso = aviso,
                )
            conteudoDisponivel(conteudo, vazio = listas.isEmpty())
        }

    fun alterarNome(nome: String) = campos.update { it.copy(nome = nome, tentouCriar = false, mensagemSucesso = null) }

    fun criarLista() {
        campos.update { it.copy(tentouCriar = true, mensagemSucesso = null) }
        val nome = campos.value.nome
        if (erroNomeLista(nome, repositorio.dados.value.listas) != null) return

        gravar {
            repositorio.criarLista(nome)
            campos.update {
                if (it.nome == nome) {
                    it.copy(nome = "", tentouCriar = false, mensagemSucesso = "Lista criada com sucesso.")
                } else {
                    it
                }
            }
        }
    }

    fun iniciarEdicao(lista: ListaCompra) =
        campos.update { it.copy(dialogo = DialogoLista.Edicao(lista, lista.nome), mensagemSucesso = null) }

    fun alterarNomeEditado(nome: String) =
        campos.update { valores ->
            val edicao = valores.dialogo as? DialogoLista.Edicao ?: return@update valores
            valores.copy(dialogo = edicao.copy(nome = nome))
        }

    fun salvarEdicao() {
        val edicao = campos.value.dialogo as? DialogoLista.Edicao ?: return
        val outrasListas =
            repositorio.dados.value.listas
                .filterNot { it.id == edicao.lista.id }
        if (erroNomeLista(edicao.nome, outrasListas) != null) return
        gravar {
            repositorio.renomearLista(edicao.lista.id, edicao.nome)
            concluirDialogo(edicao, "Lista editada com sucesso.")
        }
    }

    fun solicitarExclusao(lista: ListaCompra) =
        campos.update { it.copy(dialogo = DialogoLista.Exclusao(lista), mensagemSucesso = null) }

    fun confirmarExclusao() {
        val exclusao = campos.value.dialogo as? DialogoLista.Exclusao ?: return
        gravar {
            repositorio.excluirLista(exclusao.lista.id)
            concluirDialogo(exclusao, "Lista excluída com sucesso.")
        }
    }

    fun fecharDialogo() = campos.update { it.copy(dialogo = null) }

    private fun concluirDialogo(
        dialogo: DialogoLista,
        mensagem: String,
    ) = campos.update { if (it.dialogo == dialogo) it.copy(dialogo = null, mensagemSucesso = mensagem) else it }
}

private data class CamposListas(
    val nome: String = "",
    val tentouCriar: Boolean = false,
    val mensagemSucesso: String? = null,
    val dialogo: DialogoLista? = null,
)

internal data class ConteudoListas(
    val listas: List<ListaComProgresso>,
    val nome: String,
    val erroCriacao: String?,
    val mensagemSucesso: String?,
    val dialogo: DialogoLista?,
    val erroEdicao: String?,
    val aviso: String?,
)

internal sealed interface DialogoLista {
    data class Edicao(
        val lista: ListaCompra,
        val nome: String,
    ) : DialogoLista

    data class Exclusao(
        val lista: ListaCompra,
    ) : DialogoLista
}

private fun erroNomeLista(
    nome: String,
    listas: List<ListaCompra>,
    exigirNome: Boolean = true,
): String? =
    when {
        exigirNome && nome.isBlank() -> "O nome da lista é obrigatório."
        nome.trim().length > ListaCompraValidator.LIMITE_NOME ->
            "O nome deve ter no máximo ${ListaCompraValidator.LIMITE_NOME} caracteres."
        nome.isNotBlank() && ListaCompraValidator.nomeJaExiste(nome, listas) ->
            "Já existe uma lista com esse nome."
        else -> null
    }
