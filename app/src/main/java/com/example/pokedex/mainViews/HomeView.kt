package com.example.pokedex.mainViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.pokedex.data.MockPokemonDataStore
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.launch

@Composable
fun HomeView(modifier: Modifier = Modifier, navController: NavController) {

    val coroutineScope = rememberCoroutineScope()
    var pokemonList by remember { mutableStateOf<List<Pokemon>>(emptyList()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            pokemonList = MockPokemonDataStore().fetchPokemons()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFDD99)),
        ) {
        if (pokemonList.isNotEmpty()) {
            PokemonOfDayView(pokemon = pokemonList[0])
        }
        GamesRow()
        RecentlyViewedPokemonView(recentPokemons = pokemonList)
    }
}


@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    val navController = rememberNavController()
    HomeView(navController = navController)
}

@Composable
fun PokemonOfDayView(pokemon: Pokemon) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pokémon of the Day",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = rememberAsyncImagePainter(model = pokemon.imageURL),
            contentDescription = pokemon.name,
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Name: ", fontSize = 16.sp
            )
            Text(
                text = pokemon.name, fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Type: ", fontSize = 16.sp
            )
            Text(
                //TODO: type needs to be updated so that it can be displayed as the correct type.
                text = "Fire", fontSize = 16.sp
            )
        }
    }
}

@Composable
fun GamesRow() {
    Text(
        text = "Pokémon Games",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(8.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(120.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Who's that Pokémon?", fontSize = 14.sp)
        }
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(120.dp)
                .background(
                    MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)
                ), contentAlignment = Alignment.Center
        ) {
            Text(text = "Pokémon Trivia", fontSize = 14.sp)
        }
    }
}

@Composable
fun RecentlyViewedPokemonView(recentPokemons: List<Pokemon>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Recently viewed Pokémon",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(8.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recentPokemons.size) { index ->
                RecentlyViewedPokemon(pokemon = recentPokemons[index])
            }
        }
    }
}

@Composable
fun RecentlyViewedPokemon(pokemon: Pokemon) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(120.dp)
            .background(Color(0xFFB2DFDB), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = pokemon.imageURL),
                contentDescription = pokemon.name,
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = pokemon.name,
                fontSize = 12.sp
            )
        }
    }
}

