package com.example.pokedex.mainViews.SearchView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.pokedex.navigation.Screen
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.Result

@Composable
fun SearchView(modifier: Modifier = Modifier, navController: NavController) {
    val viewModel = viewModel<SearchViewModel>()

    val pokemons by viewModel.pokemonList.collectAsState()




    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFDD99)),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            MakeSearchTools(viewModel = viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            MakeSearchList(pokemons = pokemons, navController = navController)
        }
    }
}

@Composable
fun MakeSearchTools(viewModel: SearchViewModel) {
    var filterExpanded = remember { mutableStateOf(false) }
    TextField(
        value = viewModel.searchText.value,
        onValueChange = {
            viewModel.searchText.value = it
            viewModel.searchPokemonList()
        },

        placeholder = { Text(
            "Search...",
            color = Color.Black
        ) },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        shape = RoundedCornerShape(24.dp),
        maxLines = 1,
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val selectedColor = Color(0xFF0000FF)
        val unselectedColor = Color(0x00FF00FF)
        val textColor = Color.Black
        Button(
            onClick = { filterExpanded.value = true},
            colors = buttonColors(containerColor = Color(0xfff2f2f2))
        ) {
            Text(
                text = "Filter",
                color = Color.Black
            )
            DropdownMenu(
                expanded = filterExpanded.value,
                onDismissRequest = { filterExpanded.value = false }
            ) {
                val selectedOptions = viewModel.selectedOptionsFlow.collectAsState()
                for (option in viewModel.getAllFilterOptions())
                {
                    DropdownMenuItem(
                        text = { Text(option, color = textColor) },
                        modifier = Modifier.background(color = if (selectedOptions.value.contains(option)) selectedColor else unselectedColor),
                        onClick = { viewModel.selectOption(option) }
                    )
                }
            }
        }
        Button(
            onClick = {  },
            colors = buttonColors(containerColor = Color(0xfff2f2f2))
        ) {
            Text(
                text = "Sort",
                color = Color.Black
            )
        }
    }
}

@Composable
fun MakeSearchList(pokemons: List<Result>, navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(pokemons) { pokemon ->
            SearchListItem(pokemon = pokemon, navController = navController)
        }
    }
}

@Composable
fun SearchListItem(pokemon: Result, navController: NavController) {
    Button(
        onClick = { navController.navigate(Screen.PokemonDetails.createRoute(pokemon.name)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = buttonColors(
            containerColor = Color(0xfff2f2f2)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            /*AsyncImage(
                model = pokemon.sprites,
                contentDescription = "${pokemon.name} Image",
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 2.dp)
            )*/
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchViewPreview() {
    val navController = rememberNavController()
    SearchView(navController = navController)
}
