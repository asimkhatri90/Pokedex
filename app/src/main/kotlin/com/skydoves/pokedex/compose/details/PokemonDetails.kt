package com.skydoves.pokedex.compose.details

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.palette.graphics.Palette
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.palette.PalettePlugin
import com.skydoves.pokedex.compose.theme.PokeDexAppTheme
import com.skydoves.pokedex.compose.theme.flying
import com.skydoves.pokedex.compose.theme.md_blue_200
import com.skydoves.pokedex.compose.theme.md_green_200
import com.skydoves.pokedex.compose.theme.md_orange_100
import com.skydoves.pokedex.compose.theme.primary
import com.skydoves.pokedex.compose.theme.white93
import com.skydoves.pokedex.compose.theme.white_70
import com.skydoves.pokedex.core.model.Pokemon
import com.skydoves.pokedex.core.model.PokemonInfo
import com.skydoves.pokedex.utils.PokemonTypeUtils

@Composable
fun PokemonDetails(pokemon: Pokemon, viewModel: PokemonDetailsViewModel, pressOnBack: () -> Unit) {

  val pokemonInfo: PokemonInfo? by viewModel.pokemonInfoFlow.collectAsState(initial = null)
  val isLoading: Boolean by viewModel.isLoading
  val toastMessage: String? by viewModel.toastMessage

  val snackBarHostState = remember { SnackbarHostState() }
  if (toastMessage != null) {
    LaunchedEffect(snackBarHostState) {
      snackBarHostState.showSnackbar(toastMessage!!)
    }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
  ) { innerPadding ->
    val modifier = Modifier.padding(innerPadding)
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier
        .fillMaxSize()
    ) {
      PokemonDetailsTopBanner(
        modifier = modifier,
        imageUrl = pokemon.getImageUrl(),
        id = if (pokemonInfo != null) pokemonInfo!!.getIdString() else "",
        goBack = pressOnBack
      )
      if (pokemonInfo != null) {
        PokemonDetailsBody(modifier = modifier, pokemon = pokemon, pokemonInfo = pokemonInfo!!)
      }
    }

    if (isLoading) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        CircularProgressIndicator(
          modifier = modifier.width(64.dp),
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
  }
}

@Composable
private fun PokemonDetailsTopBanner(
  modifier: Modifier,
  imageUrl: String,
  id: String,
  goBack: () -> Unit
) {

  val palette: MutableState<Palette?> = remember {
    mutableStateOf(null)
  }

  val dominant =
    palette.value?.dominantSwatch?.rgb?.let { Color(it) } ?: MaterialTheme.colorScheme.primary

  val light =
    palette.value?.lightVibrantSwatch?.rgb?.let { Color(it) }

  var gradient: Brush =
    Brush.verticalGradient(colorStops = arrayOf(0.5f to dominant, 0.5f to dominant))
  if (light != null) {
    gradient = Brush.verticalGradient(listOf(dominant, light))
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .fillMaxWidth()
      .height(260.dp)
      .clip(RoundedCornerShape(bottomEnd = 50.dp, bottomStart = 50.dp))
      .background(gradient)
  ) {
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = modifier
        .fillMaxWidth()
        .height(50.dp)
        .padding(10.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          tint = Color.White,
          imageVector = Icons.Filled.ArrowBack,
          contentDescription = "Back",
          modifier = Modifier
            .clickable { goBack() })

        Text(text = "Pokedex", style = MaterialTheme.typography.titleLarge, color = Color.White)
      }
      Text(text = id, style = MaterialTheme.typography.titleMedium)
    }

    CoilImage(
      modifier = modifier.size(190.dp),
      imageModel = { imageUrl },
      imageOptions = ImageOptions(contentScale = ContentScale.Crop),
      component = rememberImageComponent {
        +PalettePlugin {
          palette.value = it
        }
      }
    )
  }
}

@Composable
private fun PokemonDetailsBody(
  modifier: Modifier,
  pokemon: Pokemon,
  pokemonInfo: PokemonInfo
) {

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .fillMaxSize()
      .padding(10.dp)
  ) {

    Text(
      modifier = modifier.padding(top = 20.dp),
      text = pokemon.name.replaceFirstChar { it.titlecase() },
      style = MaterialTheme.typography.displaySmall
    )

    LazyRow(
      modifier = modifier
        .fillMaxWidth()
        .padding(top = 10.dp),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      items(pokemonInfo.types.size) { index ->
        val typeName = pokemonInfo.types[index].type.name
        Box(
          modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(colorResource(PokemonTypeUtils.getTypeColor(typeName)))
            .padding(top = 2.dp, start = 44.dp, end = 44.dp, bottom = 8.dp)
        ) {
          Text(
            text = typeName,
            style = MaterialTheme.typography.titleMedium
          )
        }
      }
    }

    PokemonDetailsWeightStats(
      modifier = modifier.padding(top = 20.dp, bottom = 12.dp),
      weight = pokemonInfo.getWeightString(),
      height = pokemonInfo.getHeightString()
    )

    PokemonDetailsBaseStats(modifier = Modifier, pokemonInfo = pokemonInfo)
  }
}

@Composable
private fun PokemonDetailsWeightStats(modifier: Modifier, weight: String, height: String) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = weight, style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(10.dp)
      )
      Text(text = "Weight", style = MaterialTheme.typography.titleSmall)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = height, style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(10.dp)
      )
      Text(text = "Height", style = MaterialTheme.typography.titleSmall)
    }
  }
}

