package com.example.pokedex.mainViews.PokemonDetailView

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.launch

@Composable
fun PokemonDetailView(pokemonName: String, navController: NavController) {
    val viewModel = viewModel<PokemonDetailViewModel>(key = pokemonName) { PokemonDetailViewModel(pokemonName) }
    val pokemon = viewModel.pokemon.collectAsState().value

    when (pokemon) {
        is PokemonDetailUIState.Empty -> {
            EmptyState()
        }
        is PokemonDetailUIState.Loading -> {
            LoadingState()
        }
        is PokemonDetailUIState.Data -> {
            PokemonDetail(navController, pokemon.pokemon, viewModel)
        }
    }
}

@Composable
fun PokemonDetail(navController: NavController, pokemon: Pokemon, viewModel: PokemonDetailViewModel) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.size(500.dp)
        ) {
            AsyncImage(
                model = pokemon.sprites.front_default,
                contentDescription = "Picture of a Pokemon",
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            modifier = Modifier
                .padding(10.dp)
                .align(CenterHorizontally),
            text = pokemon.name,
            fontSize = 20.sp,
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.savePokemon(pokemon)
                }
            },
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(10.dp)
        ) {
            Text(text = viewModel.favouriteButtonText)
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(10.dp)
        ) {
            Text(text = "Go back")
        }
    }
}

@Composable
fun EmptyState() {
    Text(
        text = "No Pokemon found",
        fontSize = 20.sp
    )
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp)
        )
    }
}

