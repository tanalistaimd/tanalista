# Proposta — TáNaLista

**DIM0510 — Processos de Software · Sprint 0 · 2026.2**

> Visão do produto, MVP e backlog são comuns à proposta de DIM0524. O acordo de processo (§5) é o artefato exclusivo desta disciplina.

---

## 1. Visão do produto

```text
Para quem faz a compra da casa
Que sai com a lista no papel ou no bloco de notas
  e só descobre quanto gastou no caixa
O TáNaLista é um aplicativo de listas de compras com modo mercado
Que soma o carrinho em tempo real enquanto os itens são marcados
Diferente de aplicativos de lista que só registram nomes de produtos
Nosso produto separa planejar de comprar e funciona sem sinal no corredor
```

**Hipótese de valor.** Acreditamos que quem faz a compra do mês vai abrir o aplicativo no corredor, e não só em casa, porque saber o total antes do caixa evita devolver item na fila.

**Público.** Domicílios com uma compra grande mensal e complementos semanais, em que mais de uma pessoa mexe na mesma lista.

**Evidência do problema.** A origem do produto é observação direta, e é assim que ela deve ser lida: conversamos informalmente com colegas de curso, com familiares que fazem a compra da casa e entre os próprios integrantes da equipe. Não houve roteiro, amostra nem registro sistemático.

O relato se repetiu nos três grupos. Em lares com jovens adultos, a lista de compras simplesmente não existe de forma organizada — ninguém tem tempo de sentar e escrever tudo à mão. O que sobra é memória, mensagem solta no grupo da família e papel que se perde no caminho. Compra-se repetido, esquece-se o essencial, e o quanto se gastou só aparece no caixa.

Duas consequências entraram direto na modelagem:

1. **A lista precisa ser reutilizável.** Se o custo de escrever a lista é justamente o que impede a lista de existir, o produto não pode cobrar esse custo de novo todo mês. É o que sustenta a separação entre `Lista` e `Compra`: a lista é escrita uma vez e reaproveitada, e cada ida ao mercado é um registro novo sobre ela.
2. **Mais de uma pessoa mexe na mesma lista.** "Manda no grupo o que faltou" é compartilhamento já acontecendo, fora de qualquer ferramenta. Lista compartilhada não é recurso avançado aqui: é o comportamento atual, mal servido.

**O que esta evidência ainda não é.** Conversa informal não mede frequência, não quantifica gasto excedente e não prova que o total no corredor seja o dado que falta — isso permanece hipótese da equipe, não achado. A Sprint 1 abre com um roteiro curto de entrevista, aplicado às mesmas pessoas e registrado em `docs/pesquisa/`, e a priorização do backlog passa a ser revisitada contra esse dado, não contra preferência técnica. Se a entrevista mostrar que o obstáculo real é montar a lista, e não acompanhar o total, a ordem do backlog muda — e essa inversão é um resultado aceitável, não um fracasso.

---

## 2. Definição do MVP

| No MVP | Fora do MVP |
| --- | --- |
| Criar, renomear e excluir listas, com nome único | Catálogo global de produtos e preços de mercado |
| Itens com quantidade, unidade, categoria e preço estimado | Comparação de preços entre estabelecimentos |
| Bloqueio de nome equivalente, de lista e de item | Sugestão automática de produtos recorrentes |
| Modo Mercado: marcar, preço encontrado, quantidade, subtotal e total | Orçamento máximo com alerta |
| Proteção de alteração estrutural durante o Modo Mercado | Edição simultânea em tempo real |
| Uso integralmente offline, com fila de escrita e sincronização | Notificações |
| Conta de usuário e lista compartilhada com outra conta | Estatísticas e painéis de gasto |
| Histórico de compras finalizadas, com total e data | Integração com e-commerce ou pagamento |
| Leitura de código de barras para adicionar item | iOS como plataforma prioritária |
| Exportar e compartilhar uma lista pelo seletor do sistema | |

**Critérios de sucesso.** Uma compra real de mês registrada de ponta a ponta pelo aplicativo, com o total conferindo com o cupom fiscal; uma lista compartilhada e usada por duas pessoas; nenhuma perda de marcação em uso offline com o modo avião ligado durante toda a compra.

---

## 3. Backlog inicial

