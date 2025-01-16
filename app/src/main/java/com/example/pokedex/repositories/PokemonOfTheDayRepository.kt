package com.example.pokedex.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.dataClasses.Pokemon
import com.example.pokedex.dataClasses.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import org.threeten.bp.LocalDate

private val Context.dataStore by preferencesDataStore(name = "pofd_preferences")

class PokemonOfTheDayRepository(private val context: Context) {
    private val dataStore = DependencyContainer.pokemonDataStore
    private val connectivityRepository = DependencyContainer.connectivityRepository
    private val POFD_KEY = stringPreferencesKey("pokemon_of_the_day")
    private val gson = Gson()

    private var pokemonOfTheDay: Pokemon? = null
    val date = LocalDate.now().dayOfMonth
    var allPokemons = mutableListOf<Result>()
    var hasInternet = connectivityRepository.isConnected.asLiveData()

    private val mutablePokemonOfTheDayFlow = MutableSharedFlow<Pokemon?>()
    val pokemonOfTheDayFlow: Flow<Pokemon?> = mutablePokemonOfTheDayFlow.asSharedFlow()

    init {
        allPokemons.addAll(dataStore.getAllPokemonResults())
    }

    suspend fun getPokemonOfTheDayByName(){
        mutablePokemonOfTheDayFlow.emit(pokemonOfTheDay)
    }

    suspend fun determinePokemonOfTheDay(): Pokemon? {
        if (allPokemons.isEmpty()) {
            return null
        }

        val cachedPokemon = fetchPokemonOfTheDayFromDataStore()
        if (cachedPokemon != null && (cachedPokemon.name == allPokemons[date].name || !hasInternet.value!!)) {
            pokemonOfTheDay = cachedPokemon
        } else {
            pokemonOfTheDay = dataStore.getPokemonFromMapFallBackAPI(allPokemons[date].name)
            updateDataStore()
        }
        mutablePokemonOfTheDayFlow.emit(pokemonOfTheDay)
        return pokemonOfTheDay
    }

    private suspend fun updateDataStore() {
        if (pokemonOfTheDay?.name?.isBlank() == true) {
            return
        }
        val pokemonOfTheDay = gson.toJson(pokemonOfTheDay)
        context.dataStore.edit { preferences ->
            preferences[POFD_KEY] = pokemonOfTheDay
        }
    }

    private suspend fun fetchPokemonOfTheDayFromDataStore(): Pokemon? {
        val data = context.dataStore.data.first()
        val pokemonOfTheDayJson = data[POFD_KEY] ?: return null
        return gson.fromJson(pokemonOfTheDayJson, Pokemon::class.java)
    }
}