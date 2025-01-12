package com.example.pokedex.data

import com.example.pokedex.shared.DamageRelations
import com.example.pokedex.shared.EvolutionChain
import com.example.pokedex.shared.EvolutionChainResult
import com.example.pokedex.shared.FlavorTextAndEvolutionChain
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonList
import com.example.pokedex.shared.Result
import com.example.pokedex.shared.Species
import com.example.pokedex.shared.Type
import com.example.pokedex.shared.Types
import com.example.pokedex.shared.Varieties
import com.example.pokedex.shared.VarietiesPokemon
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

    private suspend fun fetchAllPokemons()
    {
        allPokemonResultList = fetchPokemons(10000,0)
    }

    suspend fun fetchPokemons(limit: Int, offset: Int): PokemonList {
        return withContext(Dispatchers.IO) {
            api.getPokemons(limit,offset)
        }
    }

    suspend fun fetchPokemonSpecies(name: String): FlavorTextAndEvolutionChain {
        return withContext(Dispatchers.IO) {
            api.getPokemonSpecies(name)
        }
    }

    suspend fun fetchNameFromEvoChain(id: Int): EvolutionChain {
        return withContext(Dispatchers.IO) {
            api.getEvoChain(id)
        }
    }

    suspend fun fetchTypeInfo(types: List<Type>): List<DamageRelations> {
        return withContext(Dispatchers.IO) {
            types.map { type ->
                api.getTypeInfo(type.name)
            }
        }
    }
}