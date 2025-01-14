package com.example.pokedex.mainViews.addToTeamView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.shared.formatPokemonName
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonTypeResources
import com.example.pokedex.shared.ProgressIndicator
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
            MakeSearchTools(viewModel = viewModel)

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
                    MakeSearchList(
                        pokemons = pokemons.pokemonList,
                        navController = navController,
                        viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun MakeSearchTools(viewModel: AddToTeamViewModel) {
    MakeSearchBar(viewModel)

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val selectedColor = Color(0x636363FF)
        val unselectedColor = Color(0x00FF00FF)
        val textColor = Color.Black

        MakeFilterButton(viewModel, textColor, selectedColor, unselectedColor)

        Spacer(modifier = Modifier.padding(10.dp))

        MakeSortButton(viewModel, textColor, selectedColor, unselectedColor)
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
private fun MakeFilterButton(
    viewModel: AddToTeamViewModel,
    textColor: Color,
    selectedColor: Color,
    unselectedColor: Color
) {
    var filterExpanded = remember { mutableStateOf(false) }
    Button(
        onClick = { filterExpanded.value = true },
        colors = buttonColors(containerColor = Color(0xfff2f2f2)),
        modifier = Modifier.shadow(2.dp, CircleShape)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.List,
                contentDescription = "Filter",
                tint = textColor
            )
            Spacer(modifier = Modifier.padding(2.dp))

            Text(
                text = "Filter",
                color = Color.Black
            )
        }

        DropdownMenu(
            expanded = filterExpanded.value,
            onDismissRequest = { filterExpanded.value = false }
        ) {
            for (option in viewModel.getAllFilterOptions()) {
                DropdownMenuItem(
                    text = { Text(option.capitalize(Locale.current), color = textColor) },
                    modifier = Modifier.background(
                        color = if (viewModel.selectedFilterOptionsList.value.contains(
                                option
                            )
                        ) selectedColor else unselectedColor
                    ),
                    onClick = { viewModel.selectFilterOption(option) }
                )
            }
        }
    }
}

@Composable
private fun MakeSortButton(
    viewModel: AddToTeamViewModel,
    textColor: Color,
    selectedColor: Color,
    unselectedColor: Color
) {
    var sortExpanded = remember { mutableStateOf(false) }
    Button(
        onClick = { sortExpanded.value = true },
        colors = buttonColors(containerColor = Color(0xfff2f2f2)),
        modifier = Modifier.shadow(2.dp, CircleShape)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Sort",
                tint = textColor
            )
            Spacer(modifier = Modifier.padding(2.dp))

            Text(
                text = "Sort",
                color = Color.Black
            )
        }

        DropdownMenu(
            expanded = sortExpanded.value,
            onDismissRequest = { sortExpanded.value = false }
        ) {
            for (option in viewModel.getAllSortOptions()) {
                DropdownMenuItem(
                    text = { Text(option, color = textColor) },
                    modifier = Modifier.background(color = if (viewModel.selectedSortOption.value == option) selectedColor else unselectedColor),
                    onClick = {
                        sortExpanded.value = false
                        viewModel.selectSortOption(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun MakeSearchList(
    pokemons: List<Pokemon>,
    navController: NavController,
    viewModel: AddToTeamViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(pokemons) { pokemon ->
            SearchListItem(pokemon = pokemon, navController = navController, viewModel)
        }
    }
}

@Composable
private fun SearchListItem(pokemon: Pokemon, navController: NavController, viewModel: AddToTeamViewModel) {
    Button(
        onClick = {
            viewModel.addToTeam(pokemon, navController)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(3.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = buttonColors(
            containerColor = Color(0xfff2f2f2)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
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
}