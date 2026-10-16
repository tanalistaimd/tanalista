# Uso de ferramentas de IA

Registro do que foi feito com auxílio de IA, por quem, e o que a equipe verificou
antes de aceitar. Atualizado **durante** a sprint, não no fechamento.

## Princípios que a equipe segue

1. **Autoria e responsabilidade são de quem submete.** Código gerado por IA entra
   por pull request com autoria humana, e quem abre o PR responde por ele em
   arguição. Nada entra que o autor não consiga explicar.
2. **Revisão humana é obrigatória e é de outra pessoa.** Saída de IA não dispensa
   nenhuma condição da Definição de Pronto — a revisão por outro integrante, a
   análise estática limpa e os testes verdes valem igual.
3. **O que a ferramenta afirma não é verificação.** Build, `ktlintCheck`, `detekt`
   e teste executados são a verificação. Toda afirmação de "funciona" neste
   documento corresponde a um comando que alguém rodou.
4. **Registro contemporâneo.** A tarefa entra aqui quando acontece.

## Sprint 0

**Ferramenta:** Claude Code (Claude Opus 5), no terminal.
**Quem usou:** Thallys.
**Quando:** 15 e 16/09/2026.

| Tarefa | O que a ferramenta fez | Como foi verificado |
| --- | --- | --- |
| Reestruturação para `composeApp/` | Moveu os source sets dos três módulos gerados para um só, trocou o pacote `com.example.tanalista` por `br.ufrn.tanalista` e escreveu o `composeApp/build.gradle.kts` a partir do projeto de exemplo do professor | `./gradlew :composeApp:assembleDebug` e `:composeApp:desktopJar`, executados; APK gerado |
| Análise estática | Configurou ktlint e detekt no build, escreveu `detekt.yml` e `.editorconfig`, corrigiu as 7 violações iniciais | `./gradlew ktlintCheck detekt` limpo, sem avisos |
| Documentação da sprint | Redigiu `contrato-api.md`, os registros em `decisoes/`, o índice `proposta.md` e reescreveu o `README.md` | Lido e revisado por Thallys; as decisões técnicas registradas já haviam sido tomadas pela equipe |
| Evidência do problema | Redigiu a seção a partir do relato das conversas informais feitas por Thallys | O conteúdo é o relato real; a ferramenta não produziu dado, e o texto declara explicitamente que não houve pesquisa estruturada |

**Decisões que a ferramenta tomou e que foram revisadas caso a caso:** desativar
`chain-method-continuation` do ktlint apenas em `*.gradle.kts`; substituir as classes
`AndroidPlatform` e `JVMPlatform` por objetos anônimos para satisfazer o detekt sem
suprimir regra; manter detekt 1.23.8 estável em vez de subir para a linha 2.0.0-alpha.

**O que a ferramenta não fez:** definir a visão do produto, o MVP, o backlog, o
acordo de processo ou as decisões de stack — tudo isso precede o uso da ferramenta e
está registrado nas propostas e em `decisoes/`.

### Limite observado

Em 16/09, o assistente abriu dois pull requests e fez push de branches sem
autorização, ao receber uma instrução que pedia iteração passo a passo. Nada foi
integrado e `main` não foi afetada, mas pull request fechado não é removível no
GitHub. A equipe passou a exigir autorização explícita antes de qualquer publicação
no remoto, e a restrição foi configurada na ferramenta.

Fica registrado porque é o tipo de risco que importa medir: o custo não foi de
código errado, foi de ação irreversível tomada rápido demais.

## Sprints seguintes

Uma seção por sprint, no mesmo formato.

## Sprint 1

**Ferramenta:** OpenCode (GPT-6 Sol). **Quem usou:** Vinicius.

| Tarefa | O que a ferramenta fez | Como foi verificado |
| --- | --- | --- |
| Issues #7 e #8 | Acrescentou limite de 60 caracteres ao nome, listagem ordenada por atualização, resumo de progresso separado da lista reutilizável e testes | `gradlew.bat :composeApp:desktopTest ktlintCheck detekt --no-daemon` passou; build e teste manual no Android dependem de SDK nesta máquina |
| Issue #8, após o merge do #29 (15/10/2026) — OpenCode (GPT-6.1 Sol Fast), solicitado por Vinicius | Centralizou a contagem de itens por lista, atualizou a data da lista após inclusão válida, acrescentou testes de resumo e ordenação, preview vazio e roteiro de verificação no README. A marcação de comprados continua dependente da #14. Commit, push e abertura do PR autorizados explicitamente por Vinicius | `gradlew.bat ktlintCheck detekt :composeApp:desktopTest :composeApp:assembleDebug --no-daemon` passou, com 34 testes. Validação manual no Android e revisão humana por outro integrante ainda pendentes |
| Deep link do detalhe da lista (16/10/2026) — Claude Code (Claude Opus 5.5), solicitado por Thallys | Declarou `navDeepLink<ListaDetalheRota>` com base `tanalista://lista`, o `intent-filter` no manifesto, `singleTask` na Activity e o repasse do link recebido com o app aberto ao `NavController`, para não abrir uma segunda Activity com o estado em memória vazio. Documentou o comando `adb` no README | `ktlintCheck detekt :composeApp:desktopTest :composeApp:assembleDebug` passou. No emulador: link para os ids 1 e 2 com o app aberto abriu a lista certa; id 99 mostrou "Lista não encontrada"; com o app fechado, abriu o detalhe com a tela de listas embaixo na pilha; o Voltar retornou às listas nos dois casos |
| Tema e acessibilidade, #33 (16/10/2026) — Claude Code (Claude Opus 5.5), solicitado por Thallys | Criou o esquema de cor claro e escuro, pôs o `Scaffold` na raiz, acrescentou previews no modo escuro, marcou títulos com `heading()`, deu descrições a Editar, Excluir, Voltar e ao progresso, e anunciou a confirmação de criação. Tornou a tela de listas rolável por inteiro e declarou `configChanges` na Activity, depois que a troca de tema no emulador apagou as listas em memória. Excluiu `ui/tema/` da regra `MagicNumber` do detekt | `ktlintCheck detekt :composeApp:desktopTest :composeApp:assembleDebug` passou. No emulador: capturas nos modos claro e escuro; listas preservadas após trocar o modo e girar; tela deitada rolando sem cortar o cartão; descrições conferidas no dump de acessibilidade (`uiautomator`). O percurso com o TalkBack fica com quem revisa e apresenta o vídeo |
