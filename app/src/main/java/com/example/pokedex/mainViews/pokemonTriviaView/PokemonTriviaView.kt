package com.example.pokedex.mainViews.pokemonTriviaView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pokedex.repositories.PokemonTriviaRepository
import com.example.pokedex.shared.BackButton
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokedex.shared.Option


@Composable
fun AnswerButtons(viewModel: PokemonTriviaViewModel, options: List<Option>) {
    options.forEach { option ->
        Box(
            modifier = Modifier
                .size(350.dp, 100.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(option.color)
                .clickable(enabled = !viewModel.hasAnswered) {
                    viewModel.handleAnswer(option)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = option.name,
                modifier = Modifier.padding(horizontal = 25.dp, vertical = 10.dp),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.size(10.dp))
    }
}

@Composable
fun PokemonTriviaView(navController: NavController) {
    val viewModel = remember { PokemonTriviaViewModel(PokemonTriviaRepository()) }
    val triviaState by viewModel.triviaState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFDD99)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(
            navController = navController,
            modifier = Modifier.align(Alignment.Start)
        )

        when (triviaState) {
            is PokemonTriviaUIState.Loading -> {
                Text(text = "Loading question...", fontSize = 20.sp)
            }

            is PokemonTriviaUIState.Empty -> {
                Text(text = "No more questions available.", fontSize = 20.sp)
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = { viewModel.resetTrivia() }) {
                    Text(text = "Reset Questions")
                }
            }

            is PokemonTriviaUIState.Question -> {
                val trivia = (triviaState as PokemonTriviaUIState.Question).trivia
                Text(
                    text = trivia.question,
                    modifier = Modifier.padding(horizontal = 25.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.size(120.dp))

                AnswerButtons(viewModel, trivia.options)

                if (viewModel.hasAnswered) {
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(onClick = { viewModel.loadRandomQuestion() }) {
                        Text(text = "Next Question")
                    }
                }
            }
        }
    }

    @Composable
    fun PokemonTriviaViewPreview() {
        val navController = rememberNavController()
        PokemonTriviaView(navController)
    }
}