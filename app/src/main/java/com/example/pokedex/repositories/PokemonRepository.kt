package com.example.pokedex.repositories

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.example.pokedex.data.PokemonDataStore
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonList
import com.example.pokedex.shared.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

    val pokemonMap : HashMap<String, Pokemon> = HashMap()

    private val mutableSearchFlow = MutableSharedFlow<List<Pokemon>>()
    val searchFlow: Flow<List<Pokemon>> = mutableSearchFlow.asSharedFlow()

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
        fillUpHashMapOfPokemons()
    }

    private suspend fun fillUpHashMapOfPokemons()
    {
        for (result in allPokemonResultList.results)
        {
            CoroutineScope(Dispatchers.IO).async {
                pokemonMap[result.name.capitalize(Locale("DK"))] = dataStore.fetchPokemon(result.name)
            }
        }
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
        val mutableFilteredList = mutableListOf<Pokemon>()
        var index = offset

        while (index < allPokemonList.size && foundElements < elementsToFind)
        {
            val result = allPokemonList.get(index)
            if (result.name.contains(name, ignoreCase = true))
            {
                mutableFilteredList.add(result)
                foundElements++
            }
            index++
        }
        mutableSearchFlow.emit(mutableFilteredList)
    }

    suspend fun searchPokemonByNameAndFilterWithSort(name : String, offset : Int, filterOptions : List<String>, sortOption : String)
    {
        var foundElements = 0
        val elementsToFind = 20
        val mutableFilteredList = mutableListOf<Pokemon>()
        var index = offset

        while (index < allPokemonList.size && foundElements < elementsToFind)
        {
            val result = allPokemonList.get(index)
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
        var pokemon = pokemonMap.get(name)
        while (pokemon == null)
        {
            pokemon = pokemonMap.get(name)
        }
        mutablePokemonFlow.emit(pokemon)
    }
}