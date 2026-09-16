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

**Evidência do problema.** _[Preencher: entrevistas com pessoas que fazem a compra do mês — como montam a lista hoje, se estimam o total antes do caixa, com que frequência devolvem item.]_ A priorização do backlog será revisitada a cada sprint contra essas entrevistas, e não contra preferência técnica da equipe.

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

Quadro no GitHub Projects: _[link]_. O backlog é o mesmo de DIM0524 — a diferença entre as disciplinas está nos artefatos de processo, não no produto.

| Prio | História | Critérios de aceitação | Est. | Sprint |
| --- | --- | --- | --- | --- |
| P1 | Como usuário, quero criar uma lista para separar compras por ocasião | Nome obrigatório, com limite; duplicidade bloqueada por nome normalizado | 3 | 1 |
| P1 | Como usuário, quero ver minhas listas com o progresso para saber o que falta | Nome, número de itens, comprados sobre total, ordenadas por atualização | 3 | 1 |
| P1 | Como usuário, quero adicionar itens com quantidade, unidade e categoria | Só o nome é obrigatório; quantidade positiva quando informada | 5 | 1 |
| P1 | Como usuário, quero ser impedido de repetir um produto na mesma lista | Bloqueio por nome normalizado; `Leite 200g` e `Leite 400g` aceitos | 3 | 1 |
| P1 | Como usuário, quero editar e remover itens para corrigir a lista | Edição de todos os campos; remoção com desfazer | 2 | 1 |
| P1 | Como usuário, quero renomear e excluir uma lista | Exclusão remove os itens associados, com confirmação | 2 | 1 |
| P1 | Como usuário, quero ativar o Modo Mercado para executar a compra | Interface passa a priorizar leitura e toque grandes | 5 | 2 |
| P1 | Como usuário, quero marcar item comprado e informar o preço encontrado | Subtotal por item; total soma apenas comprados | 5 | 2 |
| P1 | Como usuário, quero acompanhar o total do carrinho | Total sempre visível, atualizado a cada marcação | 3 | 2 |
| P2 | Como usuário, quero que alteração estrutural peça confirmação no Modo Mercado | Renomear lista, adicionar e remover item exigem confirmação | 3 | 2 |
| P1 | Como usuário, quero entrar na minha conta para usar minhas listas em outro aparelho | Cadastro e login pela API; token em armazenamento seguro | | 3 |
| P1 | Como usuário, quero usar o aplicativo sem sinal dentro do mercado | Leitura local; escritas em fila; indicador de pendência | | 3 |
| P1 | Como usuário, quero que a compra suba sozinha quando o sinal voltar | Sincronização automática; política de conflito documentada | | 3 |
| P2 | Como usuário, quero compartilhar uma lista com quem mora comigo | Convite por e-mail da conta; papel de editor | | 3 |
| P2 | Como usuário, quero ver o histórico de compras para lembrar quanto gastei | Compras finalizadas com data, lista de origem e total | | 3 |
| P2 | Como usuário, quero adicionar item lendo o código de barras | Câmera resolve o código; caminho manual permanece | | final |
| P3 | Como usuário, quero compartilhar a lista com quem não usa o app | Exportação pelo seletor do sistema | | final |

---

## 4. Stack tecnológico

Kotlin Multiplatform com Compose Multiplatform, alvo Android e desktop; SQLDelight para persistência local; Ktor Client para rede; Koin para injeção de dependências; GitHub Actions para integração contínua; ktlint e detekt como análise estática; `kotlin.test` e Turbine para testes. O backend é a API do próprio grupo em DIM0547, em monorepo separado.

A justificativa detalhada das escolhas está em [`docs/dim0524/proposta.md`](../dim0524/proposta.md), já que decorrem das características do produto.

O que importa nesta disciplina é que a stack permite demonstrar as práticas de processo exigidas: a análise estática e os testes de domínio rodam sem emulador, o que viabiliza o portão de qualidade no pipeline desde a Sprint 1; o GitHub Actions fornece os dados de frequência de implantação e tempo de espera para as métricas DORA da Sprint 2; e a dependência entre dois repositórios, com o backend concentrado em um integrante, cria uma fila de espera real e mensurável, que é justamente o objeto do VSM da Sprint 3.

---

## 5. Acordo de processo

### Composição da equipe e o que isso impõe ao processo

Os três integrantes cursam DIM0510 e DIM0524. Vinicius cursa também DIM0547, onde o backend do mesmo produto é o objeto avaliado.

Isso significa que a equipe é uma só aqui — os três são avaliados pela atividade neste repositório, inclusive pelo Fator de Participação, que exige de cada um commits distribuídos, pull request autorado e revisão em PR alheio. Vinicius não é um fornecedor externo que entrega uma API: ele puxa item do aplicativo como qualquer outro integrante, e o backend é trabalho adicional dele, não substituto.