@Composable
private fun PokemonDetailsBaseStats(modifier: Modifier, pokemonInfo: PokemonInfo) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(top = 12.dp),
    verticalArrangement = Arrangement.SpaceEvenly,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Base Stats",
      fontWeight = FontWeight.Bold,
      fontSize = 21.sp,
      color = white93,
    )
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier.padding(top = 5.dp)
    ) {
      BaseStatsRow(
        modifier = modifier,
        statName = "  HP",
        stats = pokemonInfo.getHpString(),
        progress = pokemonInfo.hp,
        maxProgress = PokemonInfo.MAX_HP,
        progressColor = primary
      )
      BaseStatsRow(
        modifier = modifier,
        statName = "ATK",
        stats = pokemonInfo.getAttackString(),
        progress = pokemonInfo.attack,
        maxProgress = PokemonInfo.MAX_ATTACK,
        progressColor = md_orange_100
      )
      BaseStatsRow(
        modifier = modifier,
        statName = "DEF",
        stats = pokemonInfo.getDefenseString(),
        progress = pokemonInfo.defense,
        maxProgress = PokemonInfo.MAX_DEFENSE,
        progressColor = md_blue_200
      )
      BaseStatsRow(
        modifier = modifier,
        statName = "SPD",
        stats = pokemonInfo.getSpeedString(),
        progress = pokemonInfo.speed,
        maxProgress = PokemonInfo.MAX_SPEED,
        progressColor = flying
      )
      BaseStatsRow(
        modifier = modifier,
        statName = "EXP",
        stats = pokemonInfo.getExpString(),
        progress = pokemonInfo.exp,
        maxProgress = PokemonInfo.MAX_EXP,
        progressColor = md_green_200
      )
    }
  }
}

@Composable
private fun BaseStatsRow(
  modifier: Modifier,
  statName: String,
  stats: String,
  progress: Int,
  maxProgress: Int,
  progressColor: Color,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = statName, color = white_70, fontWeight = FontWeight.Bold)
    Box(modifier = modifier.padding(start = 16.dp)) {
      ProgressView(
        modifier = modifier,
        labelText = stats,
        labelSize = 12.sp,
        labelColorInner = Color.White,
        labelColorOuter = Color.Black,
        progress = progress,
        max = maxProgress,
        colorProgress = progressColor,
        colorBackground = Color.White,
        radius = 12.dp
      )
    }
  }
}

@Composable
private fun ProgressView(
  modifier: Modifier,
  labelText: String,
  labelSize: TextUnit,
  labelColorInner: Color,
  labelColorOuter: Color,
  progress: Int,
  max: Int,
  colorProgress: Color,
  colorBackground: Color,
  radius: Dp,
) {
  // Range of 0 to 1
  var progressFloat by remember { mutableFloatStateOf(0f) }
  val animatedSize by animateFloatAsState(
    targetValue = progressFloat,
    tween(
      durationMillis = 1000,
      delayMillis = 200,
      easing = LinearOutSlowInEasing
    ), label = "ProgressFloat"
  )
  // This is used to get the max width of the progress bar
  var outerBoxWidth by remember { mutableIntStateOf(0) }

  val extraSpace = 50
  val textStyle = LocalTextStyle.current
  val textMeasurer = rememberTextMeasurer()
  val labelWidth = remember(textStyle, textMeasurer) {
    textMeasurer.measure(
      text = labelText,
      style = textStyle.copy(fontSize = labelSize)
    ).size.width
  }

  ConstraintLayout(
    modifier = modifier
      .fillMaxWidth()
      .height(18.dp)
  ) {
    val (outerBox, innerBox, label) = createRefs()

    Box(
      modifier = Modifier
        .constrainAs(outerBox) {
          start.linkTo(parent.start)
          top.linkTo(parent.top)
          bottom.linkTo(parent.bottom)
        }
        .fillMaxSize()
        .clip(RoundedCornerShape(radius))
        .background(colorBackground)
        .onSizeChanged {
          outerBoxWidth = it.width
        }
    )

    Box(
      modifier = Modifier
        .constrainAs(innerBox) {
          start.linkTo(outerBox.start)
          top.linkTo(outerBox.top)
          bottom.linkTo(outerBox.bottom)
        }
        .fillMaxWidth(animatedSize)
        .fillMaxHeight()
        .clip(RoundedCornerShape(radius))
        .background(colorProgress)
        .animateContentSize()
    )

    // If the label's width is smaller than the progress bar's width than we show the label inside
    // otherwise we show it on the outer side of the progress bar
    val showLabelInsideBox = labelWidth < (outerBoxWidth * progressFloat) - extraSpace
    Text(
      modifier = Modifier
        .constrainAs(label) {
          if (showLabelInsideBox) {
            end.linkTo(innerBox.end)
            top.linkTo(innerBox.top)
            bottom.linkTo(innerBox.bottom)
          } else {
            start.linkTo(innerBox.end)
            top.linkTo(innerBox.top)
            bottom.linkTo(innerBox.bottom)
          }
        }
        .padding(horizontal = 5.dp),
      text = labelText,
      fontSize = labelSize,
      color = if (showLabelInsideBox) labelColorInner else labelColorOuter,
    )
  }

  // Calculate the actual progress in range from 0 to 1
  LaunchedEffect(true) {
    progressFloat = progress / max.toFloat()
  }
}

@Composable
@Preview
private fun PokemonDetailsPreview() {
  PokeDexAppTheme(darkTheme = false) {
    val pokemon = Pokemon(1, "Bulbusar", "3")
    val pokemonInfo = PokemonInfo(
      id = 1,
      name = pokemon.name,
      height = 7,
      weight = 69,
      experience = 60,
      types = emptyList(),
    )
    PokemonDetailsBody(modifier = Modifier, pokemon = pokemon, pokemonInfo = pokemonInfo)
  }
}