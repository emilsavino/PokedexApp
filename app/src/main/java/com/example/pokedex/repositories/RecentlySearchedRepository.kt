package com.example.pokedex.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.pokedex.data.DatabaseService
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonTypeResources
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "recently_searched_preferences")

class RecentlySearchedRepository(private val context: Context) {
    private val databaseService = DatabaseService("recentlySearched", Pokemon::class.java)
    private val recentlySearchedPokemon = mutableListOf<String>()
    private val pokemonDataStore = DependencyContainer.pokemonDataStore
    private val RECENTLY_SEARCHED_KEY = stringPreferencesKey("recently_searched")
    private val gson = Gson()


    val filterOptions = PokemonTypeResources().getAllTypes()
    val sortOptions = listOf("NameASC","NameDSC")

    private val mutableSearchFlow = MutableSharedFlow<List<Pokemon>>()
    val searchFlow: Flow<List<Pokemon>> = mutableSearchFlow.asSharedFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initializeDataBase()
            if (recentlySearchedPokemon.isEmpty())
            {
                initializeCache()
            }
            fetchRecentlySearched()
        }
    }

    private suspend fun initializeDataBase() {
        databaseService.addListenerForList { recentlySearched ->
            if (recentlySearched != null) {
                recentlySearchedPokemon.clear()
                for (pokemon in recentlySearched)
                {
                    recentlySearchedPokemon.add(pokemon.name)
                }
            }
        }
    }

    private suspend fun initializeCache() {
        val recentlySearched = fetchRecentsFromDataStore()
        recentlySearchedPokemon.clear()
        recentlySearchedPokemon.addAll(recentlySearched.toList())
        fetchRecentlySearched()
    }

    suspend fun fetchRecentlySearched() {
        val mutableList : MutableList<Pokemon> = mutableListOf()
        for (name in recentlySearchedPokemon)
        {
            val pokemon = pokemonDataStore.getPokemonFromMapFallBackAPI(name)
            mutableList.add(pokemon)
        }
        mutableSearchFlow.emit(mutableList.toList().reversed())
    }

    suspend fun addToRecentlySearched(name: String) {
        if (recentlySearchedPokemon.contains(name)) {
            recentlySearchedPokemon.remove(name)
        }

        if (recentlySearchedPokemon.size >= 10) {
            recentlySearchedPokemon.removeAt(0)
        }

        recentlySearchedPokemon.add(name)
        val list = mutableListOf<Pokemon>()
        for (name in recentlySearchedPokemon)
        {
            list.add(pokemonDataStore.getPokemonFromMapFallBackAPI(name))
        }
        databaseService.storeList(list)
        updateDataStore()
    }

    private suspend fun updateDataStore() {
        val pokemonJson = gson.toJson(recentlySearchedPokemon)
        context.dataStore.edit { preferences ->
            preferences[RECENTLY_SEARCHED_KEY] = pokemonJson
        }
    }

    private suspend fun fetchRecentsFromDataStore(): List<String> {
        val preferences = context.dataStore.data.first()
        val pokemonJson = preferences[RECENTLY_SEARCHED_KEY] ?: "[]"
        return gson.fromJson(pokemonJson, Array<String>::class.java).toList()
    }

    suspend fun searchPokemonByNameAndFilterWithSort(name : String, offset : Int, filterOptions : List<String>, sortOption : String)
    {
        var foundElements = 0
        val elementsToFind = 20
        var mutableFilteredList = mutableListOf<Pokemon>()
        var index = offset
        var allPokemonResults = DependencyContainer.pokemonDataStore.getAllPokemonResults()
        if (sortOption == "NameASC")
        {
            allPokemonResults = allPokemonResults.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }).toMutableList()
        }
        else if (sortOption == "NameDSC")
        {
            allPokemonResults = allPokemonResults.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }).toMutableList()
            allPokemonResults = allPokemonResults.reversed()
        }

        while (index < allPokemonResults.size && foundElements < elementsToFind)
        {
            val result = allPokemonResults.get(index)
            if (result.name.contains(name, ignoreCase = true))
            {
                var pokemon = pokemonDataStore.getPokemonFromMapFallBackAPI(result.name)
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
        mutableSearchFlow.emit(mutableFilteredList)
    }

}