Daí decorrem duas regras que sustentam o resto do acordo. A primeira é que **o trabalho de backend não desconta da participação aqui**: nenhuma sprint pode fechar com Vinicius sem PR integrado neste repositório, e o planejamento reserva capacidade menor para ele justamente por isso. A segunda é que **o repositório do backend não é território privado**: Thallys e Ivis abrem issue, revisam PR e programam em par lá, para que a decisão de contrato não dependa de uma cabeça só.

### Apoio ao backend

Thallys e Ivis contribuem eventualmente no monorepo de Web II, **com homologação do docente**, registrada aqui e no README do monorepo. Como aquela disciplina avalia Vinicius individualmente, esse apoio segue três regras, para ser legível a quem avalia em vez de parecer contribuição não declarada em trabalho individual:

1. **Autoria explícita.** Trabalho em par é registrado com `Co-authored-by:` no commit, conforme exigido em AVALIACAO.md §3.3. Nenhum commit de Thallys ou Ivis entra no monorepo sem que a participação apareça no histórico.
2. **Vinicius é o autor do pull request e o responsável pela decisão.** O apoio é mão de obra e discussão, não terceirização de projeto: qualquer arquivo do monorepo continua sendo algo que ele consegue explicar sob pergunta, porque a avaliação daquela disciplina depende disso.
3. **Escopo preferencial.** O apoio se concentra onde o interesse do aplicativo é direto — contrato, DTOs, testes de integração contra o contrato, dados de teste — e não nas decisões de arquitetura que DIM0547 avalia como competência dele: separação de camadas, microsserviço em Go, gRPC, infraestrutura.

Esse apoio consome capacidade que não aparece no quadro deste repositório. Trabalho invisível distorce velocidade e VSM, então ele entra no quadro como item do tipo Tarefa, com a etiqueta `apoio-backend`. Ele não conta como valor entregue ao usuário e não entra na velocidade da sprint, mas ocupa vaga de WIP e é somado no fechamento — porque a pergunta que a Sprint 3 vai fazer é para onde foi o tempo da equipe, e a resposta precisa incluir essas horas.

### Cadência

A sprint da equipe coincide com a sprint da disciplina, com planejamento na segunda-feira que a abre e fechamento na sexta-feira da entrega. Dentro dela, uma sincronização semanal.

### Cerimônias

| Cerimônia | Quando | Duração | Saída |
| --- | --- | --- | --- |
| Planejamento | Início da sprint | 60 min | Itens no Sprint Backlog, com estimativa e capacidade por pessoa |
| Sincronização | Semanal | 20 min | Impedimentos como issue; estado de cada endpoint atualizado no contrato |
| Revisão | Sexta da entrega | 30 min | Incremento demonstrado e gravado |
| Retrospectiva | Sexta da entrega | 30 min | Uma ação de melhoria, registrada como issue com responsável |

Todas as cerimônias são dos três integrantes.

A retrospectiva sem ação registrada não conta como realizada. Toda ação de melhoria vira item do quadro, para que seu efeito possa ser confrontado com dados na sprint seguinte.

A sincronização semanal tem uma pauta fixa de um item: quais endpoints saíram do contrato para a implementação, e quais histórias do aplicativo isso desbloqueia. É a única reunião que não pode ser substituída por mensagem.

### Papéis

Equipe pequena, sem especialização por camada dentro do aplicativo — os três tocam interface, dados e pipeline, para que nenhum item fique bloqueado na ausência de um.

| Papel | Responsável | Rotatividade |
| --- | --- | --- |
| Product Owner | Thallys na Sprint 0 | Rodízio a cada sprint |
| Scrum Master | Ivis na Sprint 0 | Rodízio a cada sprint |
| Mantenedor do pipeline e das métricas | Vinicius na Sprint 0 | Rodízio a cada sprint |
| Guardião do contrato da API | Vinicius | Fixo no semestre |
| Revisor de código | Rodízio entre os outros dois | Um PR nunca é revisado pelo autor |
| Condução da apresentação | Alterna | A cada sprint |
| Redação da documentação da sprint | Alterna | A cada sprint |

Os três papéis principais giram em rodízio, de modo que cada integrante exerça todos ao longo das quatro sprints. A única atribuição fixa é o contrato da API, porque quem implementa o backend é quem sabe o que ele pode prometer.

A revisão em rodízio importa por um motivo específico: com três pessoas, é fácil um par se formar e o terceiro revisar sozinho o resto. A ordem de revisão é registrada no planejamento, e a sincronização semanal confere se cada um já revisou PR de cada um dos outros dois.

Isso impõe uma condição sobre o contrato: mudança acordada em conversa não existe. Só passa a valer quando entra em `docs/contrato-api.md` por pull request, revisado por quem não é o guardião, com a issue das histórias afetadas vinculada. Sem essa regra, o aplicativo é construído contra uma API que existe apenas na memória de uma pessoa.

### Definição de Pronto

Um item sai de "Em revisão" quando **todas** as condições valem:

