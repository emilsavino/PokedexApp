package com.example.pokedex.repositories

import com.example.pokedex.data.PokemonDataStore
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonList
import com.example.pokedex.shared.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PokemonRepository {
    private var dataStore = PokemonDataStore()

    private val pokemonTeams = mutableListOf<List<Pokemon>>()

    private val mutablePokemonFlow = MutableSharedFlow<Pokemon>()
    val pokemonFlow: Flow<Pokemon> = mutablePokemonFlow.asSharedFlow()

    val filterOptions = mutableListOf<String>("Fire","Ground","ASAP-Rocky")
    val sortOptions = mutableListOf<String>("Name","Primary")

    private var allPokemonResultList = PokemonList(emptyList())
    private var allPokemonList = listOf<Pokemon>()
    private var pokemonList = PokemonList(emptyList<Result>())
    private val mutablePokemonsFlow = MutableSharedFlow<PokemonList>()
    val pokemonsFlow: Flow<PokemonList> = mutablePokemonsFlow.asSharedFlow()

    private val mutableSearchFlow = MutableSharedFlow<List<Result>>()
    val searchFlow: Flow<List<Result>> = mutableSearchFlow.asSharedFlow()

    private val mutableTeamsFlow = MutableSharedFlow<List<List<Pokemon>>>()
    val teamsFlow: Flow<List<List<Pokemon>>> = mutableTeamsFlow.asSharedFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            fetchAllPokemons()
        }
    }

    private suspend fun fetchAllPokemons()
    {
        allPokemonResultList = dataStore.fetchPokemons(10000,0)
        val localAllPokemonList = mutableListOf<Pokemon>()
        for (result in allPokemonResultList.results)
        {
            val pokemon = dataStore.fetchPokemon(result.name)
            localAllPokemonList.add(pokemon)
        }
        allPokemonList = localAllPokemonList
    }

    suspend fun fetchPokemons(limit: Int, offset: Int) {
        pokemonList = dataStore.fetchPokemons(limit,offset)
        mutablePokemonsFlow.emit(pokemonList)
    }

    suspend fun fetchTeams() {
        mutableTeamsFlow.emit(pokemonTeams)
    }

    suspend fun searchPokemonByName(name: String, offset: Int) {
        var foundElements = 0
        val elementsToFind = 20
        val mutableFilteredList = mutableListOf<Result>()
        var index = offset

        while (index < allPokemonResultList.results.size && foundElements < elementsToFind)
        {
            val result = allPokemonResultList.results.get(index)
            if (result.name.contains(name, ignoreCase = true))
            {
                mutableFilteredList.add(result)
                foundElements++
            }
            index++
        }
        mutableSearchFlow.emit(mutableFilteredList)
    }

    suspend fun getPokemonByName(name: String) {
        mutablePokemonFlow.emit(dataStore.fetchPokemon(name))
    }
}