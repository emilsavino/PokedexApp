package com.example.pokedex.mainViews.pokemonTriviaView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pokedex.ui.shared.BackButton
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokedex.dataClasses.PokemonTriviaModel
import com.example.pokedex.dataClasses.formatPokemonName
import com.example.pokedex.ui.shared.OptionButton
import com.example.pokedex.ui.shared.ProgressIndicator

@Composable
fun PokemonTriviaView(navController: NavController) {
    val viewModel: PokemonTriviaViewModel = viewModel()
    val triviaState = viewModel.triviaState.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.background)
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
                text = "Correct Streak: ${viewModel.streakCount}",
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
                    MakeQuestion(viewModel, triviaState.trivia)
                }

                is PokemonTriviaUIState.Loading -> {
                    ProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun MakeQuestion(viewModel: PokemonTriviaViewModel, trivia: PokemonTriviaModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = trivia.question,
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
        )
    }

    Spacer(modifier = Modifier.size(16.dp))

    AnswerButtons(viewModel, trivia)

    if (viewModel.hasAnswered) {
        Button(
            onClick = { viewModel.loadRandomQuestion() },
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(text = "Next Question")
        }
    }
}

@Composable
private fun AnswerButtons(viewModel: PokemonTriviaViewModel, trivia: PokemonTriviaModel) {
    trivia.options.forEach { option ->
        OptionButton(
            text = option.name.formatPokemonName(),
            color = viewModel.getOptionColor(option),
            onClick = { viewModel.handleAnswer(option) }
        )
    }
}