1. Código integrado em `main` por pull request vinculado à issue
2. Revisado e aprovado por outro integrante, seguindo o rodízio de revisão, com ao menos um comentário substantivo
3. `ktlintCheck` e `detekt` limpos no GitHub Actions
4. Testes automatizados do item escritos e verdes no CI
5. Critérios de aceitação verificados manualmente no dispositivo
6. Documentação afetada atualizada no mesmo PR
7. Se o item consome a API: o endpoint está no contrato e há teste contra a implementação falsa, de modo que o item seja demonstrável mesmo com o backend fora do ar

Nenhuma condição admite exceção por prazo. Item que não cumpre volta para "Em progresso".

### Fluxo do quadro e limites de trabalho em curso

| Coluna | O que autoriza entrar | WIP |
| --- | --- | --- |
| Backlog | Priorizado pelo PO | — |
| Sprint Backlog | Comprometido no planejamento | — |
| Em progresso | Alguém assumiu o item | 3 |
| Em revisão | PR aberto e CI verde | 2 |
| Pronto | Definição de Pronto cumprida | — |

O limite de 3 em progresso equivale a um item por pessoa. O limite de revisão é deliberadamente menor: com três pessoas produzindo e revisando, deixar a fila de revisão crescer até três significa que ninguém está revisando. Com o limite atingido, revisar o PR do colega tem prioridade sobre iniciar item novo.

Existe uma etiqueta `aguardando-contrato`, aplicada a itens que dependem de endpoint ainda não disponível. Item com essa etiqueta não ocupa vaga de WIP, porque não há trabalho em curso nele; em compensação, o tempo que ele passa etiquetado é registrado e vira a medida direta de espera entre os dois repositórios no VSM da Sprint 3.

A etiqueta também dispara uma ação, e não só uma medição: **dois itens simultâneos em `aguardando-contrato` autorizam quem estiver livre a puxar trabalho de `apoio-backend` em vez de iniciar história nova no aplicativo**. É a regra Lean de atacar o gargalo em vez de acumular estoque na frente dele — e é possível justamente porque o apoio ao monorepo está homologado. Sem essa válvula, a equipe responderia à espera abrindo mais frentes no app, que é o comportamento que infla o trabalho em curso e alonga o lead time de tudo.

Se a espera se mostrar o maior desperdício do fluxo, ela é a candidata natural à proposta de melhoria da entrega final. A hipótese que a equipe já carrega, e que a Sprint 3 vai confirmar ou derrubar com dado, é que o gargalo não está no aplicativo nem na API, mas na disponibilidade de uma única pessoa para os dois.

### Ferramentas

| Uso | Ferramenta |
| --- | --- |
| Código e revisão | GitHub — dois repositórios, vinculados nos READMEs |
| Quadro e backlog | GitHub Projects, um quadro por repositório |
| Contrato entre app e API | `docs/contrato-api.md`, espelhando o OpenAPI do monorepo |
| Automação e métricas | GitHub Actions |
| Conversa do dia a dia | WhatsApp |
| Decisões técnicas | `docs/decisoes/` — um registro curto por decisão |
| Uso de IA | `docs/uso-de-ia.md`, atualizado durante a sprint |

Decisão tomada em conversa e não registrada é tratada como não tomada.

### Riscos identificados

| Risco | Efeito | Mitigação |
| --- | --- | --- |
| Cronogramas de duas disciplinas acoplados | App bloqueado esperando endpoint | Contrato antes da implementação; implementação falsa do repositório; item bloqueado sai do WIP e é medido |
| Vinicius acumula o backend de DIM0547 com as mesmas sprints aqui | Sobrecarga individual e queda de participação neste repositório | Capacidade menor reservada a ele no planejamento; nenhuma sprint fecha sem PR dele integrado aqui; apoio homologado de Thallys e Ivis no monorepo, contabilizado como `apoio-backend` |
| Apoio ao monorepo apagar a autoria de Vinicius em DIM0547 | Avaliação individual dele prejudicada | `Co-authored-by:` obrigatório; PR sempre autorado por ele; apoio restrito a contrato, DTOs e testes |
| Escopo do Modo Mercado crescer | Sprint 2 estoura | Modo Mercado fatiado em quatro histórias, entregáveis separadamente |
| Conflito de sincronização mal resolvido | Perda de marcação de compra | Política de conflito escrita antes do código, em `docs/offline.md`; registro de compra idempotente |
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

**Quadro no GitHub Projects:** _[link]_

**Integração entre disciplinas:** o mesmo produto é desenvolvido em **DIM0524 — Sistemas para Dispositivos Móveis**, pela equipe inteira, e em **DIM0547 — Desenvolvimento de Sistemas Web II**, por Vinicius. Os entregáveis são distintos: lá o objeto avaliado é o aplicativo e o backend; aqui, o processo — acordo de processo, métricas de fluxo, DORA, VSM, retrospectivas e relatório final —, artefatos que existem apenas nesta disciplina. Repositórios vinculados nos READMEs.