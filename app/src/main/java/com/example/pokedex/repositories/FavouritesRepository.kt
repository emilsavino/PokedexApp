package com.example.pokedex.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import com.example.pokedex.data.DatabaseService
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.dataClasses.Pokemon
import com.example.pokedex.dataClasses.PokemonTypeResources
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "pokemon_preferences")

class FavouritesRepository(private val context: Context) {
    private val databaseService = DatabaseService("favorites", Pokemon::class.java)
    private val connectivityRepository = DependencyContainer.connectivityRepository
    private val pokemonDataStore = DependencyContainer.pokemonDataStore
    private var hasInternet = connectivityRepository.isConnected.asLiveData()
    private var wasOffline = false
    private val favouritePokemons = mutableListOf<Pokemon>()
    private val FAVOURITE_POKEMONS_KEY = stringPreferencesKey("favourite_pokemons")
    private val gson = Gson()

    val filterOptions = PokemonTypeResources().getAllTypes()
    val sortOptions = listOf(
        "NameASC",
        "NameDSC",
        "Evolutions",
        "HPASC",
        "HPDSC",
        "SpeedASC",
        "SpeedDSC",
        "AttackASC",
        "AttackDSC",
        "DefenseASC",
        "DefenseDSC"
    )

    private val mutableSavedPokemonsFlow = MutableSharedFlow<List<Pokemon>>()
    val savedPokemonsFlow: Flow<List<Pokemon>> = mutableSavedPokemonsFlow.asSharedFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initializeDatabase()
            if (favouritePokemons.isEmpty()) {
                initializeCache()
            }
            mutableSavedPokemonsFlow.emit(favouritePokemons)
        }

        hasInternet.observeForever { isConnected ->
            if (isConnected == true && wasOffline) {
                CoroutineScope(Dispatchers.IO).launch {
                    databaseService.storeList(favouritePokemons)
                }
                wasOffline = false
            } else if (isConnected == false) {
                wasOffline = true
            }
        }
    }

    private suspend fun initializeDatabase() {
        databaseService.addListenerForList { favorites ->
            if (favorites != null) {
                favouritePokemons.clear()
                favouritePokemons.addAll(favorites)
            }
        }
    }

    suspend fun initializeCache() {
        val savedPokemons = fetchSavedPokemonsFromDataStore()
        favouritePokemons.clear()
        favouritePokemons.addAll(savedPokemons)
    }

    suspend fun fetchSaved() {
        mutableSavedPokemonsFlow.emit(favouritePokemons)
    }

    suspend fun makeFavourite(pokemon: Pokemon) {
        if (!favouritePokemons.contains(pokemon)) {
            favouritePokemons.add(pokemon)
            databaseService.storeList(favouritePokemons)
            updateDataStore()
        }
    }

    suspend fun removeFromFavourites(pokemon: Pokemon) {
        if (favouritePokemons.contains(pokemon)) {
            favouritePokemons.remove(pokemon)
            databaseService.storeList(favouritePokemons)
            updateDataStore()
        }
        mutableSavedPokemonsFlow.emit(favouritePokemons)
    }

    fun pokemonIsFavourite(pokemon: Pokemon): Boolean {
        return favouritePokemons.contains(pokemon)
    }

    private suspend fun updateDataStore() {
        val pokemonJson = gson.toJson(favouritePokemons)
        context.dataStore.edit { preferences ->
            preferences[FAVOURITE_POKEMONS_KEY] = pokemonJson
        }
    }

    private suspend fun fetchSavedPokemonsFromDataStore(): List<Pokemon> {
        val preferences = context.dataStore.data.first()
        val pokemonJson = preferences[FAVOURITE_POKEMONS_KEY] ?: "[]"
        return gson.fromJson(pokemonJson, Array<Pokemon>::class.java).toList()
    }

    fun getCachedPokemon(name: String): Pokemon? {
        return favouritePokemons.find { it.name == name }
    }

    suspend fun savedPokemonByNameAndFilterWithSort(
        filterOptions: List<String>,
        sortOption: String,
    ) {
        var mutableFilteredList: MutableList<Pokemon>

        val allSavedPokemons = favouritePokemons

        mutableFilteredList = allSavedPokemons.filter { pokemon ->
            if (filterOptions.isEmpty()) {
                return@filter true
            }

            pokemon.types.any { type ->
                filterOptions.contains(type.type.name)
            }
        }.toMutableList()

        when {
            sortOption == "NameASC" -> mutableFilteredList.sortBy { it.name }
            sortOption == "NameDSC" -> mutableFilteredList.sortByDescending { it.name }
            sortOption == "Evolutions" ->
                {
                    val list = mutableListOf<Pokemon>()
                    for (pokemon in pokemonDataStore.getAllPokemonResults())
                    {
                        for (innerPokemon in mutableFilteredList)
                        {
                            if (pokemon.name == innerPokemon.name)
                            {
                                list.add(innerPokemon)
                                break
                            }
                        }
                    }
                    mutableFilteredList = list
                }




            sortOption.contains("ASC") -> {
                val statToSort = sortOption.removeSuffix("ASC").lowercase()
                mutableFilteredList.sortWith(compareBy { pokemon ->
                    pokemon.stats.firstOrNull { it.stat.name.lowercase() == statToSort }?.base_stat ?: Int.MAX_VALUE
                })
            }
            sortOption.contains("DSC") -> {
                val statToSort = sortOption.removeSuffix("DSC").lowercase()
                mutableFilteredList.sortWith(compareByDescending { pokemon ->
                    pokemon.stats.firstOrNull { it.stat.name.lowercase() == statToSort }?.base_stat ?: Int.MIN_VALUE
                })
            }
        }
        mutableSavedPokemonsFlow.emit(mutableFilteredList)
    }

    suspend fun clearAllCache() {
        favouritePokemons.clear()
        updateDataStore()
        mutableSavedPokemonsFlow.emit(favouritePokemons)
    }
}
