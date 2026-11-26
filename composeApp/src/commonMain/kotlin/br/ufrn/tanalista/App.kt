package br.ufrn.tanalista

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import br.ufrn.tanalista.presentation.item.ListaDetalheDestino
import br.ufrn.tanalista.presentation.lista.ListasDestino
import br.ufrn.tanalista.presentation.mercado.MercadoDestino
import br.ufrn.tanalista.presentation.navigation.BASE_DEEP_LINK_LISTA
import br.ufrn.tanalista.presentation.navigation.ExternalUriHandler
import br.ufrn.tanalista.presentation.navigation.ListaDetalheRota
import br.ufrn.tanalista.presentation.navigation.ListasRota
import br.ufrn.tanalista.presentation.navigation.MercadoRota
import br.ufrn.tanalista.presentation.tema.TaNaListaTema
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinConfiguration

@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration { modules(moduloTaNaLista) }) {
        TaNaListaTema {
            Scaffold { espacoSistema ->
                NavegacaoApp(Modifier.padding(espacoSistema))
            }
        }
    }
}

@Composable
private fun NavegacaoApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    DisposableEffect(navController) {
        ExternalUriHandler.ouvinte = { uri -> navController.navigate(NavUri(uri)) }
        onDispose { ExternalUriHandler.ouvinte = null }
    }

    NavHost(
        navController = navController,
        startDestination = ListasRota,
        modifier = modifier,
    ) {
        composable<ListasRota> {
            ListasDestino(
                viewModel = koinViewModel(),
                onAbrirLista = { lista -> navController.navigate(ListaDetalheRota(lista.id)) },
                onAbrirMercado = { lista -> navController.navigate(MercadoRota(lista.id)) },
            )
        }
        composable<ListaDetalheRota>(
            deepLinks = listOf(navDeepLink<ListaDetalheRota>(basePath = BASE_DEEP_LINK_LISTA)),
        ) { entrada ->
            val rota = entrada.toRoute<ListaDetalheRota>()
            ListaDetalheDestino(
                viewModel = koinViewModel(parameters = { parametersOf(rota.listaId) }),
                onVoltar = { navController.popBackStack() },
            )
        }
        composable<MercadoRota> { entrada ->
            val rota = entrada.toRoute<MercadoRota>()
            MercadoDestino(
                viewModel = koinViewModel(parameters = { parametersOf(rota.listaId) }),
                onVoltar = { navController.popBackStack() },
            )
        }
    }
}
