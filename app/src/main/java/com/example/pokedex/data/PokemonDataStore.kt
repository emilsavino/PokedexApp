package com.example.pokedex.data

import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonList
import com.example.pokedex.shared.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PokemonDataStore {
    private val api = RetrofitInstance.apiService
    private var allPokemonResultList = PokemonList(emptyList())
    private val pokemonMap : HashMap<String, Pokemon> = HashMap()

    init{
        CoroutineScope(Dispatchers.IO).launch {
            fetchAllPokemons()
        }
    }

    fun getAllPokemonResults() : List<Result>
    {
        while (allPokemonResultList.results.isEmpty()) {}
        return allPokemonResultList.results
    }

    suspend fun getPokemonFromMapFallBackAPIPlaygroundClassFeature(name: String) : Pokemon
    {
        val pokemon = pokemonMap[name]

        if (pokemon != null) {
            return pokemon
        }

        pokemonMap[name] = api.getPokemon(name.lowercase())
        return pokemonMap[name]!!
    }

    suspend fun fetchPokemon(name: String): Pokemon {
        return pokemonMap[name] ?: fetchPokemonFromAPI(name).also { pokemon ->
            pokemonMap[name] = pokemon
        }
    }

    private suspend fun fetchAllPokemons()
    {
        allPokemonResultList = fetchPokemons(10000,0)
    }

    suspend fun fetchPokemons(limit: Int, offset: Int): PokemonList {
        return withContext(Dispatchers.IO) {
            api.getPokemons(limit,offset)
        }
    }

    private suspend fun fetchPokemonFromAPI(name: String): Pokemon {
        val pokemon = api.getPokemon(name.lowercase())
        pokemon.name = name.replaceFirstChar { it.uppercase() }
        return pokemon
    }
}