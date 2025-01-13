package com.example.pokedex.mainViews.pokemonTriviaView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.pokedex.shared.PokemonTriviaAnswer
import com.example.pokedex.shared.PokemonTriviaModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.pokedex.repositories.PokemonTriviaRepository
import com.example.pokedex.shared.Option
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PokemonTriviaViewModel(private val repository: PokemonTriviaRepository) : ViewModel() {

    private val _triviaState = MutableStateFlow<PokemonTriviaUIState>(PokemonTriviaUIState.Loading)
    val triviaState: StateFlow<PokemonTriviaUIState> = _triviaState.asStateFlow()

    var hasAnswered by mutableStateOf(false)
        private set

    init {
        loadRandomQuestion()
    }

    fun loadRandomQuestion() = viewModelScope.launch {
        _triviaState.update { PokemonTriviaUIState.Loading }

        val question = repository.getRandomUnansweredQuestion()
        if (question != null) {
            hasAnswered = false
            _triviaState.update { PokemonTriviaUIState.Question(question) }
        } else {
            _triviaState.update { PokemonTriviaUIState.Empty }
        }
    }

    fun handleAnswer(option: Option) {
        hasAnswered = true
        val currentState = _triviaState.value
        if (currentState is PokemonTriviaUIState.Question) {
            val question = currentState.trivia
            question.options.forEach {
                it.color = if (it.isCorrect) {
                    androidx.compose.ui.graphics.Color.Green
                } else {
                    if (it.name == option.name) androidx.compose.ui.graphics.Color.Red else androidx.compose.ui.graphics.Color.Gray
                }
            }
            repository.markQuestionAsAnswered(question)
            _triviaState.update { PokemonTriviaUIState.Question(question) }
        }
    }

    fun resetTrivia() {
        repository.resetQuestions()
        loadRandomQuestion()
    }
}

sealed class PokemonTriviaUIState {
    object Loading : PokemonTriviaUIState()
    object Empty : PokemonTriviaUIState()
    data class Question(val trivia: PokemonTriviaModel) : PokemonTriviaUIState()
}