Quadro no GitHub Projects: <https://github.com/users/thallystorres/projects/2>. O backlog é o mesmo de DIM0524 — a diferença entre as disciplinas está nos artefatos de processo, não no produto.

| Prio | História | Critérios de aceitação | Est. | Sprint |
| --- | --- | --- | --- | --- |
| P1 | Como usuário, quero criar uma lista para separar compras por ocasião | Nome obrigatório; duplicidade bloqueada por nome normalizado | 3 | 1 |
| P1 | Como usuário, quero ver minhas listas com o progresso para saber o que falta | Comprados sobre total; ordenadas por atualização | 3 | 1 |
| P1 | Como usuário, quero adicionar itens com quantidade, unidade e categoria | Só o nome é obrigatório; quantidade positiva quando informada | 5 | 1 |
| P1 | Como usuário, quero ser impedido de repetir um produto na mesma lista | Nome normalizado; `Leite 200g` e `Leite 400g` seguem distintos | 3 | 1 |
| P1 | Como usuário, quero editar e remover itens para corrigir a lista | Todos os campos editáveis; remoção com desfazer | 2 | 1 |
| P1 | Como usuário, quero renomear e excluir uma lista | Exclusão leva os itens junto, com confirmação | 2 | 1 |
| P1 | Como usuário, quero ativar o Modo Mercado para executar a compra | Interface passa a priorizar leitura e toque grandes | 5 | 2 |
| P1 | Como usuário, quero marcar item comprado e informar o preço encontrado | Subtotal por item; total soma apenas comprados | 5 | 2 |
| P1 | Como usuário, quero acompanhar o total do carrinho para não estourar no caixa | Sempre visível, atualizado a cada marcação | 3 | 2 |
| P2 | Como usuário, quero que alteração estrutural peça confirmação no Modo Mercado | Renomear, adicionar e remover exigem confirmação; preço e quantidade não | 3 | 2 |

As outras sete histórias — conta de usuário, uso offline, sincronização ao reconectar, lista compartilhada, histórico de compras, leitura de código de barras e exportação — estão no quadro, priorizadas e alocadas à Sprint 3 e à entrega final. São **17 histórias no total, todas priorizadas, 10 estimadas**. P1 é essencial ao MVP, P2 é importante, P3 é desejável.

---

## 4. Stack tecnológico

Kotlin Multiplatform com Compose Multiplatform, alvos Android e desktop; SQLDelight para persistência local; Ktor Client para rede; Koin para injeção de dependências; GitHub Actions para integração contínua; ktlint e detekt como análise estática; `kotlin.test` e Turbine para testes. O backend é a API do próprio grupo em DIM0547, em monorepo separado. A justificativa de cada escolha está em [`dim0524/proposta.md`](../dim0524/proposta.md) e em [`decisoes/`](../decisoes/), já que decorrem das características do produto.

O que importa **nesta** disciplina é que a stack torna as práticas de processo mensuráveis: análise estática e testes de domínio rodam sem emulador, o que viabiliza o portão de qualidade no pipeline desde a Sprint 1; o GitHub Actions fornece frequência de implantação e tempo de espera para as métricas DORA da Sprint 2; e a dependência entre dois repositórios, com o backend concentrado em um integrante, cria uma fila de espera real — objeto do VSM da Sprint 3.

---

## 5. Acordo de processo

### Composição da equipe

Os três cursam DIM0510 e DIM0524; Vinicius cursa também DIM0547, onde o backend do mesmo produto é avaliado. A equipe é uma só aqui: Vinicius não é fornecedor externo de uma API, ele puxa item do aplicativo como qualquer outro, e o backend é trabalho **adicional** dele.

Daí duas regras que sustentam o resto. **O backend não desconta da participação aqui** — nenhuma sprint fecha com Vinicius sem PR integrado neste repositório, e o planejamento reserva capacidade menor para ele por isso. E **o repositório do backend não é território privado** — Thallys e Ivis abrem issue, revisam PR e programam em par lá, para que o contrato não dependa de uma cabeça só.

### Apoio ao backend

Thallys e Ivis contribuem no monorepo de Web II **com homologação do docente**, registrada aqui e no README de lá. Como aquela disciplina avalia Vinicius individualmente, o apoio segue três regras, para ser legível a quem avalia:

