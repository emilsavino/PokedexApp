package com.example.pokedex.repositories

import com.example.pokedex.R
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Option
import com.example.pokedex.shared.WhoIsThatPokemon
import com.example.pokedex.shared.getSprite
import kotlinx.coroutines.flow.MutableSharedFlow

class WhoIsThatPokemonRepository {
    val dataStore = DependencyContainer.pokemonDataStore

    private val mutableWhoIsThatPokemonSharedFlow = MutableSharedFlow<WhoIsThatPokemon>()
    val whoIsThatPokemonSharedFlow = mutableWhoIsThatPokemonSharedFlow


    suspend fun getWhoIsThatPokemon() {
        mutableWhoIsThatPokemonSharedFlow.emit(determineOptions())
    }

    private suspend fun determineOptions() : WhoIsThatPokemon {
        val potentialAnswers = dataStore.getAllPokemonResults()
        var correctAnswerName = potentialAnswers.random().name
        var correctAnswer = dataStore.getPokemonFromMapFallBackAPI(correctAnswerName)
        while (correctAnswer.getSprite() == R.drawable.unknown) {
            correctAnswerName = potentialAnswers.random().name
            correctAnswer = dataStore.getPokemonFromMapFallBackAPI(correctAnswerName)
        }
        val options = mutableListOf<Option>()
        options.add(Option(correctAnswerName, true))
        while (options.size < 4) {
            val randomPokemon = potentialAnswers.random()
            if (randomPokemon.name != correctAnswerName) {
                options.add(Option(randomPokemon.name, false))
            }
        }
        options.shuffle()
        return WhoIsThatPokemon(correctAnswer, options)
    }
}