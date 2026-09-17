# TáNaLista

[![CI](https://github.com/tanalistaimd/tanalista/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/tanalistaimd/tanalista/actions/workflows/ci.yml)
[![Licença: MIT](https://img.shields.io/badge/licen%C3%A7a-MIT-blue.svg)](LICENSE)

Aplicativo multiplataforma de listas de compras com **Modo Mercado**: planejar a
compra em casa e, dentro do mercado, marcar o item comprado com o preço encontrado e
acompanhar o total do carrinho em tempo real. Funciona integralmente offline.

> Listas de compras comuns registram nomes de produtos e pouco mais. Durante a compra
> real falta outra coisa: quantidade, preço encontrado na gôndola, o que já foi pego e
> quanto o carrinho já custa. Hoje isso acaba em papel, bloco de notas e cálculo
> mental no caixa. O TáNaLista separa planejar de comprar, e faz a conta sozinho
> durante o segundo.

**UFRN/DIMAp · 2026.2 · Coorte B — online**

## Equipe

| Nome | Matrícula | GitHub | Papel na Sprint 0 |
| --- | --- | --- | --- |
| Thallys | 20240011552 | [@thallystorres](https://github.com/thallystorres) | Product Owner |
| Ivis | 20220028454 | [@ivixs](https://github.com/ivixs) | Scrum Master |
| Vinicius | 20230051760 | [@vbarbosadev](https://github.com/vbarbosadev) | Mantenedor do pipeline · guardião do contrato da API |

Product Owner, Scrum Master e mantenedor do pipeline giram a cada sprint, de modo que
cada integrante exerça os três ao longo do semestre. O guardião do contrato da API é
fixo, porque quem implementa o backend é quem sabe o que ele pode prometer.

## Integração entre disciplinas

Um produto, três disciplinas, entregáveis distintos.

| Disciplina | Objeto avaliado | Quem | Onde |
| --- | --- | --- | --- |
| **DIM0524** — Sistemas para Dispositivos Móveis | O aplicativo | equipe | este repositório · [proposta](docs/dim0524/proposta.md) |
| **DIM0510** — Processos de Software | O processo | equipe | este repositório · [proposta](docs/dim0510/proposta.md) |
| **DIM0547** — Desenvolvimento de Sistemas Web II | O backend | Vinicius | [tanalista-api](https://github.com/vbarbosadev/tanalista-api) |

**Repositório do backend:** <https://github.com/vbarbosadev/tanalista-api>

O aplicativo consome a API do próprio grupo, desenvolvida por Vinicius em DIM0547.
Os dois repositórios formam um produto só e estão vinculados nos READMEs de ambos.
O contrato entre eles está em [`docs/contrato-api.md`](docs/contrato-api.md) e muda
apenas por pull request revisado por quem não é o guardião.

Thallys e Ivis contribuem eventualmente no monorepo de Web II, com homologação do
docente: autoria explícita com `Co-authored-by:` em trabalho de par, pull requests
sempre autorados por Vinicius — a quem DIM0547 avalia — e escopo restrito a contrato,
DTOs e testes.

## O que o produto faz

**Planejamento.** Várias listas nomeadas, itens com quantidade, unidade, categoria e
preço estimado. Nome de lista e de item não se repetem de forma equivalente,
ignorando caixa e acentuação — mas `Leite 200g` e `Leite 400g` continuam sendo
produtos distintos.

**Modo Mercado.** Leitura rápida e alvo de toque grande, marcação de item comprado,
preço encontrado, quantidade levada, subtotal por item e total do carrinho calculado
sozinho. **Só entra no total o que foi marcado como comprado, pelo preço encontrado** —
nunca o preço estimado no planejamento. Alteração estrutural da lista pede confirmação
enquanto o modo está ativo.

**Offline-first.** O aplicativo abre e é inteiramente utilizável sem rede: a base
local é a fonte de leitura, as escritas entram em fila e sobem quando o sinal volta.
O cenário que define o produto é o corredor do mercado sem sinal.

**Lista compartilhada.** A compra da casa é feita por mais de uma pessoa. A lista
pertence a uma conta e pode ser compartilhada com outra.

## Como rodar

**Requisitos:** um JDK recente e o Android SDK. Não é preciso instalar o JDK 17 nem o
Gradle — o `jvmToolchain(17)` e o `foojay-resolver-convention` baixam o toolchain, e o
wrapper baixa o Gradle na versão que o CI usa.

```bash
git clone https://github.com/tanalistaimd/tanalista.git
cd tanalista

./gradlew :composeApp:assembleDebug   # APK em composeApp/build/outputs/apk/debug/
./gradlew :composeApp:run             # janela do desktop
./gradlew ktlintCheck detekt          # análise estática, a mesma que roda no CI
```

O caminho do Android SDK vai em `local.properties`, que é de cada máquina e não é
versionado. O Android Studio cria o arquivo no primeiro sync; para criar à mão,
`sdk.dir=/caminho/para/o/Android/Sdk`.

A URL da API é lida de configuração. Sem ela, o aplicativo sobe em modo local, com a
implementação falsa do repositório — é o que permite rodar e demonstrar o app antes de
o backend existir.

## Estrutura

```text
composeApp/src/
  commonMain/kotlin/br/ufrn/tanalista/
    domain/         entidades, interfaces de repositório, casos de uso
    data/           fontes de dados, DTOs, implementações de repositório
    presentation/   telas em Compose e ViewModels
  commonTest/kotlin/br/ufrn/tanalista/
  androidMain/      AndroidManifest, MainActivity, recursos
  desktopMain/      ponto de entrada da janela

docs/
  proposta.md       Índice das propostas
  dim0510/          Proposta e acordo de processo
  dim0524/          Proposta, plataforma-alvo e backend
  contrato-api.md   Contrato com o backend, e o estado de cada endpoint
  decisoes/         Um registro por decisão técnica
  uso-de-ia.md      Ferramentas de IA, tarefas e verificação
```

A camada de domínio não importa Compose nem infraestrutura.

## Processo

Quadro Kanban: <https://github.com/orgs/tanalistaimd/projects/2>

| Coluna | O que autoriza entrar | WIP |
| --- | --- | --- |
| Backlog | Priorizado pelo PO | — |
| Sprint Backlog | Comprometido no planejamento | — |
| Em progresso | Alguém assumiu o item | **3** |
| Em revisão | PR aberto e CI verde | **2** |
| Pronto | Definição de Pronto cumprida | — |

A etiqueta `aguardando-contrato` marca item que depende de endpoint inexistente: não
ocupa vaga de WIP, e o tempo etiquetado é medido — é a matéria-prima do VSM da
Sprint 3. A etiqueta `apoio-backend` marca trabalho no monorepo de Web II: ocupa WIP,
mas não conta como valor entregue.

**Definição de Pronto** — todas as condições, sem exceção por prazo:

1. Integrado em `main` por pull request vinculado à issue
2. Aprovado por outro integrante, com ao menos um comentário substantivo
3. `ktlintCheck` e `detekt` limpos no CI
4. Testes do item escritos e verdes no CI
5. Critérios de aceitação verificados no dispositivo Android
6. Documentação afetada atualizada no mesmo PR
7. Se consome a API: endpoint no contrato e teste contra a implementação falsa

O acordo de processo completo — cadência, cerimônias, rodízio de papéis e riscos —
está em [`docs/dim0510/proposta.md`](docs/dim0510/proposta.md).

## Vídeos das sprints

| Sprint | DIM0510 — Processos | DIM0524 — Sistemas Móveis |
| --- | --- | --- |
| Sprint 0 | _[link]_ | _[link]_ |

## Licença

[MIT](LICENSE).
