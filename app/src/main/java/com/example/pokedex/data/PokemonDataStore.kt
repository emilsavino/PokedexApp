package com.example.pokedex.data

import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonList
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class PokemonDataStore {
    private val api = RetrofitInstance.apiService

    suspend fun fetchPokemons(limit: Int, offset: Int): PokemonList {
        return withContext(Dispatchers.IO) {
            api.getPokemons(limit,offset)
        }
    }

    suspend fun fetchPokemon(name: String): Pokemon {
        val pokemon = api.getPokemon(name.lowercase())
        pokemon.name = name.replaceFirstChar { it.uppercase() }
        return pokemon
    }
}