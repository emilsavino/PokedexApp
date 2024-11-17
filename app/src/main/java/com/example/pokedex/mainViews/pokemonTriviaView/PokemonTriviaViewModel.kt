package com.example.pokedex.mainViews.pokemonTriviaView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class PokemonTriviaViewModel: ViewModel() {
    val question = "What is the name of the pokémon, with the ability to morph into any other?"
    val options = listOf("Swablu", "Pikachu", "Ditto", "Magikarp")
    val solutionColors = listOf(Color.Red, Color.Red, Color.Green, Color.Red)
    var hasAnswered by mutableStateOf(false)

    fun getBoxColor(index: Int): Color {
        return if (hasAnswered) {
            solutionColors[index]
        } else {
            Color.Gray
        }
    }
}