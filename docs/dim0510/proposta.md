# Proposta — TáNaLista

**DIM0524 — Desenvolvimento de Sistemas para Dispositivos Móveis · Sprint 0 · 2026.2**

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

**Hipótese de valor.** Acreditamos que quem faz a compra do mês vai abrir o aplicativo no corredor, e não só em casa, porque saber o total antes do caixa evita a devolução de item na fila — e que vai continuar preenchendo preço porque esse é o dado que produz o total.

**Público.** Domicílios que fazem uma compra grande mensal e complementos semanais, em que mais de uma pessoa mexe na mesma lista. Aparelho Android de faixa intermediária, uma mão ocupada com o carrinho, sinal ruim dentro da loja.

**Evidência do problema.** _[Preencher: quantas pessoas entrevistadas, como fazem a lista hoje, se estimam o total antes do caixa, com que frequência devolvem item por estourar o orçamento.]_

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

**Regra do total.** O total do carrinho soma apenas itens marcados como comprados, com o preço encontrado e a quantidade efetivamente levada — não o preço estimado no planejamento. Um total que mistura planejado e comprado não serve para decidir nada no caixa, que é o momento em que o número importa.

**Critérios de sucesso do MVP.** Uma compra real de mês registrada de ponta a ponta pelo aplicativo, com o total conferindo com o cupom fiscal dentro da margem dos itens não previstos; uma lista compartilhada e usada por duas pessoas; nenhuma perda de marcação em uso offline com o modo avião ligado durante toda a compra.

---

## 3. Modelo de domínio

```text
Usuário  ──< Acesso >──  Lista            papel: dono | editor
Lista    ──<            Item              nome normalizado único por lista
Lista    ──<            Compra            sessão do Modo Mercado, com total e data
Compra   ──< Registro >── Item            preço encontrado e quantidade levada
```

A unicidade de nome não é comparação de texto simples: `Compras do mês` e `compras do mês` são o mesmo, enquanto `Leite 200g` e `Leite 400g` não são. A chave é o nome normalizado — caixa baixa, acentos removidos, espaços colapsados —, o que torna a regra verificável tanto no cliente quanto no servidor, com o mesmo resultado.

A **Compra** é a entidade que separa planejamento de execução. Sem ela, marcar um item comprado destrói o dado da lista e impede repetir a lista no mês seguinte. Com ela, a lista é um modelo reutilizável e cada ida ao mercado é um registro próprio — o que também dá o histórico sem custo adicional de modelagem.

---

## 4. Backlog inicial

Quadro no GitHub Projects: _[link]_. Estimativa em pontos de história, escala de Fibonacci.

| Prio | História | Critérios de aceitação | Est. | Entrega |
| --- | --- | --- | --- | --- |
| P1 | Como usuário, quero criar uma lista para separar compras por ocasião | Nome obrigatório, com limite de caracteres; duplicidade bloqueada por nome normalizado | 3 | 1 |
| P1 | Como usuário, quero ver minhas listas com o progresso para saber o que falta | Nome, número de itens, comprados sobre total, ordenadas por atualização | 3 | 1 |
| P1 | Como usuário, quero adicionar itens com quantidade, unidade e categoria para planejar | Só o nome é obrigatório; quantidade positiva quando informada; unidade e categoria opcionais | 5 | 1 |
| P1 | Como usuário, quero ser impedido de repetir um produto na mesma lista | Bloqueio por nome normalizado; variações como `Leite 200g` e `Leite 400g` aceitas | 3 | 1 |
| P1 | Como usuário, quero editar e remover itens para corrigir a lista | Edição de todos os campos; remoção com desfazer | 2 | 1 |
| P1 | Como usuário, quero renomear e excluir uma lista | Exclusão remove os itens associados, com confirmação | 2 | 1 |
| P1 | Como usuário, quero ativar o Modo Mercado para executar a compra | Estado da lista muda; interface passa a priorizar leitura e toque grandes | 5 | 2 |
| P1 | Como usuário, quero marcar item comprado e informar o preço encontrado | Subtotal por item; total soma apenas comprados; edição em uma tela só | 5 | 2 |
| P1 | Como usuário, quero acompanhar o total do carrinho para não estourar no caixa | Total sempre visível, atualizado a cada marcação | 3 | 2 |
| P2 | Como usuário, quero que alteração estrutural peça confirmação no Modo Mercado | Renomear lista, adicionar e remover item exigem confirmação; preço e quantidade não | 3 | 2 |
| P1 | Como usuário, quero entrar na minha conta para usar minhas listas em outro aparelho | Cadastro e login pela API; token em armazenamento seguro do sistema | | 3 |
| P1 | Como usuário, quero usar o aplicativo sem sinal dentro do mercado | Leitura sempre local; escritas em fila; indicador visível de pendência | | 3 |
| P1 | Como usuário, quero que a compra suba sozinha quando o sinal voltar | Sincronização automática; política de conflito declarada em `docs/offline.md` | | 3 |
| P2 | Como usuário, quero compartilhar uma lista com quem mora comigo | Convite por e-mail da conta; papel de editor; alterações dos dois aparecem após sincronizar | | 3 |
| P2 | Como usuário, quero ver o histórico de compras para lembrar quanto gastei | Compras finalizadas com data, lista de origem e total | | 3 |
| P2 | Como usuário, quero adicionar item lendo o código de barras para não digitar | Câmera resolve o código; caminho manual permanece disponível | | final |
| P3 | Como usuário, quero compartilhar a lista com alguém que não usa o app | Exportação pelo seletor do sistema | | final |

