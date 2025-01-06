package com.example.pokedex.mainViews.PokemonDetailView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PokemonDetailViewModel(private val name: String): ViewModel() {
    private val pokemonRepository = DependencyContainer.pokemonRepository
    private val favouritesRepository = DependencyContainer.favouritesRepository

    private val _isFavorited = MutableStateFlow(false)
    val isFavorited: StateFlow<Boolean> = _isFavorited.asStateFlow()

    private val _pokemon: MutableStateFlow<PokemonDetailUIState> = MutableStateFlow(PokemonDetailUIState.Empty)
    val pokemon: StateFlow<PokemonDetailUIState> = _pokemon.asStateFlow()

    init {
        viewModelScope.launch {
            pokemonRepository.pokemonFlow.collect { newPokemon ->
                _pokemon.update {
                    onFavouriteButton(newPokemon)
                    PokemonDetailUIState.Data(newPokemon)
                }
            }
        }
        getPokemonByName()
    }

    private fun getPokemonByName() = viewModelScope.launch {
        _pokemon.update {
            PokemonDetailUIState.Loading
        }
        pokemonRepository.getPokemonByName(name)
    }

    suspend fun savePokemon(pokemon: Pokemon) {
        val currentFav = favouritesRepository.pokemonIsFavourite(pokemon)

        val newFav = !currentFav
        _isFavorited.value = newFav
        onFavouriteButton(pokemon)

        try {
            if (currentFav) {
                favouritesRepository.removeFromFavourites(pokemon)
            } else {
                favouritesRepository.makeFavourite(pokemon)
            }
        } catch (_: Exception) {
            _isFavorited.value = currentFav
            onFavouriteButton(pokemon)
        }


    }

    private fun onFavouriteButton(pokemon: Pokemon)  {
        _isFavorited.value = favouritesRepository.pokemonIsFavourite(pokemon)
    }
}

sealed class PokemonDetailUIState {
    data class Data(val pokemon: Pokemon): PokemonDetailUIState()
    object Loading: PokemonDetailUIState()
    object Empty: PokemonDetailUIState()
}