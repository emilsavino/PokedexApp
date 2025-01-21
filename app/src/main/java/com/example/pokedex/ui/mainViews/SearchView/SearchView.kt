package com.example.pokedex.mainViews.SearchView

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
    viewModel: SearchViewModel = viewModel(),
    dismiss: (() -> Unit)? = null
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

    BackHandler {
        if (dismiss != null) {
            dismiss()
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
                .padding(2.dp)
                .verticalScrollbar(
                    scrollState = scrollState,
                    showScrollBarTrack = false,
                    scrollBarColor = Color.Gray
                )
                .verticalScroll(scrollState)
        ) {

            MakeSearchTools(viewModel, dismiss)

            Spacer(modifier = Modifier.height(16.dp))

            val searchText = viewModel.searchText.value
            val filterOptionz = viewModel.selectedFilterOptionsList.value
            val sortOption = viewModel.selectedSortOption.value

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
                    if (searchText.isEmpty() && filterOptionz.isEmpty() && sortOption.isEmpty()) {
                        Text(
                            text = viewModel.suggestionText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                        )
                    }
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
private fun MakeSearchTools(viewModel: SearchViewModel, dismiss: (() -> Unit)?) {
    MakeSearchBar(viewModel, dismiss)

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
private fun MakeSearchBar(viewModel: SearchViewModel, dismiss: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .shadow(0.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(24.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (dismiss != null) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp)
            ) {
                IconButton(
                    onClick = { dismiss() },
                    modifier = Modifier
                        .size(20.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        }

        TextField(
            modifier = Modifier.fillMaxWidth(),
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
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )
    }
}


@Composable
private fun MakeFilterButton(
    viewModel: SearchViewModel,
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
    viewModel: SearchViewModel,
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
            .padding(horizontal = 14.dp)
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