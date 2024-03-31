package com.skydoves.pokedex.compose.main

import android.net.Uri
import android.os.Bundle
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.skydoves.pokedex.compose.details.PokemonDetails
import com.skydoves.pokedex.compose.details.PokemonDetailsViewModel
import com.skydoves.pokedex.compose.list.PokemonList
import com.skydoves.pokedex.core.model.Pokemon

@Composable
fun MainScreen() {
  val navHost = rememberNavController()

  NavHost(navController = navHost, startDestination = NavScreen.PokemonList.route) {
    composable(route = NavScreen.PokemonList.route) {
      PokemonList(hiltViewModel()) {
        val json = Uri.encode(Gson().toJson(it))
        navHost.navigate("${NavScreen.PokemonDetail.route}/$json")
      }
    }

    composable(
      route = NavScreen.PokemonDetail.routeWithParam,
      arguments =
      listOf(
        navArgument(NavScreen.PokemonDetail.argument0) { type = AssetParamType() },
      )
    ) { backStackEntry ->

      val pokemonName =
        backStackEntry.arguments?.getParcelable<Pokemon>(NavScreen.PokemonDetail.argument0)
          ?: return@composable

      PokemonDetails(pokemonName, hiltViewModel(
        creationCallback = { factory: PokemonDetailsViewModel.AssistedFactory ->
          factory.create(pokemonName.name)
        }
      )) {
        navHost.navigateUp()
      }
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


class AssetParamType : NavType<Pokemon>(isNullableAllowed = false) {
  override fun get(bundle: Bundle, key: String): Pokemon? {
    return bundle.getParcelable(key)
  }

  override fun parseValue(value: String): Pokemon {
    return Gson().fromJson(value, Pokemon::class.java)
  }

  override fun put(bundle: Bundle, key: String, value: Pokemon) {
    bundle.putParcelable(key, value)
  }
}