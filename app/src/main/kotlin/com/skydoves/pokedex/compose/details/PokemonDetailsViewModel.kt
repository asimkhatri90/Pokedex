package com.skydoves.pokedex.compose.details

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.skydoves.pokedex.core.model.PokemonInfo
import com.skydoves.pokedex.core.repository.DetailRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

@HiltViewModel(assistedFactory = PokemonDetailsViewModel.AssistedFactory::class)
class PokemonDetailsViewModel @AssistedInject constructor(
  detailRepository: DetailRepository,
  @Assisted private val pokemonName: String,
) : ViewModel() {

  private var _isLoading = mutableStateOf(true)
  val isLoading: State<Boolean> get() = _isLoading

  private var _toastMessage: MutableState<String?> = mutableStateOf(null)
  val toastMessage: State<String?> get() = _toastMessage

  val pokemonInfoFlow: Flow<PokemonInfo?> = detailRepository.fetchPokemonInfo(
    name = pokemonName,
    onComplete = { _isLoading.value = false },
    onError = { _toastMessage.value = it },
  )

  init {
    Timber.d("init DetailViewModel")
  }

  @dagger.assisted.AssistedFactory
  interface AssistedFactory {
    fun create(pokemonName: String): PokemonDetailsViewModel
  }
}
