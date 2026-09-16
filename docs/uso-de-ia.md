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
