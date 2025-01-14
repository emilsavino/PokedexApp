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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pokedex.shared.BackButton
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokedex.shared.PokemonTriviaModel
import com.example.pokedex.shared.PokemonTypeResources

@Composable
fun PokemonTriviaView(navController: NavController) {
    val viewModel: PokemonTriviaViewModel = viewModel()
    val triviaState by viewModel.triviaState.collectAsState()
    val streakCount by viewModel.streakCount.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PokemonTypeResources().appGradient())
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BackButton(
                navController = navController,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                text = "Correct Streak: $streakCount",
                modifier = Modifier.padding(vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            when (triviaState) {
                is PokemonTriviaUIState.Empty -> {
                    Text(
                        text = "No more questions available.",
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Button(onClick = { viewModel.resetTrivia() }) {
                        Text(text = "Reset Questions")
                    }
                }

                is PokemonTriviaUIState.Question -> {
                    val trivia = (triviaState as PokemonTriviaUIState.Question).trivia
                    Text(
                        text = trivia.question,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .align(Alignment.Start),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Start,
                    )

                    Spacer(modifier = Modifier.size(16.dp))

                    AnswerButtons(viewModel, trivia)

                    if (viewModel.hasAnswered) {
                        Spacer(modifier = Modifier.size(16.dp))
                        Button(
                            onClick = { viewModel.loadRandomQuestion() },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 16.dp)
                        ) {
                            Text(text = "Next Question")
                        }
                    }
                }

                else -> {
                    Text(text = "Loading...", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun AnswerButtons(viewModel: PokemonTriviaViewModel, trivia: PokemonTriviaModel) {
    trivia.options.forEach { option ->
        Box(
            modifier = Modifier
                .size(300.dp, 80.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(viewModel.getOptionColor(option))
                .clickable(enabled = !viewModel.hasAnswered) {
                    viewModel.handleAnswer(option)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = option.name,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.size(8.dp))
    }
}