P1 é essencial ao MVP, P2 é importante, P3 é desejável.

---

## 5. Plataforma-alvo

**Android**, com **desktop** como alvo secundário configurado no projeto.

Justificativa a partir do produto e do público: o momento que define o TáNaLista é o corredor do mercado, com uma mão no carrinho e sinal ruim. Isso é um aparelho no bolso, não um navegador. O público faz a compra com Android de faixa intermediária, e a equipe tem aparelhos Android físicos, o que permite testar em campo o cenário real — inclusive com o modo avião ligado — sem depender de emulador.

O alvo desktop cobre o outro momento do produto, o planejamento em casa, onde digitar uma lista de trinta itens no teclado é mais rápido, e dá um ciclo de edição e visualização rápido durante o desenvolvimento. Ele também é a base da intenção de concorrer ao bônus de entrega multiplataforma, com adaptação de layout para janela larga.

**Descartado:** iOS como alvo prioritário, porque nenhum integrante possui iPhone ou Mac e a entrega ficaria restrita ao simulador, longe do público real. **Descartado:** web como alvo prioritário, porque o bloco final exige câmera, armazenamento seguro e publicação em canal de distribuição.

---

## 6. Backend

**Opção C — API própria de Web II**, desenvolvida por Vinicius, integrante da equipe, em DIM0547.

Justificativa a partir do produto: as regras que sustentam o TáNaLista são regras de servidor, não de gaveta de dados. A unicidade por nome normalizado precisa valer entre dois aparelhos que editaram a mesma lista offline; o compartilhamento exige autorização por recurso, e não por tabela; e o fechamento de uma Compra é uma transação que precisa resolver a marcação concorrente de dois aparelhos no mesmo corredor. Controlar o backend permite implementar a normalização, a restrição de unicidade e a idempotência do registro de compra do lado do servidor, com o mesmo algoritmo do cliente. Além disso, a opção C é a única que dá direito ao bônus de integração entre disciplinas.

**Descartado:** Supabase, porque o grupo passaria a modelar a regra de negócio dentro dos limites do que o serviço oferece, e porque abriria mão do bônus de integração tendo um integrante cursando DIM0547. **Descartado:** Firebase, porque o modelo de consulta do Firestore obrigaria a desnormalizar o compartilhamento e não oferece restrição de unicidade transacional, que é o mecanismo da regra de item duplicado. **Descartado:** apenas local com APIs públicas, porque a lista compartilhada da casa é colaborativa por definição e a Sprint 3 exige consumo de API real.

**Contrato e desacoplamento.** O contrato precede a implementação dos dois lados e vive em `docs/contrato-api.md`, espelhando o OpenAPI do monorepo. A camada de dados do aplicativo é uma interface no domínio, com duas implementações: a de rede, com Ktor Client, e uma local que serve de implementação falsa. O aplicativo compila, roda e é demonstrável com a segunda enquanto o endpoint correspondente não existe — o risco de acoplar dois cronogramas de disciplina é real, e essa é a mitigação.

**Persistência local: SQLDelight.** O SQL é escrito à mão e verificado em tempo de compilação, o que casa com um domínio que já depende de consulta — total do carrinho, progresso da lista, unicidade normalizada — e evita descobrir erro de consulta em execução, no meio do mercado. Funciona em `commonMain` para Android e desktop com a mesma configuração, e o esquema versionado com migração é exigido na Sprint 3. **Descartado:** Room, cujo modelo de anotações é mais familiar, mas cujo suporte multiplataforma é mais recente e adiciona atrito de processamento de anotações no alvo desktop.

---

## 7. Equipe

| Nome | Matrícula | Conta GitHub | Papel |
| --- | --- | --- | --- |
| Thallys | 20240011552 | thallystorres | Developer · Product Owner na Sprint 0 |
| Ivis | 20220028454 | ivixs | Developer · Scrum Master na Sprint 0 |
| Vinicius | 20230051760 | vbarbosadev | Developer · guardião do contrato da API · responsável pelo backend em DIM0547 |

Os três integrantes cursam DIM0524 e contribuem com o aplicativo. Vinicius cursa também DIM0547 e responde sozinho pelo backend naquela disciplina.

---

## 8. Coorte e integração

**Coorte de apresentação:** B — online

**Intenção de entrega multiplataforma:** Android e desktop, com adaptação de interface para janela larga, a partir da Sprint 1.

**Integração entre disciplinas:** o aplicativo consome a API do próprio grupo em **DIM0547 — Desenvolvimento de Sistemas Web II**, e o mesmo produto é objeto de estudo em **DIM0510 — Processos de Software**, com entregáveis distintos. Aqui o objeto avaliado é o aplicativo; em DIM0547, o backend, os contratos e a infraestrutura; em DIM0510, o processo. Repositórios vinculados nos READMEs, conforme AVALIACAO.md §5.