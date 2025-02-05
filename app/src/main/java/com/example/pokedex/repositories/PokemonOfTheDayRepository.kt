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
import kotlin.random.Random

private val Context.dataStore by preferencesDataStore(name = "pofd_preferences")

class PokemonOfTheDayRepository(private val context: Context) {
    private val dataStore = DependencyContainer.pokemonDataStore
    private val connectivityRepository = DependencyContainer.connectivityRepository
    private val POFD_KEY = stringPreferencesKey("pokemon_of_the_day")
    private val gson = Gson()

    private var pokemonOfTheDay: Pokemon? = null
    var allPokemons = mutableListOf<Result>()

    private val mutablePokemonOfTheDayFlow = MutableSharedFlow<Pokemon?>()
    val pokemonOfTheDayFlow: Flow<Pokemon?> = mutablePokemonOfTheDayFlow.asSharedFlow()

    suspend fun determinePokemonOfTheDay() {
        allPokemons.addAll(dataStore.getAllPokemonResults())

        if (allPokemons.isEmpty()) {
            return
        }

        val currentDate = LocalDate.now().toString()
        val seed = currentDate.hashCode()
        val randomGen = Random(seed)

        pokemonOfTheDay = dataStore.getPokemonFromMapFallBackAPI(allPokemons[randomGen.nextInt(allPokemons.size)].name)

        if (pokemonOfTheDay!!.name.isBlank()) {
            pokemonOfTheDay = fetchPokemonOfTheDayFromDataStore()
        } else {
            updateDataStore()
        }

        mutablePokemonOfTheDayFlow.emit(pokemonOfTheDay)
    }

    fun getPokemonOfTheDay(): Pokemon? {
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