package com.example.pokedex.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.navigation.Screen

@Composable
fun PokemonGridItem(navController: NavController, pokemon: MockPokemon) {
    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Gray)
            .clickable { navController.navigate(Screen.PokemonDetails.createRoute(pokemon.name)) }
    ) {
        AsyncImage(
            model = pokemon.imageURL,
            contentDescription = "Picture of a Pokemon",
            modifier = Modifier.fillMaxSize()
        )
    }
}


