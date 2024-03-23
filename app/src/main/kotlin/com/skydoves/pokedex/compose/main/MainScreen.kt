package com.skydoves.pokedex.compose.main

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.skydoves.pokedex.compose.details.PokemonDetails
import com.skydoves.pokedex.compose.list.PokemonList

@Composable
fun MainScreen() {
  val navHost = rememberNavController()

  NavHost(navController = navHost, startDestination = NavScreen.PokemonList.route) {
    composable(NavScreen.PokemonList.route) {
      PokemonList(hiltViewModel()) {

      }
    }

    composable(
      route = NavScreen.PokemonDetail.routeWithParam,
      arguments =
        listOf(
          navArgument(NavScreen.PokemonDetail.argument0) { type = NavType.StringType },
        ),
    ) { backStackEntry ->

      val pokemonName =
        backStackEntry.arguments?.getString(NavScreen.PokemonDetail.argument0) ?: return@composable

      PokemonDetails(pokemonName)
    }
  }
}

sealed class NavScreen(val route: String) {
  object PokemonList : NavScreen("PokemonList")

  object PokemonDetail : NavScreen("PokemonDetail") {
    const val routeWithParam: String = "PokemonDetail/{pokemonName}"

    const val argument0: String = "pokemonName"
  }
}
