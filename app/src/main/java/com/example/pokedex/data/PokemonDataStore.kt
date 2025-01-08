package com.example.pokedex.data

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
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
    val pokemonMap : HashMap<String, Pokemon> = HashMap()

    init{
        CoroutineScope(Dispatchers.IO).launch {
            fetchAllPokemons()
            fillUpHashMapOfPokemons()
        }
    }

    fun fetchPokemon(name : String): Pokemon?
    {
        return pokemonMap[name.capitalize(Locale("DK"))]
    }

    fun getAllPokemonResults() : List<Result>
    {
        while (allPokemonResultList.results.isEmpty()) {}
        return allPokemonResultList.results
    }

    suspend fun getPokemonFromMapFallBackAPIPlaygroundClassFeature(name: String) : Pokemon
    {
        var pokemon = pokemonMap[name]

        if (pokemon != null) {
            return pokemon
        }

        pokemonMap[name] = api.getPokemon(name)
        return pokemonMap[name]!!
    }

    private suspend fun fetchAllPokemons()
    {
        allPokemonResultList = fetchPokemons(10000,0)
        fillUpHashMapOfPokemons()
    }

    private suspend fun fillUpHashMapOfPokemons()
    {
        for (result in allPokemonResultList.results)
        {
            pokemonMap[result.name.capitalize(Locale("DK"))] = fetchPokemonFromAPI(result.name)
        }
    }

    private suspend fun fetchPokemons(limit: Int, offset: Int): PokemonList {
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