package com.skydoves.pokedex.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme (
  background = background,
  onBackground = background_800,
  primary = primary,
)
private val WhiteColorPalette = lightColorScheme (
  background = Color.White,
)

@Composable
fun PokeDexAppTheme(darkTheme: Boolean = isSystemInDarkTheme(),
                    content : @Composable () -> Unit ) {

  val colors = DarkColorPalette

  val type = DarkTypography

  MaterialTheme(
    colorScheme = colors,
    typography = type,
    content = content
  )
}