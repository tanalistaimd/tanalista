package br.ufrn.tanalista.ui.mercado

private const val CASAS_PRECO = 2
private const val CASAS_QUANTIDADE = 3
private val PADRAO_DECIMAL = Regex("([0-9]+)(?:[,.]([0-9]+))?")

internal fun interpretarDecimal(
    texto: String,
    casas: Int,
): Int? {
    val partes = PADRAO_DECIMAL.matchEntire(texto.trim()) ?: return null
    val (inteiro, fracao) = partes.destructured
    return if (fracao.length <= casas) {
        (inteiro + fracao.padEnd(casas, '0')).trimStart('0').ifEmpty { "0" }.toIntOrNull()
    } else {
        null
    }
}

internal fun formatarPreco(centavos: Long): String = "R$ ${formatarDecimal(centavos, CASAS_PRECO)}"

internal fun formatarQuantidade(milesimos: Int): String =
    formatarDecimal(milesimos.toLong(), CASAS_QUANTIDADE).trimEnd('0').trimEnd(',')

private fun formatarDecimal(
    valor: Long,
    casas: Int,
): String {
    val digitos = valor.toString().padStart(casas + 1, '0')
    return "${digitos.dropLast(casas)},${digitos.takeLast(casas)}"
}
