package com.example.pokedex.mainViews.addToTeamView

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.navigation.Screen
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonTypeResources
import com.example.pokedex.shared.ProgressIndicator
import com.example.pokedex.shared.formatPokemonName
import com.example.pokedex.shared.getSprite


@Composable
fun AddToTeamView(teamName: String, modifier: Modifier = Modifier, navController: NavController) {
    val viewModel = viewModel<AddToTeamViewModel>(key = teamName) { AddToTeamViewModel(teamName) }

    val pokemons = viewModel.pokemonList.collectAsState().value

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PokemonTypeResources().appGradient()),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            MakeSearchBar(viewModel = viewModel)

            Text(
                text = "Suggestions",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (pokemons) {
                is SearchUIState.Empty -> {
                    Text(
                        text = "No Pokémon found",
                        fontSize = 20.sp
                    )
                }

                is SearchUIState.Loading -> {
                    ProgressIndicator()
                }

                is SearchUIState.Data -> {
                    MakeSuggestionsList(pokemons.pokemonList, viewModel, navController)
                }
            }
        }
    }
}

@Composable
private fun MakeSearchBar(viewModel: AddToTeamViewModel) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .shadow(0.dp),

        placeholder = {
            Text(
                "Search...",
                color = Color.Black
            )
        },

        onValueChange = {
            viewModel.searchText.value = it
            viewModel.searchPokemonList()
        },

        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            autoCorrectEnabled = false
        ),

        keyboardActions = KeyboardActions.Default,
        value = viewModel.searchText.value,
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
    )
}

@Composable
private fun MakeSuggestionsList(pokemons: List<Pokemon>, viewModel: AddToTeamViewModel, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pokemons) { pokemon ->
            SuggestionListItem(pokemon = pokemon, viewModel = viewModel, navController = navController)
        }
    }
}

@Composable
private fun SuggestionListItem(pokemon: Pokemon, viewModel: AddToTeamViewModel, navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(shape = RoundedCornerShape(24.dp), color = Color.White)
            .padding(4.dp)
            .pointerInput(Unit) {
                detectTapGestures (
                    onLongPress = { navController.navigate(Screen.PokemonDetails.createRoute(pokemon.name)) },
                    onTap = {
                        viewModel.addPokemonToRecentlySearched(pokemon.name)
                        viewModel.addToTeam(pokemon, navController)
                    }
                )
            }
    ) {
        AsyncImage(
            model = pokemon.getSprite(),
            contentDescription = "${pokemon.name} Image",
            modifier = Modifier
                .size(100.dp)
                .padding(end = 2.dp)
        )
        Text(
            text = pokemon.name.formatPokemonName(),
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}