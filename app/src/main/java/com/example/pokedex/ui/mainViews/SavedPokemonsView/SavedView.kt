package com.example.pokedex.mainViews.SavedPokemonsView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pokedex.dataClasses.Pokemon
import com.example.pokedex.dataClasses.PokemonTypeResources
import com.example.pokedex.mainViews.SavedPokemonsView.SavedViewModel.SavedUIState
import com.example.pokedex.mainViews.SearchView.SearchViewModel
import com.example.pokedex.ui.shared.PokemonGridItem
import com.example.pokedex.ui.shared.ProgressIndicator
import com.example.pokedex.ui.shared.verticalScrollbar

@Composable
fun SavedView(navController: NavController) {
    val viewModel = viewModel<SavedViewModel>()
    val savedState = viewModel.savedState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PokemonTypeResources().appGradient())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Saved Pokemons",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
        )

        when (savedState) {
            is SavedUIState.Empty -> {
                MakeSavedTools(viewModel)
                Text(text = "No saved pokemons")
            }

            is SavedUIState.Loading -> {
                ProgressIndicator()
            }

            is SavedUIState.Data -> {
                if (savedState.saved.isEmpty()) {
                    Text(text = "No saved pokemons")
                    viewModel.savedIsEmpty()
                    MakeSavedTools(viewModel)
                } else {
                    MakeSavedTools(viewModel)
                    SavedList(navController, savedState.saved)
                }
            }
        }
    }
}

@Composable
private fun MakeSavedTools(
    viewModel: SavedViewModel,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val selectedColor = Color(0x636363FF)
        val unselectedColor = Color(0x00FF00FF)
        val textColor = Color.Black

        MakeFilterButton(
            viewModel,
            textColor,
            selectedColor,
            unselectedColor
        )

        Spacer(modifier = Modifier.padding(10.dp))

        MakeSortButton(
            viewModel,
            textColor,
            selectedColor,
            unselectedColor
        )
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun MakeFilterButton(
    viewModel: SavedViewModel,
    textColor: Color,
    selectedColor: Color,
    unselectedColor: Color
) {
    val typeResources = PokemonTypeResources()
    var filterExpanded = remember { mutableStateOf(false) }
    var maxVisibleItems = 5
    val scrollState = rememberScrollState()
    Button(
        onClick = { filterExpanded.value = true},
        colors = buttonColors(containerColor = Color.White),
        modifier = Modifier.shadow(2.dp, CircleShape)
    ) {
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
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
            onDismissRequest = { filterExpanded.value = false },
            modifier = Modifier
                .height((maxVisibleItems * 48).dp)
                .background(Color.White)
                .verticalScrollbar(
                    scrollState = scrollState,
                    showScrollBarTrack = false,
                    scrollBarColor = Color.Gray
                ),
            scrollState = scrollState
        ) {
            val allFilterOptions = viewModel.getAllFilterOptions()
            for (option in allFilterOptions) {
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = typeResources.getTypeImage(option),
                                contentDescription = "${option.capitalize()} type icon",
                                modifier = Modifier.size(24.dp).padding(end = 8.dp)
                            )
                            Text(option.capitalize(), color = textColor)
                        }
                    },
                    modifier = Modifier.background(
                        color = if (viewModel.selectedFilterOptionsList.value.contains(option)) selectedColor else unselectedColor
                    ),
                    onClick = {
                        viewModel.selectFilterOption(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun MakeSortButton(
    viewModel: SavedViewModel,
    textColor: Color,
    selectedColor: Color,
    unselectedColor: Color
) {
    var sortExpanded = remember { mutableStateOf(false) }
    val maxVisibleItems = 5
    val scrollState = rememberScrollState()
    Button(
        onClick = { sortExpanded.value = true },
        colors = buttonColors(containerColor = Color.White),
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
            onDismissRequest = { sortExpanded.value = false },
            modifier = Modifier
                .height((maxVisibleItems * 48).dp)
                .background(Color.White)
                .verticalScrollbar(
                    scrollState = scrollState,
                    showScrollBarTrack = false,
                    scrollBarColor = Color.Gray
                ),
            scrollState = scrollState
        ) {
            val allSortOptions = viewModel.getAllSortOptions()
            for (option in allSortOptions) {
                DropdownMenuItem(
                    text = { Text(option, color = textColor) },
                    modifier = Modifier.background(
                        color = if (viewModel.selectedSortOption.value == option) selectedColor else unselectedColor
                    ),
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
private fun SavedList(navController: NavController, savedPokemons: List<Pokemon>) {
    val pokemonsChunked = savedPokemons.chunked(3)
    LazyColumn(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(pokemonsChunked) { item ->
            SavedRow(pokemons = item, navController = navController)
        }
    }
}

@Composable
private fun SavedRow(modifier: Modifier = Modifier, pokemons: List<Pokemon>, navController: NavController) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(19.dp)
    ) {
        for (pokemon in pokemons) {
            PokemonGridItem(navController, pokemon = pokemon)
        }
    }
}