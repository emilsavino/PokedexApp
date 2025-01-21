package com.example.pokedex.mainViews.pokemonTriviaView

import PokemonTriviaRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.pokedex.dataClasses.PokemonTriviaModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dataClasses.Option
import com.example.pokedex.dataClasses.PokemonTypeResources
import com.example.pokedex.dependencyContainer.DependencyContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class PokemonTriviaViewModel : ViewModel() {
    private val repository: PokemonTriviaRepository = DependencyContainer.pokemonTriviaRepository
    private val dataStore = DependencyContainer.pokemonDataStore

    private val _triviaState = MutableStateFlow<PokemonTriviaUIState>(PokemonTriviaUIState.Empty)
    val triviaState: StateFlow<PokemonTriviaUIState> = _triviaState.asStateFlow()
    var background by mutableStateOf(PokemonTypeResources().appGradient())

    var streakCount by mutableIntStateOf(0)
    var hasAnswered by mutableStateOf(false)

    init {
        viewModelScope.launch {
            repository.triviaFlow.collect { question ->
                _triviaState.update {
                    if (question == null) {
                        PokemonTriviaUIState.Empty
                    } else {
                        PokemonTriviaUIState.Question(question)
                    }
                }
            }
        }
        loadRandomQuestion()
        getBackground()
    }

    fun loadRandomQuestion() = viewModelScope.launch {
        _triviaState.update { PokemonTriviaUIState.Loading }
        repository.loadRandomUnansweredQuestion()
        hasAnswered = false
        getBackground()
    }

    fun handleAnswer(option: Option) {
        val currentState = _triviaState.value
        if (currentState is PokemonTriviaUIState.Question && !hasAnswered) {
            hasAnswered = true

            if (option.isCorrect) {
                streakCount++
            } else {
                streakCount = 0
            }
            viewModelScope.launch {
                repository.markQuestionAsAnswered(currentState.trivia)
            }
        }
    }

    fun resetTrivia() = viewModelScope.launch {
        repository.resetQuestions()
        streakCount = 0
        hasAnswered = false
    }

    fun getOptionColor(option: Option) = if (hasAnswered) {
        if (option.isCorrect) Color.Green else Color.Red
    } else {
        Color.White
    }

    private fun getBackground() = viewModelScope.launch {
        if (triviaState.value is PokemonTriviaUIState.Question) {
            (triviaState.value as PokemonTriviaUIState.Question).trivia.options.forEach { option ->
                if (option.isCorrect) {
                    runBlocking {
                        val pokemon = dataStore.getPokemonFromMapFallBackAPI(option.name)
                        background = PokemonTypeResources().getTypeGradient(pokemon.types[0].type.name)
                    }
                }
            }
        }
    }
}

sealed class PokemonTriviaUIState {
    object Loading : PokemonTriviaUIState()
    object Empty : PokemonTriviaUIState()
    data class Question(val trivia: PokemonTriviaModel) : PokemonTriviaUIState()
}