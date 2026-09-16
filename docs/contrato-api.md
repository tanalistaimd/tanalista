# Contrato da API

Contrato entre o aplicativo (DIM0524, este repositório) e a API do grupo
(DIM0547, [monorepo de Web II](https://github.com/vbarbosadev/tanalista-api)).

Este documento espelha o OpenAPI do monorepo. Quando os dois divergirem, o OpenAPI
é a fonte da verdade e este arquivo está desatualizado — corrigir aqui, no mesmo PR.

## Como este contrato muda

Só por **pull request neste arquivo**, revisado por quem **não** é o guardião do
contrato, com a issue das histórias afetadas vinculada. Mudança combinada em conversa
não vale: o aplicativo não pode ser construído contra uma API que existe apenas na
memória de uma pessoa.

**Guardião do contrato:** Vinicius (`vbarbosadev`) — fixo no semestre, porque quem
implementa o backend é quem sabe o que ele pode prometer.

O contrato **precede a implementação dos dois lados**. Um endpoint entra aqui como
`planejado` antes de existir código, de qualquer um dos lados.

## Estado dos endpoints

| Estado | Significa |
| --- | --- |
| `planejado` | Acordado no contrato, sem implementação em nenhum lado |
| `em implementação` | Em desenvolvimento no monorepo |
| `disponível` | Publicado e consumível pelo aplicativo |

Enquanto um endpoint não está `disponível`, a história que depende dele recebe a
etiqueta `aguardando-contrato` no quadro. O item não ocupa vaga de WIP, e o tempo
etiquetado é medido — é a matéria-prima do VSM da Sprint 3.

## Como o aplicativo sobrevive sem a API

A camada de dados é uma interface declarada no domínio, com duas implementações:
a de rede (Ktor Client) e uma local que serve de implementação falsa. O aplicativo
compila, roda e é demonstrável com a segunda enquanto o endpoint correspondente não
existe. A condição 7 da Definição de Pronto exige teste contra essa implementação
falsa para todo item que consome a API.

## Regras que valem nos dois lados

**Nome normalizado.** Caixa baixa, acentos removidos, espaços colapsados. É a chave
de unicidade de lista e de item. `Compras do mês` e `compras do mês` são o mesmo
nome; `Leite 200g` e `Leite 400g` não são. O mesmo algoritmo roda no cliente e no
servidor, e a restrição de unicidade é do banco — validar só no cliente não resolve
dois aparelhos editando offline.

**Regra do total.** O total de uma compra soma apenas itens marcados como comprados,
pelo preço encontrado e pela quantidade levada. Nunca o preço estimado do
planejamento.

**Idempotência do registro de compra.** Marcar um item como comprado precisa ser
reenviável sem duplicar o registro — a fila de escrita offline vai reenviar.

## Endpoints

Nenhum endpoint acordado até aqui. A Sprint 0 entrega estrutura e processo; o
contrato começa a ser preenchido na Sprint 1, junto com as histórias de conta de
usuário e sincronização.

O formato de cada entrada, quando começarem a entrar:

```text
### <método> <caminho>
Estado: planejado | em implementação | disponível
Histórias: #<issue>
Autorização: <papel exigido>

Requisição:  <corpo, com tipos>
Resposta:    <corpo, com tipos>
Erros:       <código> — <quando>
```

## Histórico de mudanças

| Data | Mudança | PR |
| --- | --- | --- |
| 16/09/2026 | Documento criado com as regras comuns, sem endpoints | — |
