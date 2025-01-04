package com.example.pokedex.mainViews.PokemonDetailView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon

class PokemonDetailViewModel: ViewModel() {
    private val pokemonRepository = DependencyContainer.pokemonRepository

    var favouriteButtonText by mutableStateOf("")

    fun getPokemonByName(pokemonName: String): Pokemon {
        val pokemon = pokemonRepository.getPokemonByName(pokemonName)
        onFavouriteButton(pokemon)
        return pokemon
    }

    suspend fun savePokemon(pokemon: Pokemon) {
        if (pokemonRepository.pokemonIsFavourite(pokemon)) {
            pokemonRepository.removeFromFavourites(pokemon)
        } else {
            pokemonRepository.savePokemon(pokemon)
        }
        onFavouriteButton(pokemon)
    }

    private fun onFavouriteButton(pokemon: Pokemon)  {
        favouriteButtonText = if (pokemonRepository.pokemonIsFavourite(pokemon)) {
            "Remove from Favourites"
        } else {
            "Add to Favourites"
        }
    }
}