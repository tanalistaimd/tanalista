# TáNaLista

Aplicativo multiplataforma de listas de compras com modo mercado, para planejar a compra em casa e acompanhar o total do carrinho durante a compra.

> Soma o carrinho em tempo real enquanto os itens são marcados, funciona sem sinal dentro do mercado e sincroniza com a API do próprio grupo ao reconectar.

**Equipe:** Thallys · Ivis · Vinicius
**Semestre:** 2026.2 — UFRN/DIMAp

---

## Três disciplinas, um produto

Este produto atende a três disciplinas com entregáveis distintos. O aplicativo e os artefatos de processo vivem neste repositório; o backend vive no monorepo de Web II.

| Disciplina | Equipe avaliada | Objeto avaliado | Proposta | Repositório da disciplina |
| --- | --- | --- | --- | --- |
| **DIM0524** — Sistemas para Dispositivos Móveis | Thallys · Ivis · Vinicius | O aplicativo: arquitetura, offline-first, consumo da API, recursos do dispositivo, segurança e distribuição | [`docs/dim0524/proposta.md`](docs/dim0524/proposta.md) | `github.com/fmarquesfilho/sistemas-moveis-2026-2` |
| **DIM0510** — Processos de Software | Thallys · Ivis · Vinicius | O processo: acordo de processo, métricas de fluxo, DORA, VSM, retrospectivas e melhoria contínua | [`docs/dim0510/proposta.md`](docs/dim0510/proposta.md) | `github.com/fmarquesfilho/processos-2026-2` |
| **DIM0547** — Desenvolvimento de Sistemas Web II | Vinicius | O backend: API, contratos, microsserviço em Go e infraestrutura | _[link para a proposta no monorepo]_ | `github.com/fmarquesfilho/web2-2026-2` |

**Repositório do backend:** _[url do monorepo de Web II]_

Os artefatos de processo em `docs/dim0510/` são exclusivos de DIM0510 e não compõem a entrega de DIM0524. O backend não é avaliado aqui — ele é a dependência deste repositório, e o contrato entre os dois está em [`docs/contrato-api.md`](docs/contrato-api.md).

Thallys e Ivis contribuem eventualmente no monorepo de Web II, com homologação do docente. A autoria de cada contribuição é explícita no histórico, com `Co-authored-by:` em trabalho de par, e os pull requests daquele repositório são autorados por Vinicius, a quem DIM0547 avalia.

---

## Problema

Listas de compras comuns registram nomes de produtos e pouco mais. Durante a compra real o usuário precisa de outra coisa: quantidade, unidade, preço encontrado na gôndola, o que já foi pego e quanto o carrinho já custa. Hoje isso acaba em papel, bloco de notas e cálculo mental no caixa.

O TáNaLista separa os dois momentos — planejar e comprar — e faz a conta sozinho durante o segundo.

---

## O que o produto faz

**Planejamento.** Várias listas nomeadas, itens com quantidade, unidade de medida, categoria e preço estimado. Nome de lista e nome de item não podem se repetir de forma equivalente, ignorando caixa e acentuação, mas variações reais de produto continuam válidas.

**Modo Mercado.** Leitura rápida, alvo de toque grande, marcação de item comprado, preço encontrado, quantidade efetivamente levada, subtotal por item e total do carrinho calculado automaticamente. Só entra no total o que foi marcado como comprado. Alterações estruturais da lista exigem confirmação enquanto o modo está ativo.

**Offline-first.** O aplicativo abre e é inteiramente utilizável sem rede: a base local é a fonte de leitura, as escritas entram em fila e sobem quando o sinal volta. O cenário que define o produto é o corredor do mercado sem sinal.

**Lista compartilhada.** A compra da casa é feita por mais de uma pessoa. A lista pertence a uma conta e pode ser compartilhada com outra, sincronizada pela API do grupo em Web II.

---

## Estado do projeto

**Sprint atual:** Sprint 0 — entrega em 16/09/2026, 23:59

_[Badge de CI aqui assim que o pipeline existir.]_

---

## Como rodar

_[Preencher na Sprint 0, junto com a primeira tela. Meta: terceiro rodando em menos de 15 minutos.]_

```bash
git clone <url>
cd tanalista
./gradlew :composeApp:assembleDebug     # Android
./gradlew :composeApp:run               # Desktop
```

**Requisitos:** JDK 17+, Android Studio, dispositivo ou emulador Android.

A URL da API é lida de configuração. Sem ela, o aplicativo sobe em modo local, com a implementação falsa do repositório — o que permite rodar o app antes de o backend existir.

---

## Estrutura

```text
docs/
  dim0510/proposta.md      Proposta e acordo de processo
  dim0524/proposta.md      Proposta, plataforma-alvo e backend
  contrato-api.md          Contrato com o backend de Web II e estado de cada endpoint
  decisoes/                Registro de decisões técnicas
  uso-de-ia.md             Ferramentas de IA, tarefas e impacto
composeApp/                Código do aplicativo
```

---

## Vídeos das sprints

| Sprint | DIM0524 | DIM0510 |
| --- | --- | --- |
| Sprint 0 | _[link]_ | _[link]_ |

---

## Checklist da Sprint 0

**DIM0524**

- [ ] Projeto compila e roda em Android e desktop
- [ ] `ktlintCheck` e `detekt` limpos no GitHub Actions
- [ ] Primeira tela em Compose com componente próprio e estado elevado
- [ ] `docs/dim0524/proposta.md` (≤ 3 páginas)
- [ ] Vídeo de 5 min, com plataforma-alvo e backend justificados
- [ ] Coorte, integração entre disciplinas e intenção de multiplataforma declaradas

**DIM0510**

- [ ] Repositório público com README completo
- [ ] Quadro Kanban criado, com colunas e WIP declarado
- [ ] Backlog com ≥ 5 itens, ≥ 3 estimados e priorizados
- [ ] `docs/dim0510/proposta.md` com acordo de processo (≤ 3 páginas)
- [ ] Vídeo de 5 min, com o processo da equipe
- [ ] Papéis atribuídos e coorte declarada

---

## Licença

_[Definir — a rúbrica da entrega final exige licença declarada.]_