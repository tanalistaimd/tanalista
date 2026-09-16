# Contexto do projeto — TáNaLista

Arquivo de contexto para assistentes de código. Descreve o que já foi decidido, o
que não pode ser alterado sem conversa e o que falta entregar.

## O que é

Aplicativo multiplataforma de listas de compras com **Modo Mercado**: planejar a
compra em casa e, dentro do mercado, marcar item comprado com o preço encontrado
e acompanhar o total do carrinho em tempo real. Funciona integralmente offline.

O mesmo produto atende a três disciplinas da UFRN/DIMAp em 2026.2:

| Disciplina | Objeto avaliado | Onde |
| --- | --- | --- |
| DIM0524 — Sistemas para Dispositivos Móveis | O aplicativo | este repositório |
| DIM0510 — Processos de Software | O processo | este repositório, `docs/dim0510/` |
| DIM0547 — Desenvolvimento Web II | O backend (só Vinicius) | monorepo separado |

Repositório: https://github.com/tanalistaimd/tanalista (público, obrigatório)

## Equipe

| Nome | GitHub | Matrícula | Papel na Sprint 0 |
| --- | --- | --- | --- |
| Thallys | thallystorres | 20240011552 | Product Owner |
| Ivis | ivixs | 20220028454 | Scrum Master |
| Vinicius | vbarbosadev | 20230051760 | Mantenedor do pipeline · guardião do contrato da API |

Os papéis giram a cada sprint. O guardião do contrato é fixo no semestre.

## Documentos da disciplina

As rúbricas, o cronograma e as tarefas de cada sprint são públicos e valem mais
que qualquer suposição. Clone fora deste repositório e consulte antes de decidir
o que entregar:

```bash
git clone https://github.com/fmarquesfilho/processos-2026-2       # DIM0510
git clone https://github.com/fmarquesfilho/sistemas-moveis-2026-2 # DIM0524
git clone https://github.com/fmarquesfilho/web2-2026-2            # DIM0547
```

Arquivos que importam em cada um: `docs/RUBRICAS.md`, `docs/SPRINT-0-TAREFAS.md`,
`docs/CRONOGRAMA.md`, `docs/STACK.md`, `docs/AVALIACAO.md`.

## Stack e decisões já tomadas

Não reabrir sem conversa — cada uma está justificada nas propostas em `docs/`.

- **Kotlin Multiplatform + Compose Multiplatform**, alvos **Android** (prioritário)
  e **desktop**. iOS e web foram descartados explicitamente.
- **Backend: API própria do grupo**, feita por Vinicius em DIM0547. Supabase e
  Firebase foram descartados — as regras de unicidade por nome normalizado e a
  autorização por recurso precisam ser de servidor.
- **SQLDelight** para persistência local; **Ktor Client** para rede; **Koin**
  para injeção de dependências.
- **ktlint** e **detekt** como análise estática, verdes no CI.
- Testes: `kotlin.test` e Turbine.

Bibliotecas novas (SQLDelight, Ktor, Koin) só entram **depois da Sprint 0** —
estabilidade antes de funcionalidade enquanto o CI não estiver verde.

## Domínio

```text
Usuário  ──< Acesso >──  Lista        papel: dono | editor
Lista    ──<             Item         nome normalizado único por lista
Lista    ──<             Compra       sessão do Modo Mercado, com total e data
Compra   ──< Registro >── Item        preço encontrado e quantidade levada
```

Duas regras que atravessam o código inteiro:

1. **Nome normalizado** — caixa baixa, acentos removidos, espaços colapsados. É a
   chave de unicidade de lista e de item. `Leite 200g` e `Leite 400g` são
   distintos; `Compras do mês` e `compras do mês` são o mesmo. O mesmo algoritmo
   roda no cliente e no servidor.
2. **Regra do total** — o total do carrinho soma apenas itens marcados como
   comprados, com o preço encontrado e a quantidade levada. Nunca o preço
   estimado do planejamento.

A entidade `Compra` separa planejamento de execução: a lista é um modelo
reutilizável, cada ida ao mercado é um registro próprio.

## Arquitetura

```text
composeApp/src/
  commonMain/kotlin/
    data/         fontes de dados, DTOs, implementações de repositório
    domain/       entidades, interfaces de repositório, casos de uso
    presentation/ telas em Compose e ViewModels
  androidMain/kotlin/
  jvmMain/kotlin/   (alvo desktop)
  commonTest/kotlin/
```

A camada de domínio **não importa Compose nem infraestrutura** — verificado na
avaliação a partir da Sprint 2.

A camada de dados é uma interface no domínio com duas implementações: a de rede
(Ktor) e uma local que serve de implementação falsa. O aplicativo precisa
compilar, rodar e ser demonstrável com a segunda enquanto o backend não existir.

## Processo

