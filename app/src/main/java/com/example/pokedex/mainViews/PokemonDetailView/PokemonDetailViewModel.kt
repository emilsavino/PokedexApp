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
    private val recentlyViewedRepository = DependencyContainer.recentlyViewedRepository

    var favouriteButtonText by mutableStateOf("")

    private val _pokemon: MutableStateFlow<PokemonDetailUIState> = MutableStateFlow(PokemonDetailUIState.Empty)
    val pokemon: StateFlow<PokemonDetailUIState> = _pokemon.asStateFlow()

    init {
        viewModelScope.launch {
            pokemonRepository.pokemonFlow.collect { newPokemon ->
                recentlyViewedRepository.addToRecents(newPokemon)
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
        if (favouritesRepository.pokemonIsFavourite(pokemon)) {
            favouritesRepository.removeFromFavourites(pokemon)
        } else {
            favouritesRepository.makeFavourite(pokemon)
        }
        onFavouriteButton(pokemon)
    }

    private fun onFavouriteButton(pokemon: Pokemon)  {
        favouriteButtonText = if (favouritesRepository.pokemonIsFavourite(pokemon)) {
            "Remove from Favourites"
        } else {
            "Add to Favourites"
        }
    }
}

sealed class PokemonDetailUIState {
    data class Data(val pokemon: Pokemon): PokemonDetailUIState()
    object Loading: PokemonDetailUIState()
    object Empty: PokemonDetailUIState()
}