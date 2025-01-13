package com.example.pokedex.repositories

import androidx.compose.ui.graphics.Color
import com.example.pokedex.shared.Option
import com.example.pokedex.shared.PokemonTriviaModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PokemonTriviaRepository {

    private val answeredQuestions = mutableSetOf<PokemonTriviaModel>()


    private val triviaQuestions = listOf(
        PokemonTriviaModel(
            question = "What is the name of the pokémon, with the ability to morph into any other?",
            options = listOf(
                Option("Swablu", Color.Gray, false),
                Option("Pikachu", Color.Gray, false),
                Option("Ditto", Color.Gray,true),
                Option("Magikarp", Color.Gray, isCorrect = false)
            ),
            correctAnswer = "Ditto"
        ),
        PokemonTriviaModel(
            question = "Which Pokémon is known as the Fire Lizard?",
            options = listOf(
                Option("Bulbasaur", Color.Gray, isCorrect = false),
                Option("Charmander", Color.Gray, isCorrect = true),
                Option("Squirtle", Color.Gray, isCorrect = false),
                Option("Eevee", Color.Gray, isCorrect = false)
            ),
            correctAnswer = "Charmander"
        ),
        PokemonTriviaModel(
            question = "What is the evolved form of Pikachu?",
            options = listOf(
                Option("Raichu", Color.Gray, isCorrect = true),
                Option("Jolteon", Color.Gray, isCorrect = false),
                Option("Zubat", Color.Gray, isCorrect = false),
                Option("Electrode", Color.Gray, isCorrect = false)
            ),
            correctAnswer = "Raichu"
        ),
        PokemonTriviaModel(
            question = "Which Pokemon has the types dragon and ground?",
            options = listOf(
                Option("Pawmo", Color.Gray, isCorrect = false),
                Option("Gulpin", Color.Gray, isCorrect = false),
                Option("Gabite", Color.Gray, isCorrect = true),
                Option("Pheromosa", Color.Gray, isCorrect = false)
            ),
            correctAnswer = "Gabite"
        ),
        PokemonTriviaModel(
            question = "Which Pokemon has the desc: With quick movements, it chases down its foes, attacking relentlessly with its horns until it prevails",
            options = listOf(
                Option("Rellor", Color.Gray, isCorrect = false),
                Option("Scolipede", Color.Gray, isCorrect = true),
                Option("Machamp", Color.Gray, isCorrect = false),
                Option("Gengar", Color.Gray, isCorrect = false)
            ),
            correctAnswer = "Scolipede"
        ),
        PokemonTriviaModel(
            question = "What Pokemon is a starter pokemon in generation 1",
            options = listOf(
                Option("Pikachu", Color.Gray, isCorrect = false),
                Option("Geodude", Color.Gray, isCorrect = false),
                Option("Squirtle", Color.Gray, isCorrect = true),
                Option("Rattata", Color.Gray, isCorrect = false)
            ),
            correctAnswer = "Squirtle"
        ),
        PokemonTriviaModel(
            question = "What type is the legendary pokemon Moltres",
            options = listOf(
                Option("Ice", Color.Gray, isCorrect = false),
                Option("Fire", Color.Gray, isCorrect = true),
                Option("Lightning", Color.Gray, isCorrect = false),
                Option("Dragon", Color.Gray, isCorrect = false)
            ),
            correctAnswer = "Fire"
        ),
    )

    fun getRandomUnansweredQuestion(): PokemonTriviaModel? {
        val unansweredQuestions = triviaQuestions.filterNot { answeredQuestions.contains(it) }
        return if (unansweredQuestions.isNotEmpty()) {
            unansweredQuestions.random()
        } else {
            null
        }
    }

    fun markQuestionAsAnswered(question: PokemonTriviaModel) {
        answeredQuestions.add(question)
    }

    fun resetQuestions() {
        answeredQuestions.clear()
    }
}


