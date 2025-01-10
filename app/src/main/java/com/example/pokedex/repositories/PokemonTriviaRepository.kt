package com.example.pokedex.repositories

import androidx.compose.ui.graphics.Color
import com.example.pokedex.shared.Option
import com.example.pokedex.shared.PokemonTriviaModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PokemonTriviaRepository {

    private val triviaQuestions = listOf(
        PokemonTriviaModel(
            question = "What is the name of the pokémon, with the ability to morph into any other?",
            options = listOf(
                Option("Swablu", Color.Gray),
                Option("Pikachu", Color.Gray),
                Option("Ditto", Color.Gray, isCorrect = true),
                Option("Magikarp", Color.Gray)
            ),
            correctAnswer = "Ditto"
        ),
        PokemonTriviaModel(
            question = "Which Pokémon is known as the Fire Lizard?",
            options = listOf(
                Option("Bulbasaur", Color.Gray),
                Option("Charmander", Color.Gray, isCorrect = true),
                Option("Squirtle", Color.Gray),
                Option("Eevee", Color.Gray)
            ),
            correctAnswer = "Charmander"
        ),
        PokemonTriviaModel(
            question = "What is the evolved form of Pikachu?",
            options = listOf(
                Option("Raichu", Color.Gray, isCorrect = true),
                Option("Jolteon", Color.Gray),
                Option("Zubat", Color.Gray),
                Option("Electrode", Color.Gray)
            ),
            correctAnswer = "Raichu"
        )
    )

    fun getRandomQuestion(): PokemonTriviaModel {
        return triviaQuestions.random()
    }
}


