package com.example.pokedex.mainViews.WhoIsThatPokemon

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pokedex.shared.BackButton
import com.example.pokedex.shared.Option
import com.example.pokedex.shared.ProgressIndicator
import com.example.pokedex.shared.WhoIsThatPokemon

@Composable
fun WhoIsThatPokemonView(modifier: Modifier = Modifier, navController: NavController) {
    val viewModel = viewModel<WhoIsThatPokemonViewModel>()
    val whoIsThatPokemon = viewModel.whoIsThatPokemonStateFlow.collectAsState().value
    when (whoIsThatPokemon) {
        is WhoIsThatPokemonUIState.Data -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BackButton(
                    navController = navController,
                    modifier = Modifier.align(Alignment.Start)
                )

                Text(text = "Who Is That Pokemon",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(10.dp)
                )

                AsyncImage(
                    modifier = modifier.height(250.dp),
                    model = whoIsThatPokemon.pokemon.pokemon.sprites.front_default,
                    contentDescription = "Blacked out image of pokemon"
                )

                Options(viewModel, whoIsThatPokemon.pokemon)
            }
        }

        WhoIsThatPokemonUIState.Empty -> Text(text = "Empty")
        WhoIsThatPokemonUIState.Loading -> ProgressIndicator()
    }
}

@Composable
fun Options (viewModel: WhoIsThatPokemonViewModel, whoIsThatPokemon : WhoIsThatPokemon) {
    LazyColumn (modifier = Modifier.fillMaxHeight()) {
        items(whoIsThatPokemon.options) { item: Option ->
            OptionComposable(option = item, viewModel = viewModel)
        }
    }
}

@Composable
fun OptionComposable(modifier : Modifier = Modifier, option : Option, viewModel: WhoIsThatPokemonViewModel) {
    Button(
        modifier = modifier
            .padding(5.dp)
            .height(90.dp)
            .fillMaxWidth(),
        onClick = { viewModel.guessed() }
    ) {
        Text(text = option.name,
            color = viewModel.getColor(option),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold)
    }
}
@Preview (showBackground = true)
@Composable
fun WhoIsThatPokemonPreview() {
    val navController = rememberNavController()
    WhoIsThatPokemonView(navController = navController)
}
