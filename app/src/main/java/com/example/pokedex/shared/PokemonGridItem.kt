package com.example.pokedex.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PokemonGridItem(modifier: Modifier = Modifier, pokemon: Pokemon) {
    Box(
        modifier = modifier
            .size(110.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Gray)
    ) {
        AsyncImage(
            model = pokemon.imageURL,
            contentDescription = "Picture of a Pokemon",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonGridItemPreview() {
    val testPokemon = Pokemon("Pikachu", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/132.png")
    PokemonGridItem(pokemon = testPokemon)
}
