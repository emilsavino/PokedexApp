package com.example.pokedex.mainViews.SearchView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.pokedex.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun SearchView(modifier: Modifier = Modifier, navController: NavController) {
    val viewModel = remember { SearchViewModel() }

    val searchText by viewModel.searchText.collectAsState()
    val pokemons by viewModel.pokemons.collectAsState(initial = emptyList())

    val coroutineScope = rememberCoroutineScope()

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
            TextField(
                value = searchText,
                onValueChange = { viewModel.updateSearchText(it) },
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
                Button(
                    onClick = { /* Add filter logic */ },
                    colors = buttonColors(containerColor = Color(0xfff2f2f2))
                ) {
                    Text(
                        text = "Filter",
                        color = Color.Black
                    )
                }
                Button(
                    onClick = { /* Add sort logic */ },
                    colors = buttonColors(containerColor = Color(0xfff2f2f2))
                ) {
                    Text(
                        text = "Sort",
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(pokemons) { pokemon ->
                    Button(
                        onClick = { /* Add Pokémon click logic */ },
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate(Screen.PokemonDetails.createRoute(pokemon.name)) }
                        ) {
                            AsyncImage(
                                model = pokemon.imageURL,
                                contentDescription = "${pokemon.name} Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(end = 2.dp)
                            )
                            Text(
                                text = pokemon.name,
                                color = Color.Black,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            viewModel.fetchMorePokemons()
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchViewPreview() {
    val navController = rememberNavController()
    SearchView(navController = navController)
}
