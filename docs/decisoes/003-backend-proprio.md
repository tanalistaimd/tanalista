# 003 — Backend próprio em DIM0547, em vez de BaaS

**Data:** 14/09/2026
**Estado:** aceita

## Contexto

O aplicativo precisa de conta de usuário, lista compartilhada entre duas pessoas e
sincronização do que foi registrado offline. A disciplina oferece três caminhos:
serviço gerenciado (Supabase, Firebase), apenas local com APIs públicas, ou API
própria do grupo — e Vinicius cursa DIM0547, onde um backend é o objeto avaliado.

O que decide não é a conveniência de subir um backend rápido. São três regras do
produto, e onde elas precisam valer:

- **Unicidade por nome normalizado** precisa valer entre dois aparelhos que editaram
  a mesma lista offline. Validar no cliente não resolve: os dois clientes validam,
  os dois aceitam, e a lista fica com o item duplicado ao sincronizar.
- **Compartilhamento** é autorização por recurso — esta lista, para esta conta, neste
  papel —, não por tabela.
- **Fechamento de uma compra** é transação, e precisa resolver a marcação concorrente
  de dois aparelhos no mesmo corredor sem duplicar o registro.

## Decisão

**Opção C — API própria do grupo**, desenvolvida por Vinicius em DIM0547, em
[monorepo separado](https://github.com/vbarbosadev/tanalista-api).

Controlar o servidor permite implementar a normalização, a restrição de unicidade e a
idempotência do registro de compra do lado de lá, com o mesmo algoritmo do cliente.
A opção C é também a única que dá direito ao bônus de integração entre disciplinas,
tendo um integrante cursando DIM0547.

O contrato entre os dois repositórios vive em [`../contrato-api.md`](../contrato-api.md)
e **precede a implementação dos dois lados**.

## Alternativas descartadas

**Supabase.** Sobe rápido e resolve autenticação de graça. Descartado porque a equipe
passaria a modelar a regra de negócio dentro do que o serviço permite expressar, em
vez de dentro do domínio — e porque abriria mão do bônus de integração tendo um
integrante em DIM0547.

**Firebase.** Descartado por motivo técnico específico: o modelo de consulta do
Firestore obrigaria a desnormalizar o compartilhamento, e não oferece restrição de
unicidade transacional — que é exatamente o mecanismo da regra de item duplicado.
Seria escolher um banco que não sabe expressar a regra central do produto.

**Apenas local, com APIs públicas.** Descartado porque a lista compartilhada da casa
é colaborativa por definição — foi o comportamento que apareceu nas conversas de
origem, com gente mandando no grupo da família o que faltou — e porque a Sprint 3
exige consumo de API real.

## Consequências

**Dois cronogramas de disciplina ficam acoplados**, e esse é o risco central do
semestre. A mitigação é arquitetural: a camada de dados é uma interface no domínio
com duas implementações, a de rede (Ktor Client) e uma local que serve de
implementação falsa. O aplicativo compila, roda e é demonstrável com a segunda
enquanto o endpoint não existe — e a condição 7 da Definição de Pronto exige teste
contra ela em todo item que consome a API.

A espera vira dado em vez de sumir: item bloqueado recebe a etiqueta
`aguardando-contrato`, sai do limite de WIP e tem o tempo etiquetado medido. É a
matéria-prima do VSM da Sprint 3, e a hipótese que a equipe já carrega é que o gargalo
não está no aplicativo nem na API, mas na disponibilidade de uma pessoa para os dois.

**O contrato só muda por pull request**, revisado por quem não é o guardião. Mudança
combinada em conversa não vale.

Se o backend não chegar a tempo em alguma sprint, o aplicativo é demonstrado com a
implementação falsa e o item fica registrado como bloqueado — não se improvisa um
Firebase no meio do caminho, porque isso jogaria fora a justificativa desta decisão
e o bônus de integração junto.
