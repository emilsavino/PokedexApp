package com.example.pokedex.mainViews.pokemonTriviaView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

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

@Composable
fun PokemonTriviaView(navController: NavController) {
    val viewModel = remember { PokemonTriviaViewModel() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = viewModel.question,
            modifier = Modifier.padding(horizontal = 25.dp, vertical = 10.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        viewModel.options.forEachIndexed { index, option ->
            Box (
                modifier = Modifier
                    .size(300.dp, 80.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(viewModel.getBoxColor(index))
                    .clickable {
                        viewModel.hasAnswered = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    modifier = Modifier.padding(horizontal = 25.dp, vertical = 10.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.size(10.dp))
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PokemonTriviaViewPreview() {
    val navController = rememberNavController()
    PokemonTriviaView(navController)
}