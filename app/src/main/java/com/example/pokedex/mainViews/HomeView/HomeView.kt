package com.example.pokedex.mainViews.HomeView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.pokedex.navigation.Screen
import com.example.pokedex.shared.Pokemon

private val BoxSize = 150.dp
private val BoxHeight = 120.dp
private val Padding = 8.dp

@Composable
fun HomeView(modifier: Modifier = Modifier, navController: NavController) {
    val viewModel = viewModel<HomeViewModel>()
    val pokemonOfTheDay = viewModel.pokemonOfTheDay.collectAsState().value

    when (pokemonOfTheDay) {
        is HomeUIState.Empty -> {
            Text(text = "No Pokémon found")
        }
        is HomeUIState.Loading -> {
            Text(text = "Loading Pokémon")
        }
        is HomeUIState.Data -> {
            MakeHomeView(navController, pokemonOfTheDay.pokemonOfTheDay, viewModel)
        }
    }


}

@Composable
fun MakeHomeView(navController: NavController, pokemon: Pokemon, viewModel: HomeViewModel) {
    val recentPokemons = viewModel.recentlyViewedPokemons.collectAsState().value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFDD99)),
    ) {
        PokemonOfDayView(pokemon = pokemon, navController = navController)
        GamesRow(navController = navController)

        when (recentPokemons) {
            is RecentsUIState.Empty -> {
                Text(text = "No recently viewed Pokémon")
            }
            is RecentsUIState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            }
            is RecentsUIState.Data -> {
                RecentlyViewedPokemons(recentPokemons = recentPokemons.pokemons, navController = navController)
            }
        }
    }
}

@Composable
fun PokemonOfDayView(pokemon: Pokemon, navController: NavController) {
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

        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = pokemon.sprites.front_default),
                contentDescription = pokemon.name,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
        }

        PokemonDetailsRow(pokemon)
    }
}

@Composable
fun PokemonDetailsRow(pokemon: Pokemon) {
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
fun RecentlyViewedPokemons(recentPokemons: List<Pokemon>, navController: NavController) {
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
                RecentlyViewedPokemonItem(pokemon = recentPokemons[index], navController = navController)
            }
        }
    }
}

@Composable
fun RecentlyViewedPokemonItem(pokemon: Pokemon, navController: NavController) {
    Box(
        modifier = Modifier
            .size(BoxSize)
            .background(Color(0xFFB2DFDB), RoundedCornerShape(Padding))
            .clickable { navController.navigate(Screen.PokemonDetails.createRoute(pokemon.name)) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = pokemon.sprites.front_default),
                contentDescription = pokemon.name,
                modifier = Modifier
                    .fillMaxSize(1f)
                    .aspectRatio(1f)
                    .weight(1f)
                    .padding(4.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = pokemon.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}