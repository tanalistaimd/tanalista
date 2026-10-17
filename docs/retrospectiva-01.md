# Retrospectiva da Sprint 1

Sprint de 14/09 a 16/10/2026. Retrospectiva feita em 16/10 com Thallys, Ivis e
Vinicius, com o quadro e as métricas abertos.

## 1. Fatos

### Entregue

| Issue | História | PR | Primeiro commit | Integração |
| --- | --- | --- | --- | --- |
| #7 | Criar lista de compras | #27 | 23/09 | 28/09 |
| #12 | Renomear e excluir lista | #28 | 28/09 | 13/10 |
| #9 | Adicionar itens à lista | #29 | 13/10 | 15/10 |
| #10 | Impedir produtos duplicados na mesma lista | #29 | 13/10 | 15/10 |
| #8 | Visualizar listas com progresso da compra | #30 | 15/10 | 16/10 |
| #32 | Deep link para o detalhe da lista | #31 | 16/10 | 16/10 |
| #33 | Tema claro e escuro e acessibilidade | #35 (integrado pelo #36) | 16/10 | 16/10 |

Em 16/10, `develop` foi integrada em `main` pelo #37.

Fora da sprint, devolvidas ao Backlog em 16/10: #11 (editar e remover itens), o
Modo Mercado (#13 a #16), conta e sincronização (#17 a #19) e as histórias P2 e P3
(#20 a #23). O layout em duas larguras de janela (#34) também ficou para a Sprint 2.

### Métricas de fluxo

Método: o início de um cartão é o primeiro commit da branch do PR que o fecha. O
fim é a integração do PR em `develop`. A chegada ao quadro é a criação da issue.
Os dados vêm do histórico do git e dos PRs.

As médias de lead e cycle time consideram as 5 histórias do backlog. As tarefas #32 e
#33 foram criadas e entregues no mesmo dia (0 dia de lead e de cycle time); somá-las
baixaria o lead time médio para 18,1 dias sem refletir o fluxo real da sprint.

| Métrica | Valor |
| --- | --- |
| Cycle time médio das histórias (início → integração) | 5,0 dias (mediana 2 dias) |
| Lead time médio das histórias (chegada ao quadro → integração) | 25,4 dias |
| Throughput | 7 itens em 5 semanas (5 histórias e 2 tarefas), 1,4 por semana |
| Throughput por semana | 0 · 1 · 0 · 0 · 6 |
| WIP médio (Lei de Little: 1,4 item/semana × 3,6 dias de cycle time dos 7 itens) | ≈ 0,7 item |

- O cartão mais lento foi o #12: 15 dias de ciclo. O PR #28 abriu em 30/09 com o CI
  vermelho, foi corrigido em 06/10 e só foi aprovado em 13/10. Ele passou 13 dias
  em "Em revisão".
- Entre 06/10 e 13/10, nenhum commit entrou no repositório.
- Seis dos sete itens foram integrados na última semana da sprint, três deles no último dia.
- A diferença entre lead time (25 dias) e cycle time (5 dias) mostra que o tempo foi
  gasto **esperando no Sprint Backlog**, não trabalhando. Exemplo: o #9 ficou 27 dias
  parado antes do primeiro commit.
- O WIP nunca passou do limite (3 em progresso, 2 em revisão). O problema não foi
  excesso de trabalho simultâneo, foi trabalho parado.

### Quadro e revisão

- O Sprint Backlog recebeu as 17 histórias do backlog no início da sprint. Foram
  entregues 5; as outras 12 voltaram ao Backlog em 16/10, com o motivo comentado em
  cada issue.
- As tarefas técnicas da rubrica de DIM0524 (deep link, tema, larguras de janela,
  acessibilidade) só viraram cartões em 16/10.
- Dos 8 PRs integrados na sprint, 4 tiveram aprovação de outro integrante (#28, #29,
  #30 e #37). O #27 foi integrado sem revisão, e o #31, o #35 e o #36 foram integrados
  sem aprovação registrada no último dia, para caber no prazo.
- O #35 foi aberto com base em `feat/deep-link`, empilhado sobre o #31. Por isso o
  `Closes #33` não fechou a issue, que foi fechada à mão.
- O #27 e o #30 não referenciam a issue com `Closes`: o #7 e o #8 foram fechados à
  mão, e o vínculo entre cartão e PR não aparece no quadro.
- `main` não recebeu nenhuma integração durante a sprint até o #37, no último dia.
  Até então, a branch avaliada continha só o commit inicial.

## 2. Causas

### Por que o trabalho se concentrou na última semana?

1. Por que seis dos sete itens foram integrados na última semana? Porque o trabalho nelas
   só começou em 13 e 15/10.
2. Por que começou tão tarde? Porque o #28 ficou parado em revisão de 30/09 a 13/10, e
   as histórias seguintes dependiam do estado das listas que ele alterava.
3. Por que o #28 ficou parado? Porque o CI falhou e ninguém tinha a tarefa de olhar
   PRs abertos. A revisão acontecia quando alguém lembrava.
4. Por que ninguém tinha essa tarefa? Porque o acordo de processo define o limite de WIP
   em revisão, mas não um prazo para revisar.
5. Por que não percebemos isso antes? Porque o adiamento da entrega de 02/10 para 16/10
   tirou a pressão do prazo, e não havia uma métrica acompanhada durante a sprint.

**Causa principal:** não existe prazo de revisão, e o fluxo só foi medido no fim. A
mesma causa explica os três PRs integrados sem aprovação no último dia: sem prazo de
revisão ao longo da sprint, a revisão ficou para quando já não havia tempo.

### Por que as tarefas da rubrica de DIM0524 só apareceram no último dia?

1. Por que só viraram cartões em 16/10? Porque o planejamento da sprint partiu das
   histórias de usuário do backlog.
2. Por que isso as deixou de fora? Porque deep link, tema, larguras de janela e
   acessibilidade são critérios de avaliação, não histórias de usuário.
3. Por que não cruzamos o backlog com a rubrica? Porque o planejamento não tinha esse
   passo, e as tarefas da disciplina (`SPRINT-1-TAREFAS.md`) não foram transformadas
   em cartões na T1.

**Causa principal:** o planejamento não usa a rubrica como entrada.

### Por que o Sprint Backlog recebeu todo o backlog?

Não havia throughput medido para estimar quanto cabia na sprint, e mover tudo de uma
vez pareceu mais simples que escolher.

## 3. Ações

| # | Ação | Responsável | Prazo | Como verificar |
| --- | --- | --- | --- | --- |
| 1 | No primeiro dia da Sprint 2, transformar cada critério das rubricas e cada tarefa de `SPRINT-2-TAREFAS.md` em cartão com dono. O Sprint Backlog recebe no máximo o dobro do throughput medido. | Thallys | 03/11 | Data de criação dos cartões no quadro |
| 2 | Revisão em até 48 horas: PR aberto há mais de dois dias entra na daily. Ativar a proteção de branch com 1 aprovação e o CI obrigatórios. | Ivis | 05/11 | Tempo médio entre abertura e aprovação dos PRs da Sprint 2 menor que 2 dias; nenhum PR integrado sem aprovação |
| 3 | Integrar `develop` em `main` toda sexta-feira, por PR, com o CI verde em `main` depois de cada integração. | Vinicius | 07/11, depois semanal | PRs de `develop` para `main` com intervalo de no máximo 7 dias |

## 4. Revisão do acordo de processo

- **Prática XP adotada:** revisão de código, com integração contínua. Todo PR passa
  pelo CI (`ktlintCheck`, `detekt`, build) e precisa da aprovação de outro integrante,
  com um comentário sobre o código. Na Sprint 1, o CI barrou o #28 até a correção, e
  a revisão do #30 incluiu um teste manual no emulador com os passos do PR.
- **Prazo de revisão:** 48 horas, acrescentado à política da coluna "Em revisão".
- **Planejamento:** a rubrica da sprint entra no planejamento junto com o backlog.
- **Branch principal:** `main` passa a ser a branch padrão e recebe `develop`
  semanalmente.
- Os limites de WIP continuam em 3 (em progresso) e 2 (em revisão): não foram o
  gargalo.
