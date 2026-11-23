package br.ufrn.tanalista.model

private const val MILESIMOS_POR_UNIDADE = 1_000

/** Quantidade em milésimos da unidade do item e preço encontrado em centavos por unidade. */
data class RegistroCompra(
    val precoUnitarioCentavos: Int,
    val quantidadeMilesimos: Int,
) {
    init {
        require(precoUnitarioCentavos >= 0) { "O preço encontrado não pode ser negativo." }
        require(quantidadeMilesimos > 0) { "A quantidade levada deve ser maior que zero." }
    }

    val subtotalCentavos: Long
        get() {
            val subtotalEmMilesimosDeCentavo = precoUnitarioCentavos.toLong() * quantidadeMilesimos
            // Cada subtotal é arredondado ao centavo mais próximo, com meio centavo para cima.
            return (subtotalEmMilesimosDeCentavo + MILESIMOS_POR_UNIDADE / 2) / MILESIMOS_POR_UNIDADE
        }
}

/** Sessão de compra de uma lista. Só itens marcados têm registro. */
class Compra private constructor(
    val listaId: Int,
    private val registros: Map<Int, RegistroCompra>,
) {
    constructor(listaId: Int) : this(listaId, emptyMap())

    val totalCentavos: Long
        get() =
            registros.values.fold(0L) { total, registro ->
                val subtotal = registro.subtotalCentavos
                check(subtotal <= Long.MAX_VALUE - total) { "O total da compra excede o limite em centavos." }
                total + subtotal
            }

    val itensComprados: Int
        get() = registros.size

    fun registroDoItem(item: ItemLista): RegistroCompra? = registros[idDoItem(item)]

    /** Marcar o mesmo item novamente substitui seu preço e sua quantidade. */
    fun marcarComprado(
        item: ItemLista,
        registro: RegistroCompra,
    ): Compra = Compra(listaId, registros + (idDoItem(item) to registro))

    fun desmarcar(item: ItemLista): Compra = Compra(listaId, registros - idDoItem(item))

    fun limitarA(itens: List<ItemLista>): Compra {
        val ids = itens.map(::idDoItem).toSet()
        return Compra(listaId, registros.filterKeys { it in ids })
    }

    private fun idDoItem(item: ItemLista): Int {
        require(item.listaId == listaId) { "O item não pertence à lista desta compra." }
        return item.id
    }
}
