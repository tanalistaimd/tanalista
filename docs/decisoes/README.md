# Registros de decisão

Uma decisão técnica por arquivo, numerada em ordem de tomada. Decisão tomada em
conversa e não registrada aqui é tratada como **não tomada** — é a regra do acordo
de processo, e existe porque equipe de três pessoas confunde "combinamos" com
"alguém lembrou".

Cada registro responde a quatro perguntas: qual era o problema, o que foi decidido,
o que foi descartado e por quê, e o que muda se a decisão se mostrar errada.

| # | Decisão | Data |
| --- | --- | --- |
| [001](001-kotlin-multiplatform-compose.md) | Kotlin Multiplatform com Compose Multiplatform | 14/09/2026 |
| [002](002-android-prioritario.md) | Android prioritário, desktop secundário, iOS e web descartados | 14/09/2026 |
| [003](003-backend-proprio.md) | Backend próprio em DIM0547, em vez de BaaS | 14/09/2026 |
| [004](004-sqldelight.md) | SQLDelight para persistência local | 14/09/2026 |

## Formato

```markdown
# NNN — Título

**Data:** dd/mm/aaaa
**Estado:** aceita | substituída por NNN | revista em NNN

## Contexto
## Decisão
## Alternativas descartadas
## Consequências
```

Uma decisão não é apagada quando muda de ideia: escreve-se outra, e a antiga passa a
`substituída por NNN`. O histórico de por que a equipe mudou vale mais que a limpeza
do diretório.
