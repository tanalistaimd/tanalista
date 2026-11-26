package br.ufrn.tanalista

import br.ufrn.tanalista.data.RepositorioListasEmMemoria
import br.ufrn.tanalista.domain.RepositorioListas
import br.ufrn.tanalista.presentation.item.ListaDetalheViewModel
import br.ufrn.tanalista.presentation.lista.ListasViewModel
import br.ufrn.tanalista.presentation.mercado.MercadoViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val moduloTaNaLista =
    module {
        single<RepositorioListas> { RepositorioListasEmMemoria() }
        viewModelOf(::ListasViewModel)
        viewModelOf(::ListaDetalheViewModel)
        viewModelOf(::MercadoViewModel)
    }
