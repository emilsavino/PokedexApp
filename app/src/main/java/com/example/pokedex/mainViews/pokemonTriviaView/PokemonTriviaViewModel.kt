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
import kotlinx.coroutines.launch


class PokemonTriviaViewModel(private val repository: PokemonTriviaRepository): ViewModel() {
    var pokemonTrivia: PokemonTriviaModel? by mutableStateOf(null)
    var hasAnswered by mutableStateOf(false)

    init {
        loadRandomQuestion()
    }

    fun loadRandomQuestion() {
        val question = repository.getRandomUnansweredQuestion()
        if (question != null) {
            pokemonTrivia = question
            hasAnswered = false
        } else {
            pokemonTrivia = null
        }
    }

    fun handleAnswer(option: Option) {
        hasAnswered = true
        pokemonTrivia?.let { question ->
            question.options.forEach {
                it.color = if (it.isCorrect) {
                    Color.Green
                } else {
                    if (it.name == option.name) Color.Red else Color.Gray
                }
            }
            repository.markQuestionAsAnswered(question)
        }
    }

    fun resetTrivia() {
        repository.resetQuestions()
        loadRandomQuestion()
    }
}