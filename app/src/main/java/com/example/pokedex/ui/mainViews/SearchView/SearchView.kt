package com.example.pokedex.mainViews.SearchView

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.R
import com.example.pokedex.ui.navigation.Screen
import com.example.pokedex.dataClasses.formatPokemonName
import com.example.pokedex.dataClasses.Pokemon
import com.example.pokedex.dataClasses.PokemonTypeResources
import com.example.pokedex.ui.shared.ProgressIndicator
import com.example.pokedex.dataClasses.getSprite
import com.example.pokedex.ui.shared.verticalScrollbar

@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    navController: NavController,
    filterOption: String,
    viewModel: SearchViewModel = viewModel()
) {
    val scrollState : ScrollState = rememberScrollState()
    val pokemons = viewModel.pokemonList.collectAsState().value
    LaunchedEffect(Unit) {
        viewModel.searchPokemonList()
        if (!filterOption.isEmpty())
        {
            viewModel.selectFilterOption(filterOption)
        }
    }
    var atBottom by remember { mutableStateOf(false) }
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }
            .collect { currentScrollPosition ->
                val isAtBottom = currentScrollPosition >= scrollState.maxValue
                atBottom = isAtBottom
            }
    }

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
                .verticalScroll(scrollState)
        ) {
            MakeSearchTools(viewModel = viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            when(pokemons) {
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
                    MakeSearchList(pokemons = pokemons.pokemonList, navController = navController, viewModel)
                    if (atBottom && (viewModel.searchText.value != "" || viewModel.selectedSortOption.value.isNotEmpty() || viewModel.selectedFilterOptionsList.value.isNotEmpty())) {
                        viewModel.searchOffset += 20
                        viewModel.searchPokemonList()
                        atBottom = false
                    }
                }
            }
        }
    }
}

@Composable
private fun MakeSearchTools(viewModel: SearchViewModel) {
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
private fun MakeSearchBar(viewModel: SearchViewModel) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .shadow(0.dp),

        placeholder = { Text(
            "Search...",
            color = Color.Black
        ) },

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
    viewModel: SearchViewModel,
    textColor: Color,
    selectedColor: Color,
    unselectedColor: Color
) {
    val typeResources = PokemonTypeResources()
    var filterExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val maxVisibleItems = 5

    Box(
        contentAlignment = Alignment.TopStart,
        modifier = Modifier.wrapContentSize()
    ) {
        // The Filter Button
        Button(
            onClick = { filterExpanded = !filterExpanded },
            colors = buttonColors(containerColor = Color.White),
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
        }

        if (filterExpanded) {
            Box(
                modifier = Modifier
                    .offset(y = 8.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                    .wrapContentSize()
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(max = (maxVisibleItems * 48).dp)
                        .verticalScrollbar(
                            scrollState = scrollState,
                            showScrollBarTrack = false,
                            scrollBarColor = Color.Gray
                        )
                        .verticalScroll(scrollState)
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
                                color = if (viewModel.selectedFilterOptionsList.value.contains(option))
                                    selectedColor else unselectedColor
                            ),
                            onClick = {
                                viewModel.selectFilterOption(option)
                                filterExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MakeSortButton(
    viewModel: SearchViewModel,
    textColor: Color,
    selectedColor: Color,
    unselectedColor: Color
) {
    var sortExpanded = remember { mutableStateOf(false) }
    val maxVisibleItems = 3
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
private fun MakeSearchList(pokemons: List<Pokemon>, navController: NavController, viewModel: SearchViewModel) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (pokemon in pokemons)
        {
            SearchListItem(pokemon = pokemon, navController = navController, viewModel)
        }
    }
}

@Composable
private fun SearchListItem(pokemon: Pokemon, navController: NavController, viewModel: SearchViewModel) {
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
                    onLongPress = { viewModel.onLongClick(pokemon, navController) },
                    onTap = { viewModel.onPokemonClicked(pokemon, navController) }
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

@Composable
private fun NoInternetView() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No Internet Connection",
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}