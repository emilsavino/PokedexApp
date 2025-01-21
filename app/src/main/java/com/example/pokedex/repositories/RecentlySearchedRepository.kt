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
import com.example.pokedex.dataClasses.Result
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

    private var sortingMap : HashMap<String, HashMap<String, List<Result>>> = HashMap()

    val filterOptions = PokemonTypeResources().getAllTypes()
    val sortOptions = listOf("NameASC","NameDSC", "Evolutions","HPASC","HPDSC","SpeedASC","SpeedDSC","AttackASC","AttackDSC","DefenseASC","DefenseDSC")

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

        if (recentlySearchedPokemon.size >= 20) {
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

    suspend fun searchPokemonByNameAndFilterWithSort(name : String, offset : Int, filterOptions : List<String>, sortOption : String, searchID : Int)
    {
        var localSortOption : String = sortOption
        var foundElements = 0
        // Below is not optimal, but an easy way to guarantee the correct amount of pokemons :)
        // Also, searching is very fast as our pokemons should be in memory at this point, so not that bad.
        val elementsToFind = 20 + offset
        var mutableFilteredList = mutableListOf<Pokemon>()
        var index = 0
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
        else if (sortOption == "Evolutions" || sortOption == "")
        {
        }
        else
        {

            var newList = mutableListOf<Result>()

            if (sortingMap.containsKey(filterOptions.toString()) && sortingMap[filterOptions.toString()]!!.containsKey(sortOption))
            {
                newList = sortingMap[filterOptions.toString()]!![sortOption]!!.toMutableList()
            }

            if (sortOption.contains("DSC"))
            {
                localSortOption = sortOption.removeSuffix("DSC")


                for (pokemon in allPokemonResults)
                {
                    var highestValueFound = Int.MIN_VALUE
                    var bestPokemonSoFar : Result = Result("","")
                    for (innerPokemon in allPokemonResults)
                    {
                        var toContinue : Boolean = false
                        for (item in newList)
                        {
                            if (item.name == innerPokemon.name)
                            {
                                toContinue = true
                            }
                        }
                        if (pokemon == innerPokemon || toContinue)
                        {
                            continue
                        }
                        val operationPokemon = pokemonDataStore.getPokemonFromMapFallBackAPI(innerPokemon.name)
                        if (!pokemonIsTypeRelevant(operationPokemon,filterOptions))
                        {
                            continue
                        }
                        for (stat in operationPokemon.stats)
                        {
                            if (stat.stat.name == localSortOption.lowercase())
                            {
                                if (stat.base_stat > highestValueFound)
                                {
                                    highestValueFound = stat.base_stat
                                    bestPokemonSoFar = Result(innerPokemon.name,innerPokemon.url)
                                    break
                                }
                            }
                        }
                    }
                    newList.add(bestPokemonSoFar)
                    if (newList.size >= elementsToFind)
                    {
                        break
                    }
                }
                allPokemonResults = newList.toList().subList(0,elementsToFind)
            }
            else if (sortOption.contains("ASC"))
            {
                localSortOption = sortOption.removeSuffix("ASC")

                for (pokemon in allPokemonResults)
                {
                    var highestValueFound = Int.MAX_VALUE
                    var bestPokemonSoFar : Result = Result("","")
                    for (innerPokemon in allPokemonResults)
                    {
                        var toContinue : Boolean = false
                        for (item in newList)
                        {
                            if (item.name == innerPokemon.name)
                            {
                                toContinue = true
                            }
                        }
                        if (pokemon == innerPokemon || toContinue)
                        {
                            continue
                        }
                        val operationPokemon = pokemonDataStore.getPokemonFromMapFallBackAPI(innerPokemon.name)
                        if (!pokemonIsTypeRelevant(operationPokemon,filterOptions))
                        {
                            continue
                        }
                        for (stat in operationPokemon.stats)
                        {
                            if (stat.stat.name == localSortOption.lowercase())
                            {
                                if (stat.base_stat < highestValueFound)
                                {
                                    highestValueFound = stat.base_stat
                                    bestPokemonSoFar = Result(innerPokemon.name,innerPokemon.url)
                                    break
                                }
                            }
                        }
                    }
                    newList.add(bestPokemonSoFar)
                    if (newList.size >= elementsToFind)
                    {
                        break
                    }
                }
                allPokemonResults = newList.toList()
            }
            if (!sortingMap.containsKey(filterOptions.toString()))
            {
                sortingMap.put(filterOptions.toString(),HashMap())
            }
            sortingMap[filterOptions.toString()]!!.put(sortOption,allPokemonResults)
        }


        while (index < allPokemonResults.size && foundElements < elementsToFind)
        {
            val result = allPokemonResults.get(index)
            if (result.name.contains(name, ignoreCase = true))
            {
                var pokemon = pokemonDataStore.getPokemonFromMapFallBackAPI(result.name)
                val typeRelevant = pokemonIsTypeRelevant(pokemon,filterOptions)

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

    private fun pokemonIsTypeRelevant(pokemon : Pokemon, filterOptions : List<String>) : Boolean {
        for (type in filterOptions)
        {
            for (innerType in pokemon.types)
            {
                if (type == innerType.type.name)
                {
                    return true
                }
            }
        }
        if (filterOptions.isEmpty())
        {
            return true
        }
        return false
    }

    suspend fun searchShuffledPokemons(types: List<String>) {
        var allPokemons = pokemonDataStore.getAllPokemonResults()
        allPokemons = allPokemons.shuffled()

        var shuffledListToReturn = mutableListOf<Pokemon>()
        for (pokemonResult in allPokemons) {
            val pokemon = pokemonDataStore.getPokemonFromMapFallBackAPI(pokemonResult.name)
            if (pokemonIsTypeRelevant(pokemon, types)) {
                shuffledListToReturn.add(pokemon)
            }
        }

        shuffledListToReturn.take(20)
        val searchResult = SearchResult(
            indexOfSearch = -1,
            pokemons = shuffledListToReturn
        )
        mutableSearchFlow.emit(searchResult)
    }
}