1. **Autoria explícita** — trabalho em par com `Co-authored-by:` no commit, conforme AVALIACAO.md §3.3.
2. **Vinicius autora o PR e responde pela decisão** — o apoio é mão de obra e discussão, não terceirização: todo arquivo de lá continua sendo algo que ele explica sob arguição.
3. **Escopo restrito** a contrato, DTOs, testes contra o contrato e dados de teste — não às decisões de arquitetura que DIM0547 avalia como competência dele.

Esse apoio consome capacidade que não apareceria no quadro daqui, e trabalho invisível distorce velocidade e VSM. Por isso entra no quadro como Tarefa com a etiqueta `apoio-backend`: **não** conta como valor entregue nem entra na velocidade, mas ocupa WIP e é somado no fechamento — a Sprint 3 vai perguntar para onde foi o tempo da equipe, e a resposta precisa incluir essas horas.

### Cadência e cerimônias

A sprint da equipe coincide com a da disciplina: planejamento na segunda que abre, fechamento na sexta da entrega, com uma sincronização no meio.

| Cerimônia | Quando | Duração | Saída |
| --- | --- | --- | --- |
| Planejamento | Início da sprint | 60 min | Itens no Sprint Backlog, com estimativa e capacidade por pessoa |
| Sincronização | Semanal | 20 min | Impedimentos como issue; estado de cada endpoint atualizado no contrato |
| Revisão | Sexta da entrega | 30 min | Incremento demonstrado e gravado |
| Retrospectiva | Sexta da entrega | 30 min | Uma ação de melhoria, como issue com responsável |

Todas com os três integrantes. **Retrospectiva sem ação registrada não conta como realizada** — a ação vira item do quadro, para que o efeito seja confrontado com dado na sprint seguinte. A sincronização tem pauta fixa de um item: que endpoints saíram do contrato para a implementação, e que histórias isso desbloqueia. É a única reunião que não pode virar mensagem.

### Papéis

Sem especialização por camada: os três tocam interface, dados e pipeline, para que nenhum item fique bloqueado na ausência de um.

| Papel | Sprint 0 | Rotatividade |
| --- | --- | --- |
| Product Owner | Thallys | Rodízio a cada sprint |
| Scrum Master | Ivis | Rodízio a cada sprint |
| Mantenedor do pipeline e das métricas | Vinicius | Rodízio a cada sprint |
| Guardião do contrato da API | Vinicius | **Fixo no semestre** |
| Revisor de código | Rodízio entre os outros dois | Um PR nunca é revisado pelo autor |
| Condução da apresentação e redação | Alterna | A cada sprint |

Os três primeiros giram para que cada um exerça todos ao longo das quatro sprints. O contrato é a única atribuição fixa, porque quem implementa o backend é quem sabe o que ele pode prometer — e disso decorre que **mudança de contrato combinada em conversa não existe**: só vale entrando em `docs/contrato-api.md` por PR, revisado por quem não é o guardião, com a issue vinculada.

O rodízio de revisão importa por um motivo concreto: com três pessoas, é fácil um par se formar e o terceiro revisar todo o resto. A ordem é registrada no planejamento, e a sincronização confere se cada um já revisou PR de cada um dos outros dois.

### Definição de Pronto

Um item sai de "Em revisão" quando **todas** valem:

1. Integrado em `main` por pull request vinculado à issue
2. Revisado e aprovado por outro integrante, seguindo o rodízio, com ao menos um comentário substantivo
3. `ktlintCheck` e `detekt` limpos no GitHub Actions
4. Testes automatizados do item escritos e verdes no CI
5. Critérios de aceitação verificados manualmente no dispositivo
6. Documentação afetada atualizada no mesmo PR
7. Se consome a API: endpoint no contrato e teste contra a implementação falsa, de modo que o item seja demonstrável com o backend fora do ar

Nenhuma condição admite exceção por prazo. Item que não cumpre volta para "Em progresso".

### Fluxo do quadro e limites de trabalho em curso

| Coluna | O que autoriza entrar | WIP |
| --- | --- | --- |
| Backlog | Priorizado pelo PO | — |
| Sprint Backlog | Comprometido no planejamento | — |
| Em progresso | Alguém assumiu o item | **3** |
| Em revisão | PR aberto e CI verde | **2** |
| Pronto | Definição de Pronto cumprida | — |

