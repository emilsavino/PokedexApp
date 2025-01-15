package com.example.pokedex.repositories

import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.threeten.bp.LocalDate

class PokemonOfTheDayRepository {

    private val dataStore = DependencyContainer.pokemonDataStore

    private val mutablePokemonOfTheDayFlow = MutableSharedFlow<Pokemon?>()
    val pokemonOfTheDayFlow: Flow<Pokemon?> = mutablePokemonOfTheDayFlow.asSharedFlow()

    suspend fun getPokemonOfTheDayByName(){
        mutablePokemonOfTheDayFlow.emit(determinePokemonOfTheDay())
    }

    suspend fun determinePokemonOfTheDay(): Pokemon? {
        val pokemons = dataStore.getAllPokemonResults()
        if (pokemons.isEmpty()) {
            return null
        }
        val date = LocalDate.now().dayOfMonth
        return dataStore.getPokemonFromMapFallBackAPI(pokemons[date].name)
    }
}