package com.example.pokedex.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.DamageRelations
import com.example.pokedex.shared.EvolutionChain
import com.example.pokedex.shared.EvolutionChainResult
import com.example.pokedex.shared.EvolutionChainUrlFromSpecies
import com.example.pokedex.shared.FlavorTextAndEvolutionChain
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonList
import com.example.pokedex.shared.Result
import com.example.pokedex.shared.Species
import com.example.pokedex.shared.Type
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "all_pokemon")

class PokemonDataStore(private val context: Context) {
    private val connectivityRepository = DependencyContainer.connectivityRepository
    private val api = RetrofitInstance.apiService
    private var allPokemonResultList = PokemonList(emptyList())
    private val pokemonMap : HashMap<String, Pokemon> = HashMap()
    private var hasInternet = connectivityRepository.isConnected.asLiveData()
    private val ALL_POKEMONS_KEY = stringPreferencesKey("all_pokemons")
    private val gson = Gson()

    init{
        CoroutineScope(Dispatchers.IO).launch {
            initilizeCache()
        }
    }

    private suspend fun initilizeCache() {
        val cachedPokemons = fetchSavedPokemonsFromDataStore()
        if (cachedPokemons.results.isEmpty()) {
            fetchAllPokemons()
            fillUpMapFromAllPokemonResults()
            return
        }

        allPokemonResultList = cachedPokemons
        if (cachedPokemons.results.size < 1000) {
            fetchAllPokemons()
            fillUpMapFromAllPokemonResults()
        }
    }

    fun getAllPokemonResults() : List<Result>
    {
        while (allPokemonResultList.results.isEmpty()) {
            if (hasInternet.value == false) {
                return emptyList()
            }
        }
        return allPokemonResultList.results
    }

    private fun fillUpMapFromAllPokemonResults()
    {
        if (hasInternet.value == false) { return }
        val elementsPerBatch = 200
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

    suspend fun getPokemonFromMapFallBackAPI(name: String) : Pokemon
    {
        val pokemon = pokemonMap[name]

        if (pokemon != null) {
            return pokemon
        }

        if (hasInternet.value == false) {
            return Pokemon()
        }

        pokemonMap[name] = api.getPokemon(name.lowercase())
        return pokemonMap[name]!!
    }

    private suspend fun fetchAllPokemons()
    {
        if (hasInternet.value == false) { return }
        allPokemonResultList = fetchPokemons(10000,0)
        updateDataStore()
    }

    private suspend fun fetchPokemons(limit: Int, offset: Int): PokemonList {
        if (hasInternet.value == false) { return PokemonList(emptyList()) }
        return withContext(Dispatchers.IO) {
            api.getPokemons(limit,offset)
        }
    }

    suspend fun fetchPokemonSpecies(name: String): FlavorTextAndEvolutionChain {
        if (hasInternet.value == false) { return FlavorTextAndEvolutionChain(EvolutionChainUrlFromSpecies(""), emptyList()) }
        return withContext(Dispatchers.IO) {
            api.getPokemonSpecies(name)
        }
    }

    suspend fun fetchNameFromEvoChain(id: Int): EvolutionChain {
        if (hasInternet.value == false) { return EvolutionChain(EvolutionChainResult(Species(""), emptyList())) }
        return withContext(Dispatchers.IO) {
            api.getEvoChain(id)
        }
    }

    suspend fun fetchTypeInfo(types: List<Type>): List<DamageRelations> {
        if (hasInternet.value == false) { return emptyList() }
        return withContext(Dispatchers.IO) {
            types.map { type ->
                api.getTypeInfo(type.name)
            }
        }
    }

    private suspend fun updateDataStore() {
        val pokemonJson = gson.toJson(allPokemonResultList)
        context.dataStore.edit { preferences ->
            preferences[ALL_POKEMONS_KEY] = pokemonJson
        }
    }

    private suspend fun fetchSavedPokemonsFromDataStore(): PokemonList {
        val preferences = context.dataStore.data.first()
        val pokemonJson = preferences[ALL_POKEMONS_KEY] ?: "{}"
        return gson.fromJson(pokemonJson, PokemonList::class.java)
    }
}