package com.example.pokedex.mainViews.pokemonTriviaView

import PokemonTriviaRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.ViewModel
import com.example.pokedex.dataClasses.PokemonTriviaModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dataClasses.Option
import com.example.pokedex.dependencyContainer.DependencyContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PokemonTriviaViewModel : ViewModel() {
    private val repository: PokemonTriviaRepository = DependencyContainer.pokemonTriviaRepository

    private val _triviaState = MutableStateFlow<PokemonTriviaUIState>(PokemonTriviaUIState.Empty)
    val triviaState: StateFlow<PokemonTriviaUIState> = _triviaState.asStateFlow()

    var streakCount by mutableIntStateOf(0)
    var bestStreak by mutableIntStateOf(0)
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
    }

    fun loadRandomQuestion() = viewModelScope.launch {
        _triviaState.update { PokemonTriviaUIState.Loading }
        repository.loadRandomUnansweredQuestion()
        hasAnswered = false
    }

    fun handleAnswer(option: Option) {
        val currentState = _triviaState.value
        if (currentState is PokemonTriviaUIState.Question && !hasAnswered) {
            hasAnswered = true

            if (option.isCorrect) {
                streakCount++
            } else {
                if (streakCount > bestStreak){
                    bestStreak = streakCount
                }
                streakCount = 0
            }

            viewModelScope.launch {
                repository.markQuestionAsAnswered(currentState.trivia)
            }
        }
    }

    fun resetTrivia() = viewModelScope.launch {
        if (streakCount > bestStreak){
            bestStreak = streakCount
        }
        repository.resetQuestions()
        streakCount = 0
        hasAnswered = false
    }

    fun getOptionColor(option: Option) = if (hasAnswered) {
        if (option.isCorrect) Color.Green else Color.Red
    } else {
        Color.Gray
    }
}

sealed class PokemonTriviaUIState {
    object Loading : PokemonTriviaUIState()
    object Empty : PokemonTriviaUIState()
    data class Question(val trivia: PokemonTriviaModel) : PokemonTriviaUIState()
}