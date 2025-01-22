package com.example.pokedex.mainViews.HomeView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.pokedex.ui.navigation.Screen
import com.example.pokedex.dataClasses.Pokemon
import com.example.pokedex.R
import com.example.pokedex.ui.shared.NoInternetAlert
import com.example.pokedex.dataClasses.PokemonTypeResources
import com.example.pokedex.dataClasses.formatPokemonName
import com.example.pokedex.ui.shared.ProgressIndicator
import com.example.pokedex.dataClasses.getSprite

private val Padding = 8.dp
private val typeResources = PokemonTypeResources()

@Composable
fun HomeView(navController: NavController) {
    val viewModel = viewModel<HomeViewModel>()
    val pokemonOfTheDay = viewModel.pokemonOfTheDay.collectAsState().value
    val recentPokemons = viewModel.recentlyViewedPokemons.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.getPokemonOfTheDay()
        viewModel.getRecentlyViewedPokemons()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PokemonTypeResources().appGradient())
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        when (pokemonOfTheDay) {
            is HomeUIState.Empty -> {
                Text(text = "No Pokémon found")
            }
            is HomeUIState.Loading -> {
                ProgressIndicator()
            }
            is HomeUIState.Data -> {
                PokemonOfDayView(pokemon = pokemonOfTheDay.pokemonOfTheDay, navController = navController)
            }
        }

        GamesRow(navController = navController, viewModel = viewModel)

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

        if (viewModel.showNoInternetAlert) {
            NoInternetAlert(
                tryingToDo = "play this game",
                onDismiss = { viewModel.showNoInternetAlert = false }
            )
        }
    }
}

@Composable
private fun PokemonOfDayView(pokemon: Pokemon, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Padding),
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
                    .aspectRatio(1f)
                    .clickable { navController.navigate(Screen.PokemonDetails.createRoute(pokemon.name)) },
                contentScale = ContentScale.Fit
            )
        }

        PokemonDetailsRow(pokemon, navController)
    }
}

@Composable
private fun PokemonDetailsRow(pokemon: Pokemon, navController: NavController) {
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
                    .clickable { navController.navigate(Screen.Search.createRoute(type.type.name)) }
            )
        }
    }
}

@Composable
private fun GamesRow(navController: NavController, viewModel: HomeViewModel) {
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
            onClick = { viewModel.onWhoIsThatPokemonClicked(navController) }
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
private fun HomePageBox(
    color: Color,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
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
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxSize()
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
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(255.dp)
                .height(170.dp)
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
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .weight(1f)
                    .clip(RoundedCornerShape(28.dp))
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, RoundedCornerShape(Padding)),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Text(
                    text = pokemon.name.formatPokemonName(),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )
            }
        }
    }
}