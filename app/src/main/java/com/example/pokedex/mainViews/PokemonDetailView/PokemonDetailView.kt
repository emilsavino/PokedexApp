package com.example.pokedex.mainViews.PokemonDetailView

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun PokemonDetailView(pokemonName: String, navController: NavController) {
    val viewModel = viewModel<PokemonDetailViewModel>()
    val pokemon = viewModel.getPokemonByName(pokemonName)
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.size(500.dp)
        ) {
            AsyncImage(
                model = pokemon.imageURL,
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

