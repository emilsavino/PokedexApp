package com.example.pokedex.mainViews.PokemonDetailView

import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.shared.MockPokemon

class PokemonDetailViewModel {
    val pokemonRepository = PokemonRepository()

    fun getPokemonByName(pokemonName: String): MockPokemon {
        return pokemonRepository.getPokemonByName(pokemonName)
    }
}