package com.skydoves.pokedex.compose.list

import androidx.annotation.MainThread
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.skydoves.pokedex.core.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel
  @Inject
  constructor(
    val repository: MainRepository,
  ) : ViewModel() {
    private var _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private var _toastMessage: MutableState<String?> = mutableStateOf(null)
    val toastMessage: State<String?> get() = _toastMessage

    private val pokemonFetchingIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val pokemonListFlow =
      pokemonFetchingIndex.flatMapLatest { page ->
        repository.fetchPokemonList(page, {
          _isLoading.value = true
        }, {
          _isLoading.value = false
        }, { error ->
          _toastMessage.value = error
        })
      }



    @MainThread
    fun fetchNextPokemonList() {
      if (!_isLoading.value) pokemonFetchingIndex.value++
    }
  }
