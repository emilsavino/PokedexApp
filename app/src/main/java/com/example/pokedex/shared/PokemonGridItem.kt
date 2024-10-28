package com.example.pokedex.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.pokedex.R
import java.util.jar.Attributes.Name

@Composable
fun PokemonGridItem(modifier: Modifier = Modifier, pokemon: Pokemon) {
    Scaffold { innerPadding ->
        modifier.padding(innerPadding)
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonGridItemPreview() {
    val testPokemon = Pokemon("Pikachu", null)
    PokemonGridItem(pokemon = testPokemon)
}
