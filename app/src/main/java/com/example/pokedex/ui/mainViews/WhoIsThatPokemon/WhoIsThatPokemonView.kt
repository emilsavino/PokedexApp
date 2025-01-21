package com.example.pokedex.mainViews.WhoIsThatPokemon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pokedex.ui.shared.BackButton
import com.example.pokedex.dataClasses.Option
import com.example.pokedex.dataClasses.PokemonTypeResources
import com.example.pokedex.ui.shared.ProgressIndicator
import com.example.pokedex.dataClasses.WhoIsThatPokemon
import com.example.pokedex.dataClasses.formatPokemonName
import com.example.pokedex.dataClasses.getSprite
import com.example.pokedex.ui.shared.OptionButton

@Composable
fun WhoIsThatPokemonView(modifier: Modifier = Modifier, navController: NavController) {
    val viewModel = viewModel<WhoIsThatPokemonViewModel>()
    val whoIsThatPokemon = viewModel.whoIsThatPokemonStateFlow.collectAsState().value
    when (whoIsThatPokemon) {
        is WhoIsThatPokemonUIState.Data -> {
            val primaryTypeForPokemonOnScreen = whoIsThatPokemon.pokemon.pokemon.types.firstOrNull()?.type?.name ?: "normal"
            val gradient = PokemonTypeResources().getTypeGradient(primaryTypeForPokemonOnScreen)
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(gradient),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BackButton(
                        navController = navController,
                        modifier = modifier.padding(horizontal = 10.dp)
                    )

                    IconButton(
                        onClick = { viewModel.nextPokemon() },
                        modifier = modifier.padding(horizontal = 10.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                    }
                }

                Text(text = "Who Is That Pokemon",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(10.dp)
                )

                AsyncImage(
                    modifier = modifier.height(250.dp),
                    model = whoIsThatPokemon.pokemon.pokemon.getSprite(),
                    contentDescription = "Blacked out image of pokemon"
                )

                Options(viewModel, whoIsThatPokemon.pokemon)
            }
        }

        WhoIsThatPokemonUIState.Empty -> Text(text = "Empty")
        WhoIsThatPokemonUIState.Loading -> ProgressIndicator()
    }
}

@Composable
private fun Options (viewModel: WhoIsThatPokemonViewModel, whoIsThatPokemon : WhoIsThatPokemon) {
    LazyColumn (modifier = Modifier.fillMaxHeight()) {
        items(whoIsThatPokemon.options) { item: Option ->
            OptionButton(
                text = item.name.formatPokemonName(),
                color = viewModel.getColor(item),
                onClick = { viewModel.guessed() }
            )
        }
    }
}