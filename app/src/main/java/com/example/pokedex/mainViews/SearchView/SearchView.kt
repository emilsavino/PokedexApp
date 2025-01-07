package com.example.pokedex.mainViews.SearchView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
            .background(Color.Transparent)
            .shadow(0.dp)
            .clip(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        MakeFilterOrSortButton("Filter")

        Spacer(modifier = Modifier.padding(10.dp))

        MakeFilterOrSortButton("Sort")
    }
}

//Dog water method, but it works
@Composable
private fun MakeFilterOrSortButton(name: String) {
    Button(
        onClick = {  },
        colors = buttonColors(containerColor = Color(0xfff2f2f2))
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (name.contains("Filter"))
                    Icons.Filled.List else Icons.Filled.MoreVert,
                contentDescription = name,
                tint = Color.Black
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = name,
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
                text = FormatPokemonName(pokemon.name),
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun FormatPokemonName(name: String): String {
    return name.split("-")
        .joinToString (" "){ word -> word.replaceFirstChar { it.uppercase() } }
}

@Preview(showBackground = true)
@Composable
fun SearchViewPreview() {
    val navController = rememberNavController()
    SearchView(navController = navController)
}
