package com.example.pokedex.data

import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class PokemonDataStore {
    private val api = RetrofitInstance.apiService

    suspend fun fetchPokemons(): List<Pokemon> {
        return withContext(Dispatchers.IO) {
            api.getPokemons()
        }
    }

    suspend fun fetchPokemon(name: String): Pokemon {
        return withContext(Dispatchers.IO) {
            api.getPokemon(name)
        }
    }
}