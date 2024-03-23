package com.skydoves.pokedex.compose.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.skydoves.pokedex.compose.theme.PokeDexAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      PokeDexAppTheme {
        MainScreen()
      }
    }
  }
}
