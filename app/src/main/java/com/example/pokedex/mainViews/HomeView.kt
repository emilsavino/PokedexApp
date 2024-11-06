package com.example.pokedex.mainViews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeView(modifier: Modifier = Modifier) {
    PokemonOfTheDayView(modifier)
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = "Home View",
            style = MaterialTheme.typography.titleLarge,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    HomeView()
}

@Composable
fun PokemonOfTheDayView(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Who's that Pokemon?")
    }
}

fun OptionsRowView() {

}

fun RecentlyViewedView() {

}

fun RecentlyViewedPokemon() {

}

