package com.example.pokedex.repositories

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PokemonRepository {
    private var dataStore = DependencyContainer.pokemonDataStore

    private val pokemonTeams = mutableListOf<List<Pokemon>>()

    private val mutablePokemonFlow = MutableSharedFlow<Pokemon>()
    val pokemonFlow: Flow<Pokemon> = mutablePokemonFlow.asSharedFlow()

    val filterOptions = mutableListOf<String>("fire","grass","ASAP-Rocky")
    val sortOptions = mutableListOf<String>("NameASC","NameDSC")

    private val mutableSearchFlow = MutableSharedFlow<List<Pokemon>>()
    val searchFlow: Flow<List<Pokemon>> = mutableSearchFlow.asSharedFlow()

    private val mutableTeamsFlow = MutableSharedFlow<List<List<Pokemon>>>()
    val teamsFlow: Flow<List<List<Pokemon>>> = mutableTeamsFlow.asSharedFlow()

    suspend fun fetchTeams() {
        mutableTeamsFlow.emit(pokemonTeams)
    }

    suspend fun searchPokemonByNameAndFilterWithSort(name : String, offset : Int, filterOptions : List<String>, sortOption : String)
    {
        var foundElements = 0
        val elementsToFind = 20
        var mutableFilteredList = mutableListOf<Pokemon>()
        var index = offset

        while (index < dataStore.getAllPokemonResults().size && foundElements < elementsToFind)
        {
            val result = dataStore.getAllPokemonResults().get(index)
            if (result.name.contains(name, ignoreCase = true))
            {
                var pokemon = dataStore.pokemonMap.get(result.name.capitalize(Locale("DK")))
                while (pokemon == null)
                {
                    pokemon = dataStore.pokemonMap.get(result.name.capitalize(Locale("DK")))
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
        var pokemon = dataStore.getPokemonFromMapFallBackAPIPlaygroundClassFeature(name)
        mutablePokemonFlow.emit(pokemon)
    }
}