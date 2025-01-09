package com.example.pokedex.data

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonList
import com.example.pokedex.shared.PokemonSpecies
import com.example.pokedex.shared.Result
import com.example.pokedex.shared.Type
import com.example.pokedex.shared.TypeObject
import com.example.pokedex.shared.Weakness
import com.example.pokedex.shared.Weaknesses
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

    suspend fun fetchPokemonWeaknesses(types: List<TypeObject>) : Weaknesses {
        var weaknessList = mutableListOf<Weakness>()

        for (type in types) {

            val response = api.getWeakness(type.type.name)
            for (weakness in response.double_damage_from) {
                weaknessList.add(weakness)
            }
        }
        return Weaknesses(double_damage_from = weaknessList)
    }

    suspend fun fetchPokemonDescription(name: String) : PokemonSpecies {
        return withContext(Dispatchers.IO) {
            api.getPokemonDesc(name)
        }
    }

    suspend fun fetchPokemonType(name: String): List<TypeObject> {
        return withContext(Dispatchers.IO) {
            val pokemon = api.getPokemon(name)
            pokemon.types.map { typeEntry ->
                TypeObject(type = Type(name = typeEntry.type.name))
            }
        }
    }

    suspend fun getPokemonFromMapFallBackAPIPlaygroundClassFeature(name: String) : Pokemon
    {
        var pokemon = pokemonMap[name]

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

    private suspend fun fetchPokemonFromAPI(name: String): Pokemon {
        val pokemon = api.getPokemon(name.lowercase())
        pokemon.name = name.replaceFirstChar { it.uppercase() }
        return pokemon
    }
}