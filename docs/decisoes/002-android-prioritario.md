# 002 — Android prioritário, desktop secundário, iOS e web descartados

**Data:** 14/09/2026
**Estado:** aceita

## Contexto

Decidido o Kotlin Multiplatform ([001](001-kotlin-multiplatform-compose.md)), sobra
escolher quais alvos ficam configurados no projeto. KMP torna barato *adicionar* um
alvo e caro *sustentar* um: cada alvo é mais uma superfície para testar, mais um jeito
de o CI quebrar e mais um caminho que precisa funcionar na demonstração.

## Decisão

**Android** como alvo prioritário e **desktop (JVM)** como alvo secundário, ambos
configurados em `composeApp/build.gradle.kts`. Nenhum outro alvo declarado.

Android é prioritário porque o momento que define o produto é o corredor do mercado,
com uma mão no carrinho e sinal ruim — isso é um aparelho no bolso, não um navegador
nem um computador. O público descrito nas conversas que originaram o produto faz a
compra com Android de faixa intermediária, e a equipe tem aparelhos Android físicos,
o que permite testar o cenário real, inclusive com o modo avião ligado, sem depender
de emulador.

Desktop cobre o outro momento do produto, o planejamento em casa, e paga um segundo
custo durante o desenvolvimento: ciclo de edição e visualização em segundos, contra
build e instalação no emulador.

## Alternativas descartadas

**iOS como alvo prioritário ou secundário.** Descartado porque nenhum integrante possui
iPhone ou Mac. Sem Mac não há como compilar o alvo, e a entrega ficaria restrita ao
simulador — longe do público real e impossível de demonstrar em vídeo com aparelho.
Não é falta de interesse: é falta de máquina.

**Web como alvo.** Descartado porque o bloco final da disciplina exige câmera (leitura
de código de barras), armazenamento seguro de credencial e publicação em canal de
distribuição. Os três são recursos de sistema operacional; no navegador seriam
aproximações, e a rubrica avalia o uso real desses recursos.

**Só Android, sem desktop.** Seria o caminho de menor risco para a Sprint 0. Descartado
porque abre mão do bônus de entrega multiplataforma e do ciclo rápido de
desenvolvimento, e porque o alvo desktop é praticamente gratuito quando a interface já
está em `commonMain`.

## Consequências

`@Preview` e `:composeApp:run` são o ciclo do dia a dia, mas **a verificação que vale
é no Android** — condição 5 da Definição de Pronto. Interface pensada só na janela
larga chega quebrada no aparelho.

A partir da Sprint 1 as telas precisam de adaptação para janela larga, que é o que
sustenta a intenção de entrega multiplataforma declarada na proposta de DIM0524.

Toda biblioteca nova precisa suportar `androidTarget()` e `jvm()`. Uma que só suporte
Android obriga a escolher entre `expect`/`actual` e abrir mão do desktop.

Se o calendário apertar, **o desktop é o primeiro a ser sacrificado** — é alvo
secundário por decisão, não por acidente. Android não é negociável.
