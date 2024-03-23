package com.skydoves.pokedex.compose.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.palette.PalettePlugin
import com.skydoves.pokedex.compose.theme.PokeDexAppTheme
import com.skydoves.pokedex.compose.theme.primary
import com.skydoves.pokedex.core.model.Pokemon

@Composable
fun PokemonList(
  viewModel: PokemonViewModel,
  pokemonSelected: (Pokemon) -> Unit,
) {
  val pokemonList: List<Pokemon> by viewModel.pokemonListFlow.collectAsState(initial = emptyList())
  val isLoading: Boolean by viewModel.isLoading
  val toastMessage: String? by viewModel.toastMessage

  val snackBarHostState = remember { SnackbarHostState() }

  val listState = rememberLazyGridState()
  val reachedBottom by remember {
    derivedStateOf { listState.reachedBottom() }
  }

  if (toastMessage != null) {
    LaunchedEffect(snackBarHostState) {
      snackBarHostState.showSnackbar(toastMessage!!)
    }
  }

  LaunchedEffect(reachedBottom) {
    if (reachedBottom) viewModel.fetchNextPokemonList()
  }

  Scaffold(
    topBar = { PokemonAppBar() },
    snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
  ) { innerPadding ->
    val modifier = Modifier.padding(innerPadding)
    LazyVerticalGrid(
      state = listState,
      modifier = modifier.padding(5.dp),
      columns = GridCells.Fixed(2),
    ) {
      items(pokemonList.size) { index ->
        PokemonItem(pokemon = pokemonList[index]) {
          pokemonSelected(it)
        }
      }
    }

    if (isLoading) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        CircularProgressIndicator(modifier = modifier.width(32.dp))
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonAppBar() {
  TopAppBar(
    title = { Text(text = "PokeDex") },
    colors =
      TopAppBarDefaults.topAppBarColors(
        containerColor = primary,
        titleContentColor = Color.White,
      ),
  )
}

@Composable
fun PokemonItem(
  modifier: Modifier = Modifier,
  pokemon: Pokemon,
  selectPokemon: (pokemon: Pokemon) -> Unit,
) {
  val imagePalette: MutableState<Palette?> = remember { mutableStateOf(null) }
  val cardBackground =
    imagePalette.value?.dominantSwatch?.rgb?.let { Color(it) }
      ?: MaterialTheme.colorScheme.onBackground

  Surface(
    modifier =
      modifier
        .fillMaxWidth()
        .padding(5.dp)
        .clickable(onClick = { selectPokemon(pokemon) }),
    color = cardBackground,
    shape = RoundedCornerShape(16.dp),
    tonalElevation = 4.dp,
  ) {
    Column(
      modifier = modifier.padding(vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      CoilImage(
        modifier = modifier.size(120.dp),
        imageModel = { pokemon.getImageUrl() },
        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
        component =
          rememberImageComponent {
            +PalettePlugin {
              imagePalette.value = it
            }
          },
      )
      Text(
        pokemon.name,
        style = MaterialTheme.typography.titleMedium,
      )
    }
  }
}

@Composable
@Preview
private fun PokemonItemPreview() {
  PokeDexAppTheme(darkTheme = false) {
    PokemonItem(pokemon = Pokemon(1, "Asim", "3")) {
    }
  }
}

internal fun LazyGridState.reachedBottom(buffer: Int = 1): Boolean {
  val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
  return lastVisibleItem?.index != 0 &&
    lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}
