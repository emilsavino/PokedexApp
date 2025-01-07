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

    private val mutablePokemonFlow = MutableSharedFlow<Pokemon>()
    val pokemonFlow: Flow<Pokemon> = mutablePokemonFlow.asSharedFlow()

    val filterOptions = mutableListOf<String>("fire","grass","ASAP-Rocky")
    val sortOptions = mutableListOf<String>("NameASC","NameDSC")

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
            pokemonMap[result.name.capitalize(Locale("DK"))] = dataStore.fetchPokemon(result.name)
        }
        val dean = 0
    }

    suspend fun fetchPokemons(limit: Int, offset: Int) {
        pokemonList = dataStore.fetchPokemons(limit,offset)
        mutablePokemonsFlow.emit(pokemonList)
    }

    suspend fun searchPokemonByName(name: String, offset: Int) {
        var foundElements = 0
        val elementsToFind = 20
        val mutableFilteredList = mutableListOf<Pokemon>()
        var index = offset

        while (index < allPokemonResultList.results.size && foundElements < elementsToFind)
        {
            val result = allPokemonResultList.results.get(index)
            if (result.name.contains(name, ignoreCase = true))
            {
                var pokemon = pokemonMap.get(result.name.capitalize(Locale("DK")))
                while (pokemon == null)
                {
                    pokemon = pokemonMap.get(result.name.capitalize(Locale("DK")))

                }
                mutableFilteredList.add(pokemon)
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
        var mutableFilteredList = mutableListOf<Pokemon>()
        var index = offset

        while (index < allPokemonResultList.results.size && foundElements < elementsToFind)
        {
            val result = allPokemonResultList.results.get(index)
            if (result.name.contains(name, ignoreCase = true))
            {
                var pokemon = pokemonMap.get(result.name.capitalize(Locale("DK")))
                while (pokemon == null)
                {
                    pokemon = pokemonMap.get(result.name.capitalize(Locale("DK")))
                }
                var typeRelevant = false
                for (type in filterOptions)
                {
                    for (innerType in pokemon.types)
                    {
                        if (type == innerType.type.name)
                        {
                            typeRelevant = true
                            break
                        }
                        if (typeRelevant)
                        {
                            break
                        }
                    }
                }
                if (filterOptions.isEmpty())
                {
                    typeRelevant = true
                }

                if (!typeRelevant)
                {
                    index++
                    continue;
                }

                mutableFilteredList.add(pokemon)
                foundElements++
            }
            index++
        }
        if (sortOption === "NameASC")
        {
            mutableFilteredList = mutableFilteredList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }).toMutableList()
        }
        else if (sortOption === "NameDSC")
        {
            mutableFilteredList = mutableFilteredList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }).toMutableList()
            mutableFilteredList.reverse()
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