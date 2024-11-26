package com.example.pokedex.mainViews.pokemonTriviaView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonTriviaAnswer
import com.example.pokedex.shared.PokemonTriviaModel

class PokemonTriviaViewModel: ViewModel() {
    val question = "What is the name of the pokémon, with the ability to morph into any other?"
    val options = listOf(
        PokemonTriviaAnswer("Swablu", false),
        PokemonTriviaAnswer("Pikachu", false),
        PokemonTriviaAnswer("Ditto", true),
        PokemonTriviaAnswer("Magikarp", false)
    )
    val pokemonTrivia = PokemonTriviaModel(question, options)
    var hasAnswered by mutableStateOf(false)

    fun getBoxColor(answer: PokemonTriviaAnswer): Color {
        if (hasAnswered) {
            if (answer.isCorrect) {
                return Color.Green
            }
            return Color.Red

        } else {
            return Color.Gray
        }
    }
}