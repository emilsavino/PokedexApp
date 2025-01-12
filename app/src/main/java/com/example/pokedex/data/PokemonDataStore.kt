package com.example.pokedex.data

import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonList
import com.example.pokedex.shared.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PokemonDataStore {
    private val api = RetrofitInstance.apiService
    private var allPokemonResultList = PokemonList(emptyList())
    private val pokemonMap : HashMap<String, Pokemon> = HashMap()

    init{
        CoroutineScope(Dispatchers.IO).launch {
            fetchAllPokemons()
            fillUpMapFromAllPokemonResults()
        }
    }

    fun getAllPokemonResults() : List<Result>
    {
        while (allPokemonResultList.results.isEmpty()) {}
        return allPokemonResultList.results
    }

    suspend private fun fillUpMapFromAllPokemonResults()
    {
        val elementsPerBatch = 650
        val pokemonNameMatrix : MutableList<List<String>> = mutableListOf()
        var counter = 0
        var pokemonNameRow = mutableListOf<String>()
        while (counter < allPokemonResultList.results.size)
        {
            if (counter % elementsPerBatch == 0 && counter != 0 || counter == allPokemonResultList.results.size - 1)
            {
                pokemonNameMatrix.add(pokemonNameRow.toList())
                pokemonNameRow = mutableListOf()
                pokemonNameRow.clear()
            }
            pokemonNameRow.add(allPokemonResultList.results.get(counter).name)
            counter++
        }

        for (i in 0 until pokemonNameMatrix.size)
        {
            CoroutineScope(Dispatchers.IO).async{
                for (name in pokemonNameMatrix[i])
                {
                    if (pokemonMap[name] == null)
                    {
                        pokemonMap[name] = api.getPokemon(name)
                    }
                }
                println("DONE FETCHING BATCH $i")
            }
        }
        println("DONE STARTING ALL BATCH JOBS FOR FETCHING POKEMONS")
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

    private suspend fun fetchAllPokemons()
    {
        allPokemonResultList = fetchPokemons(10000,0)
    }

    private suspend fun fetchPokemons(limit: Int, offset: Int): PokemonList {
        return withContext(Dispatchers.IO) {
            api.getPokemons(limit,offset)
        }
    }
}