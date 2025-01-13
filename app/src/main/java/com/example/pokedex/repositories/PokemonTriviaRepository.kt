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
        ),
        PokemonTriviaModel(
            question = "Which Pokemon has the types dragon and ground?",
            options = listOf(
                Option("Pawmo", Color.Gray),
                Option("Gulpin", Color.Gray),
                Option("Gabite", Color.Gray, isCorrect = true),
                Option("Pheromosa", Color.Gray)
            ),
            correctAnswer = "Gabite"
        ),
        PokemonTriviaModel(
            question = "Which Pokemon has the desc: With quick movements, it chases down its foes, attacking relentlessly with its horns until it prevails",
            options = listOf(
                Option("Rellor", Color.Gray),
                Option("Scolipede", Color.Gray, isCorrect = true),
                Option("Machamp", Color.Gray),
                Option("Gengar", Color.Gray)
            ),
            correctAnswer = "Scolipede"
        ),
        PokemonTriviaModel(
            question = "What Pokemon is a starter pokemon in generation 1",
            options = listOf(
                Option("Pikachu", Color.Gray),
                Option("Geodude", Color.Gray),
                Option("Squirtle", Color.Gray, isCorrect = true),
                Option("Rattata", Color.Gray)
            ),
            correctAnswer = "Squirtle"
        ),
        PokemonTriviaModel(
            question = "What type is the legendary pokemon Moltres",
            options = listOf(
                Option("Ice", Color.Gray),
                Option("Fire", Color.Gray, isCorrect = true),
                Option("Lightning", Color.Gray),
                Option("Dragon", Color.Gray)
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


