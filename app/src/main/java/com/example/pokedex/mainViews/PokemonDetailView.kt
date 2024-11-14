package com.example.pokedex.mainViews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pokedex.shared.Pokemon

@Composable
fun PokemonDetailView(pokemon: Pokemon) {
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

    }
}

@Preview(showBackground = true)
@Composable
fun PokemonDetailViewPreview() {
    val testPokemon = Pokemon("Pikachu", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/132.png")
    PokemonDetailView(pokemon = testPokemon)
}