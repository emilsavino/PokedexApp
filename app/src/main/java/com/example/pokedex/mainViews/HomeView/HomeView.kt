package com.example.pokedex.mainViews.HomeView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.pokedex.navigation.Screen
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.R
import com.example.pokedex.shared.PokemonTypeResources
import com.example.pokedex.shared.formatPokemonName
import com.example.pokedex.shared.ProgressIndicator
import com.example.pokedex.shared.getSprite

private val Padding = 8.dp
private val typeResources = PokemonTypeResources()

@Composable
fun HomeView(navController: NavController) {
    val viewModel = viewModel<HomeViewModel>()
    val pokemonOfTheDay = viewModel.pokemonOfTheDay.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.getPokemonOfTheDay()
        viewModel.getRecentlyViewedPokemons()
    }

    when (pokemonOfTheDay) {
        is HomeUIState.Empty -> {
            Text(text = "No Pokémon found")
        }
        is HomeUIState.Loading -> {
            MakeHomeLoadingScreen()
        }
        is HomeUIState.Data -> {
            MakeHomeView(navController, pokemonOfTheDay.pokemonOfTheDay, viewModel)
        }
    }
}

@Composable
private fun MakeHomeLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.loading_screen),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        CircularProgressIndicator(
            color = Color.Blue,
            strokeWidth = 4.dp
        )
    }
}

@Composable
private fun MakeHomeView(
    navController: NavController,
    pokemon: Pokemon,
    viewModel: HomeViewModel
) {
    val recentPokemons = viewModel.recentlyViewedPokemons.collectAsState().value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PokemonTypeResources().appGradient())
            .verticalScroll(rememberScrollState()),
    ) {
        PokemonOfDayView(pokemon = pokemon, navController = navController)
        GamesRow(navController = navController)

        when (recentPokemons) {
            is RecentsUIState.Empty -> {
                Text(text = "No recently viewed Pokémon")
            }
            is RecentsUIState.Loading -> {
                ProgressIndicator()
            }
            is RecentsUIState.Data -> {
                RecentlyViewedPokemons(recentPokemons = recentPokemons.pokemons, navController = navController)
            }
        }
    }
}

@Composable
private fun PokemonOfDayView(pokemon: Pokemon, navController: NavController) {
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
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = pokemon.getSprite()),
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
private fun PokemonDetailsRow(pokemon: Pokemon) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Name: ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = pokemon.name.formatPokemonName(), fontSize = 16.sp)
        Spacer(modifier = Modifier.width(Padding))
        Text(text = "Types: ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        pokemon.types.forEach { type ->
            val typeImage = typeResources.getTypeImage(type.type.name)
            Image(
                painter = typeImage,
                contentDescription = "${type.type.name} type image",
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
private fun GamesRow(navController: NavController) {
    Text(
        text = "Pokémon Games",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(Padding),
        fontWeight = FontWeight.Bold
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        HomePageBox(
            color = MaterialTheme.colorScheme.primary,
            onClick = { navController.navigate(Screen.WhoIsThatPokemon.route) }
        ) {
            MakeWhosThatPokemonBox()
        }

        HomePageBox(
            color = MaterialTheme.colorScheme.secondary,
            onClick = { navController.navigate(Screen.PokemonTrivia.route) }
        ) {
            MakePokemonTriviaBox()
        }
    }
}

@Composable
private fun HomePageBox(color: Color,
                onClick: () -> Unit,
                content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .width(190.dp)
            .height(160.dp)
            .padding(6.dp)
            .background(color, RoundedCornerShape(Padding))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun MakeWhosThatPokemonBox() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(Padding))
    ) {
        Image(
            painter = painterResource(id = R.drawable.whos_that_pokemon),
            contentDescription = "Who's that Pokémon?",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = "Who's that Pokémon?",
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp)
        )
    }
}

@Composable
private fun MakePokemonTriviaBox() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(Padding))
    ) {
        Image(
            painter = painterResource(id = R.drawable.pokemon_trivia),
            contentDescription = "Pokémon Trivia",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = "Pokémon Trivia",
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp)
        )
    }
}

@Composable
private fun RecentlyViewedPokemons(recentPokemons: List<Pokemon>, navController: NavController) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Recently Viewed Pokémon",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(Padding),
            fontWeight = FontWeight.Bold
        )
        Column {
            for (pokemons in recentPokemons.asReversed().chunked(2)) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for(pokemon in pokemons) {
                        RecentlyViewedPokemonItem(pokemon, navController)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentlyViewedPokemonItem(pokemon: Pokemon, navController: NavController) {
    HomePageBox(
        color = Color.Gray.copy(0.5f),
        onClick = { navController.navigate(Screen.PokemonDetails.createRoute(pokemon.name)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = pokemon.getSprite()),
                contentDescription = pokemon.name,
                modifier = Modifier
                    .fillMaxSize(1f)
                    .aspectRatio(1f)
                    .weight(1f)
                    .padding(4.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = pokemon.name.formatPokemonName(),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}