Três em progresso é um item por pessoa. O limite de revisão é deliberadamente menor: deixar a fila crescer até três, com três pessoas, significa que ninguém está revisando. Com o limite atingido, revisar o PR do colega tem prioridade sobre iniciar item novo.

A etiqueta `aguardando-contrato` marca item que depende de endpoint indisponível. Ele **não** ocupa vaga de WIP, porque não há trabalho em curso nele, e o tempo etiquetado é medido — é a medida direta de espera entre os dois repositórios no VSM da Sprint 3.

A etiqueta também dispara uma ação: **dois itens simultâneos em `aguardando-contrato` autorizam quem estiver livre a puxar `apoio-backend` em vez de iniciar história nova**. É atacar o gargalo em vez de acumular estoque na frente dele, e só é possível porque o apoio ao monorepo está homologado. Sem essa válvula, a equipe responderia à espera abrindo mais frentes no app — o comportamento que infla o trabalho em curso e alonga o lead time de tudo. A hipótese que a Sprint 3 vai confirmar ou derrubar com dado é que o gargalo não está no app nem na API, mas na disponibilidade de uma pessoa para os dois.

### Ferramentas

| Uso | Ferramenta |
| --- | --- |
| Código e revisão | GitHub — dois repositórios, vinculados nos READMEs |
| Quadro e backlog | GitHub Projects |
| Contrato entre app e API | `docs/contrato-api.md`, espelhando o OpenAPI do monorepo |
| Automação e métricas | GitHub Actions |
| Conversa do dia a dia | WhatsApp |
| Decisões técnicas | `docs/decisoes/` — um registro curto por decisão |
| Uso de IA | `docs/uso-de-ia.md`, atualizado durante a sprint |

Decisão tomada em conversa e não registrada é tratada como não tomada.

### Riscos identificados

| Risco | Efeito | Mitigação |
| --- | --- | --- |
| Cronogramas de duas disciplinas acoplados | App bloqueado esperando endpoint | Contrato antes da implementação; implementação falsa; item bloqueado sai do WIP e é medido |
| Vinicius acumula o backend com as sprints daqui | Sobrecarga e queda de participação neste repositório | Capacidade menor reservada a ele; nenhuma sprint fecha sem PR dele aqui; apoio homologado como `apoio-backend` |
| Apoio ao monorepo apagar a autoria dele em DIM0547 | Avaliação individual prejudicada | `Co-authored-by:` obrigatório; PR sempre autorado por ele; escopo restrito |
| Escopo do Modo Mercado crescer | Sprint 2 estoura | Modo Mercado fatiado em quatro histórias entregáveis separadamente |
| Conflito de sincronização mal resolvido | Perda de marcação de compra | Política de conflito escrita antes do código; registro de compra idempotente |
| Concentração de commits na véspera | Componente B penalizado | Sincronização semanal confere movimentação do quadro; item parado há mais de uma semana é discutido |

---

## 6. Equipe

| Nome | Matrícula | Conta GitHub | Papel |
| --- | --- | --- | --- |
| Thallys | 20240011552 | thallystorres | Developer · Product Owner na Sprint 0 |
| Ivis | 20220028454 | ivixs | Developer · Scrum Master na Sprint 0 |
| Vinicius | 20230051760 | vbarbosadev | Developer · mantenedor do pipeline na Sprint 0 · guardião do contrato da API |

Os três cursam DIM0510 e DIM0524. Vinicius cursa também DIM0547, onde responde pelo backend do mesmo produto.

---

## 7. Coorte e integração

**Coorte de apresentação:** B — online

**Quadro no GitHub Projects:** <https://github.com/users/thallystorres/projects/2>

**Integração entre disciplinas:** o mesmo produto é desenvolvido em **DIM0524 — Sistemas para Dispositivos Móveis**, pela equipe inteira, e em **DIM0547 — Desenvolvimento de Sistemas Web II**, por Vinicius. Os entregáveis são distintos: lá o objeto avaliado é o aplicativo e o backend; aqui, o processo — acordo de processo, métricas de fluxo, DORA, VSM, retrospectivas e relatório final —, artefatos que existem apenas nesta disciplina. Repositórios vinculados nos READMEs.