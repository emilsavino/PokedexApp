package com.example.pokedex.mainViews.PokemonDetailView

import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.shared.Pokemon

class PokemonDetailViewModel {
    val pokemonRepository = PokemonRepository

    fun getPokemonByName(pokemonName: String): Pokemon {
        return pokemonRepository.getPokemonByName(pokemonName)
    }

    fun savePokemon(pokemon: Pokemon) {
        pokemonRepository.savePokemon(pokemon)
    }
}