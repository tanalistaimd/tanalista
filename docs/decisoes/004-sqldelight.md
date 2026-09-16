# 004 — SQLDelight para persistência local

**Data:** 14/09/2026
**Estado:** aceita

## Contexto

O aplicativo é offline-first por definição: a base local é a fonte de leitura, e as
escritas entram em fila até o sinal voltar ([003](003-backend-proprio.md)). A
persistência local não é cache — é onde o produto funciona.

O domínio depende de consulta, não só de armazenamento. Total do carrinho, progresso
da lista, unicidade por nome normalizado e histórico de compras são todas perguntas
ao banco. E a Sprint 3 exige esquema versionado com migração.

A biblioteca precisa funcionar em `commonMain`, para Android e desktop, com a mesma
configuração ([001](001-kotlin-multiplatform-compose.md)).

## Decisão

**SQLDelight.** O SQL é escrito à mão em arquivos `.sq` e verificado em tempo de
compilação, que gera as funções tipadas a partir dele.

O argumento decisivo é onde o erro aparece. Com SQL verificado na compilação, uma
consulta errada quebra o build — e o build roda no CI, antes do merge. Com SQL
montado em execução, a mesma consulta quebra dentro do mercado, com o carrinho cheio,
que é o pior lugar possível para descobrir. Num domínio em que a consulta *é* a regra
— o total soma só o que foi marcado como comprado —, essa diferença paga o custo de
escrever o SQL à mão.

## Alternativas descartadas

**Room.** O modelo de anotações é mais familiar e a maior parte da equipe já viu Room
em disciplina anterior. Descartado porque o suporte multiplataforma é mais recente
que o do SQLDelight e adiciona atrito de processamento de anotações no alvo desktop —
justamente o alvo que existe para dar ciclo rápido. Trocar familiaridade por um alvo
que quebra é mau negócio.

**DataStore ou preferências.** Descartado por não ser banco: sem consulta, sem
restrição de unicidade, sem migração. O domínio tem quatro entidades relacionadas;
guardar isso como documento serializado significaria carregar tudo para a memória
para responder qualquer pergunta.

**Realm.** Descartado por estar em descontinuação anunciada — adotar base de dados
sem manutenção garantida, num projeto que atravessa o semestre, é risco sem
contrapartida.

**SQLite direto, sem camada.** Descartado porque devolveria o problema ao ponto de
partida: consulta como texto solto, erro em execução, tipagem manual.

## Consequências

**O esquema vira artefato versionado.** Os arquivos `.sq` entram no repositório e
mudança de esquema exige migração escrita — que é exatamente o que a Sprint 3 cobra,
então o custo já estava previsto.

Escrever SQL à mão é mais lento no começo que anotar uma entidade. É custo aceito em
troca de erro de consulta na compilação.

A entrada só acontece **depois da Sprint 0**: enquanto o CI não estiver verde,
estabilidade vem antes de funcionalidade. Até lá, a implementação falsa do repositório
guarda os dados em memória, e é ela que sustenta a demonstração.

Se o SQLDelight se mostrar um problema no alvo desktop — o inverso do que se espera —,
a saída é `expect`/`actual` na camada de dados, com implementação diferente por alvo,
sem tocar no domínio. A interface de repositório existe justamente para isso.