- Kanban: Backlog · Sprint Backlog · Em progresso · Em revisão · Pronto
- WIP: **3** em progresso, **2** em revisão
- Etiqueta `aguardando-contrato`: item que depende de endpoint inexistente. Não
  ocupa vaga de WIP; o tempo etiquetado é medido (VSM da Sprint 3).
- Etiqueta `apoio-backend`: trabalho no monorepo de Web II. Entra no quadro, não
  conta como valor entregue, mas ocupa WIP.
- Contrato da API só muda por PR em `docs/contrato-api.md`, revisado por quem não
  é o guardião. Mudança combinada em conversa não vale.

**Definição de Pronto** — todas as condições:

1. Integrado em `main` por PR vinculado à issue
2. Revisado e aprovado por outro integrante, com ao menos um comentário substantivo
3. `ktlintCheck` e `detekt` limpos no CI
4. Testes do item escritos e verdes no CI
5. Critérios de aceitação verificados no dispositivo
6. Documentação afetada atualizada no mesmo PR
7. Se consome a API: endpoint no contrato e teste contra a implementação falsa

## Convenções de trabalho

- Branch padrão: **`main`** — as rúbricas cobram CI verde e incremento em `main`.
- Todo trabalho entra por pull request, revisado por outro integrante. Um PR
  nunca é revisado pelo autor.
- Programação em par exige `Co-authored-by:` no commit.
- Commits autorados com o e-mail da conta GitHub declarada na Sprint 0 — a nota
  de participação individual depende disso.
- Commits distribuídos ao longo da sprint. Concentração na véspera é penalizada.
- Decisão técnica não registrada em `docs/decisoes/` é tratada como não tomada.
- Uso de IA é registrado em `docs/uso-de-ia.md` durante a sprint.

## Ambiente

Requisitos do projeto, não de uma máquina em particular. Linux, macOS e Windows
servem; cada integrante monta o resto como preferir.

- **Gradle pelo wrapper, sempre**: `./gradlew` (`gradlew.bat` no Windows). Nunca um
  Gradle instalado no sistema — a versão do wrapper é a que o CI usa, e divergir dela
  é a forma mais comum de "funciona aqui e quebra no pipeline".
- **JDK**: o alvo de compilação é o 17, declarado por `jvmToolchain(17)` no bloco
  `kotlin { }`. Não é preciso ter o 17 instalado: o `foojay-resolver-convention` no
  `settings.gradle.kts` baixa o toolchain quando falta. Qualquer JDK recente roda o
  Gradle.
- **Android SDK** com a `compileSdk` declarada em `gradle/libs.versions.toml`. O
  caminho fica em `local.properties`, que é de cada máquina e não é versionado.
- **IDE**: Android Studio é o caminho conhecido — o alvo desktop e os `@Preview`
  funcionam nele sem configuração extra. IntelliJ IDEA com o plugin Android também
  serve. Nada no projeto depende de IDE: build, análise estática e testes rodam pelo
  terminal, que é como o CI os executa.
- **Teste manual**: emulador ou aparelho Android para o alvo Android, `:composeApp:run`
  para o desktop. Os critérios de aceitação da Definição de Pronto são verificados no
  Android.

## Portão de qualidade do CI

Sprint 0:

```bash
./gradlew ktlintCheck detekt
./gradlew assembleDebug
```

Da Sprint 1 em diante, acrescenta `./gradlew allTests`. Pipeline vermelho no
momento do prazo limita a nota da entrega técnica a 6.

## Estado atual — Sprint 0, entrega 16/09/2026 23:59

Projeto KMP recém-criado pelo assistente do Android Studio, com alvos Android e
desktop. Nada além disso.

Falta, em ordem de prioridade:

- [ ] Trocar a branch padrão de `develop` para `main`
- [ ] Subir o projeto KMP para o repositório
- [ ] `jvmToolchain(17)` dentro do bloco `kotlin { }` em `composeApp/build.gradle.kts`
- [ ] Confirmar a declaração do alvo desktop e os nomes das tasks
- [ ] ktlint e detekt no build, com `detekt.yml`
- [ ] `.github/workflows/ci.yml` em push e PR, verde em `main`
- [ ] `README.md` e as propostas em `docs/dim0510/` e `docs/dim0524/`
- [ ] Esqueletos de `docs/contrato-api.md` e `docs/uso-de-ia.md`
- [ ] Quadro no GitHub Projects: colunas, WIP declarado, issues do backlog,
      estimativa em ≥ 3, prioridade em todas, etiquetas criadas
- [ ] Evidência do problema (entrevistas) nas duas propostas
- [ ] Preencher: link do quadro, URL do monorepo de Web II, links dos vídeos, licença
- [ ] Dois vídeos de 5 min, todos os integrantes falando
- [ ] Opcional: primeira tela em Compose com componente próprio e estado elevado

O monorepo de Web II precisa estar linkado neste README e vice-versa — é
condição do bônus de integração entre disciplinas.
