package com.example.pokedex.mainViews.SavedPokemonsView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonGridItem

@Composable
fun SavedView(modifier: Modifier = Modifier, navController: NavController) {
    val viewModel = SavedViewModel()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFDD99)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Saved",
            fontSize = 60.sp,
            modifier = modifier.padding(10.dp)
        )
        SavedList(viewModel, navController)
        Spacer(modifier = Modifier.padding(10.dp))
    }

}
@Composable
fun SavedList (savedViewModel: SavedViewModel, navController: NavController) {
    val pokemons = savedViewModel.savedState.collectAsState().value
    val pokemonsChunked = pokemons.chunked(3)
    LazyColumn (
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(pokemonsChunked) { item: List<Pokemon> ->
            SavedRow(pokemons = item, navController = navController)
        }
    }
}

@Composable
fun SavedRow(modifier: Modifier = Modifier, pokemons : List<Pokemon>, navController: NavController) {
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