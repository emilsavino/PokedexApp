package com.example.pokedex.mainViews.SavedPokemonsView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonGridItem
import com.example.pokedex.shared.PokemonTypeResources
import com.example.pokedex.shared.ProgressIndicator

@Composable
fun SavedView(navController: NavController) {
    val viewModel = viewModel<SavedViewModel>()
    val savedState = viewModel.savedState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PokemonTypeResources().appGradient())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Saved Pokemons",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
        )

        when (savedState) {
            is SavedUIState.Empty -> {
                Text(text = "No saved pokemons")
            }

            is SavedUIState.Loading -> {
                ProgressIndicator()
            }

            is SavedUIState.Data -> {
                if (savedState.saved.isEmpty()) {
                    Text(text = "No saved pokemons")
                    viewModel.savedIsEmpty()
                } else {
                    SavedList(navController, savedState.saved)
                }
            }
        }
    }
}

@Composable
private fun SavedList (navController: NavController, savedPokemons: List<Pokemon>) {
    val pokemonsChunked = savedPokemons.chunked(3)
    LazyColumn (
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(pokemonsChunked) { item ->
            SavedRow(pokemons = item, navController = navController)
        }
    }
}

@Composable
private fun SavedRow(modifier: Modifier = Modifier, pokemons : List<Pokemon>, navController: NavController) {
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(19.dp)
    ) {
        for (pokemon in pokemons) {
            PokemonGridItem(navController, pokemon = pokemon)
        }
    }
}

@Preview (showBackground = true)
@Composable
fun SavedViewPreview() {
    val navController = rememberNavController()
    SavedView(navController = navController)
}