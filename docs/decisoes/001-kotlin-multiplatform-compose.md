# 001 — Kotlin Multiplatform com Compose Multiplatform

**Data:** 14/09/2026
**Estado:** aceita

## Contexto

O TáNaLista precisa rodar no aparelho Android — o cenário que define o produto é o
corredor do mercado — e queremos um alvo desktop para o momento de planejamento em
casa, onde digitar trinta itens no teclado é mais rápido que no celular. A disciplina
também oferece bônus de entrega multiplataforma.

A equipe tem três pessoas, uma delas com a carga adicional do backend em DIM0547. Manter
duas bases de código para a mesma lógica de lista, item e total não cabe nessa
capacidade.

Além disso, as regras que definem o produto — nome normalizado, regra do total,
fila de escrita offline — são as mesmas em qualquer alvo. Duplicá-las é duplicar o
lugar onde elas podem divergir.

## Decisão

**Kotlin Multiplatform** com **Compose Multiplatform**, um único módulo `composeApp`,
com a lógica e a interface em `commonMain` e apenas o ponto de entrada específico de
cada alvo.

Kotlin porque é a linguagem de primeira classe do Android e a única em que "mesmo
código nos dois alvos" não custa uma camada de tradução. Compose Multiplatform porque
permite que a própria interface seja compartilhada, e não só o domínio — com dois
alvos e três pessoas, escrever a tela duas vezes anularia o ganho.

## Alternativas descartadas

**Android nativo com Kotlin e Jetpack Compose, sem multiplataforma.** É o caminho mais
curto para o alvo prioritário e o mais bem documentado. Descartado porque elimina o
alvo desktop, que é onde o planejamento acontece, e com ele o bônus de entrega
multiplataforma.

**Flutter.** Multiplataforma maduro e produtivo. Descartado porque nenhum integrante
tem experiência com Dart, e porque a disciplina avalia domínio da plataforma Android —
aprender linguagem e framework ao mesmo tempo, com quatro sprints, é risco que o
calendário não absorve.

**React Native.** Mesmo argumento de custo de aprendizado, somado a uma integração
mais atritada com SQLite tipado e com armazenamento seguro do sistema, que são
exigências das Sprints 2 e 3.

**KMP compartilhando só o domínio, com interface nativa em cada alvo.** Seria mais
conservador e daria interface idiomática em cada plataforma. Descartado pelo custo:
duas interfaces para três pessoas, sem ganho que a rubrica reconheça.

## Consequências

A camada de domínio **não importa Compose nem infraestrutura** — é o que permite
testá-la sem emulador e o que a avaliação verifica a partir da Sprint 2.

O ciclo de desenvolvimento passa a ter o desktop como caminho rápido: `:composeApp:run`
abre a janela em segundos, contra o ciclo de build e instalação no emulador. Em
compensação, **toda tela precisa ser verificada no Android antes de sair de "Em
revisão"** — é a condição 5 da Definição de Pronto —, porque funcionar na janela não
prova que funciona com uma mão no carrinho.

Bibliotecas escolhidas precisam suportar KMP. É restrição real: foi ela que eliminou
Room em favor de SQLDelight ([004](004-sqldelight.md)).

Se o alvo desktop se mostrar um peso — Compose Multiplatform quebrando no desktop e
consumindo sprint — a saída é entregar só Android e abrir mão do bônus, sem mexer na
arquitetura. O domínio compartilhado continua válido.
