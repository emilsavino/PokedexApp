package com.example.pokedex.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import com.example.pokedex.data.DatabaseService
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.dataClasses.Pokemon
import com.example.pokedex.dataClasses.SearchResult
import com.example.pokedex.dataClasses.PokemonTypeResources
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "recently_searched_preferences")

class RecentlySearchedRepository(private val context: Context) {
    private val databaseService = DatabaseService("recentlySearched", Pokemon::class.java)
    private val connectivityRepository = DependencyContainer.connectivityRepository
    private var hasInternet = connectivityRepository.isConnected.asLiveData()
    private var wasOffline = false
    private val recentlySearchedPokemon = mutableListOf<String>()
    private val pokemonDataStore = DependencyContainer.pokemonDataStore
    private val RECENTLY_SEARCHED_KEY = stringPreferencesKey("recently_searched")
    private val gson = Gson()


    val filterOptions = PokemonTypeResources().getAllTypes()
    val sortOptions = listOf("NameASC","NameDSC", "Evolutions")

    private val mutableSearchFlow = MutableSharedFlow<SearchResult>()
    val searchFlow: Flow<SearchResult> = mutableSearchFlow.asSharedFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initializeDataBase()
            if (recentlySearchedPokemon.isEmpty())
            {
                initializeCache()
            }
            fetchRecentlySearched()
        }

        hasInternet.observeForever { isConnected ->
            if (isConnected == true && wasOffline) {
                CoroutineScope(Dispatchers.IO).launch {
                    val list = mutableListOf<Pokemon>()
                    for (name in recentlySearchedPokemon) {
                        list.add(pokemonDataStore.getPokemonFromMapFallBackAPI(name))
                    }
                    databaseService.storeList(list)
                }
                wasOffline = false
            } else if (isConnected == false) {
                wasOffline = true
            }
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

    suspend fun fetchRecentlySearched(searchID : Int = -1) {
        val mutableList : MutableList<Pokemon> = mutableListOf()
        for (name in recentlySearchedPokemon)
        {
            val pokemon = pokemonDataStore.getPokemonFromMapFallBackAPI(name)
            mutableList.add(pokemon)
        }
        val result = SearchResult(searchID,mutableList.reversed())
        mutableSearchFlow.emit(result)
    }

    suspend fun addToRecentlySearched(name: String) {
        if (recentlySearchedPokemon.contains(name)) {
            recentlySearchedPokemon.remove(name)
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

    suspend fun searchPokemonByNameAndFilterWithSort(name : String, offset : Int, filterOptions : List<String>, sortOption : String, searchID : Int)
    {
        var foundElements = 0
        // Below is not optimal, but an easy way to guarantee the correct amount of pokemons :)
        // Also, searching is very fast as our pokemons should be in memory at this point, so not that bad.
        val elementsToFind = 20 + offset
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
        val result = SearchResult(searchID,mutableFilteredList)
        mutableSearchFlow.emit(result)
    }
}