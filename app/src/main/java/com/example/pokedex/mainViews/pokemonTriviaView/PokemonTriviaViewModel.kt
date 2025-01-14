package com.example.pokedex.mainViews.pokemonTriviaView

import PokemonTriviaRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.pokedex.shared.PokemonTriviaModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.pokedex.shared.Option
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.viewModelScope
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

    private val _streakCount = MutableStateFlow(0)
    val streakCount: StateFlow<Int> = _streakCount.asStateFlow()

    var hasAnswered by mutableStateOf(false)

    init {
        viewModelScope.launch {
            repository.triviaFlow.collect { question ->
                _triviaState.value = if (question != null) {
                    PokemonTriviaUIState.Question(question)
                } else {
                    PokemonTriviaUIState.Empty
                }
            }
        }
    }

    fun loadRandomQuestion() = viewModelScope.launch {
        repository.loadRandomUnansweredQuestion()
        hasAnswered = false
    }

    fun handleAnswer(option: Option) {
        val currentState = _triviaState.value
        if (currentState is PokemonTriviaUIState.Question && !hasAnswered) {
            hasAnswered = true

            if (option.isCorrect) {
                _streakCount.update { it + 1 }
            } else {
                _streakCount.update { 0 }
            }
            viewModelScope.launch {
                repository.markQuestionAsAnswered(currentState.trivia)
            }
        }
    }

    fun resetTrivia() = viewModelScope.launch {
        repository.resetQuestions()
        _streakCount.update { 0 }
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