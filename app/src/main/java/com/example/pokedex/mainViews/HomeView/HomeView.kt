package com.example.pokedex.mainViews.HomeView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.pokedex.navigation.Screen
import com.example.pokedex.shared.MockPokemon

private val BoxSize = 150.dp
private val BoxHeight = 120.dp
private val Padding = 8.dp

@Composable
fun HomeView(modifier: Modifier = Modifier, navController: NavController, homeViewModel: HomeViewModel = viewModel()) {
    val pokemonList = homeViewModel.pokemonList.collectAsState().value

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFDD99)),
    ) {
        if (pokemonList.isNotEmpty()) {
            PokemonOfDayView(pokemon = pokemonList[0], navController = navController)
        }
        GamesRow(navController = navController)
        RecentlyViewedPokemonView(recentPokemons = pokemonList, navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    val navController = rememberNavController()
    HomeView(navController = navController)
}

@Composable
fun PokemonOfDayView(pokemon: MockPokemon, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Padding)
            .clickable { navController.navigate(Screen.PokemonDetails.createRoute(pokemon.name)) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pokémon of the Day",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(Padding))
        Image(
            painter = rememberAsyncImagePainter(model = pokemon.imageURL),
            contentDescription = pokemon.name,
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(Padding))
        PokemonDetailsRow(pokemon)
    }
}

@Composable
fun PokemonDetailsRow(pokemon: MockPokemon) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Name: ", fontSize = 16.sp)
        Text(text = pokemon.name, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(Padding))
        Text(text = "Type: ", fontSize = 16.sp)
        Text(text = "Fire", fontSize = 16.sp) // TODO: Update to display the correct type
    }
}

@Composable
fun GamesRow(navController: NavController) {
    Text(
        text = "Pokémon Games",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(Padding)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        GameBox(
            text = "Who's that Pokémon?",
            color = MaterialTheme.colorScheme.primary,
            onClick = { navController.navigate(Screen.WhoIsThatPokemon.route) }
        )
        GameBox(
            text = "Pokémon Trivia",
            color = MaterialTheme.colorScheme.secondary,
            onClick = { navController.navigate(Screen.PokemonTrivia.route) }
        )
    }
}

@Composable
fun GameBox(text: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(BoxSize)
            .height(BoxHeight)
            .background(color, RoundedCornerShape(Padding))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 14.sp)
    }
}

@Composable
fun RecentlyViewedPokemonView(recentPokemons: List<MockPokemon>, navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Recently viewed Pokémon",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(Padding)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(Padding),
            contentPadding = PaddingValues(Padding),
            horizontalArrangement = Arrangement.spacedBy(Padding),
            verticalArrangement = Arrangement.spacedBy(Padding)
        ) {
            items(recentPokemons.size) { index ->
                RecentlyViewedPokemon(pokemon = recentPokemons[index], navController = navController)
            }
        }
    }
}

@Composable
fun RecentlyViewedPokemon(pokemon: MockPokemon, navController: NavController) {
    Box(
        modifier = Modifier
            .width(BoxSize)
            .height(BoxHeight)
            .background(Color(0xFFB2DFDB), RoundedCornerShape(Padding))
            .clickable { navController.navigate(Screen.PokemonDetails.createRoute(pokemon.name)) },
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = pokemon.imageURL),
            contentDescription = pokemon.name,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            contentScale = ContentScale.Fit
        )
        Text(
            text = pokemon.name,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = Padding)
                .align(Alignment.BottomCenter)
        )
    }
}