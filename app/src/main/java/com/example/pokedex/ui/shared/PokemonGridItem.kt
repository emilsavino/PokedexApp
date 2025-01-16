package com.example.pokedex.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.dataClasses.Pokemon
import com.example.pokedex.dataClasses.getSprite
import com.example.pokedex.ui.navigation.Screen

@Composable
fun PokemonGridItem(navController: NavController, pokemon: Pokemon, onLongClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Gray.copy(alpha = 0.5f))
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongClick() },
                    onTap = { navController.navigate(Screen.PokemonDetails.createRoute(pokemon.name)) }
                )
            }
    ) {
        AsyncImage(
            model = pokemon.getSprite(),
            contentDescription = "Picture of a Pokemon",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AddToTeamGridItem(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Gray.copy(alpha = 0.6f))
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = Icons.Default.AddCircle,
            tint = Color.White,
            contentDescription = "Add to Team",
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        )
    }
}

@Composable
fun EmptyGridItem(navController: NavController) {
    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Gray.copy(alpha = 0.6f))
    )
}


