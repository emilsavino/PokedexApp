package com.example.pokedex.mainViews.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(modifier: Modifier = Modifier) {
    val pokemonRepository = remember { PokemonRepository() }
    val viewModel = remember { SearchViewModel(pokemonRepository) }

    val searchText by viewModel.searchText.collectAsState()
    val pokemons by viewModel.pokemons.collectAsState(initial = emptyList())

    val coroutineScope = rememberCoroutineScope()

    val textSelectionColors = TextSelectionColors(
        handleColor = Color.Black,
        backgroundColor = Color.LightGray
    )

    val customTextFieldColors = TextFieldColors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        disabledTextColor = Color.Gray,
        errorTextColor = Color.Red,

        focusedContainerColor = Color(0xfff2f2f2),
        unfocusedContainerColor = Color(0xfff2f2f2),
        disabledContainerColor = Color.LightGray,
        errorContainerColor = Color(0xfff2f2f2),

        cursorColor = Color.Black,
        errorCursorColor = Color.Red,
        textSelectionColors = textSelectionColors,

        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Red,

        focusedLeadingIconColor = Color.Black,
        unfocusedLeadingIconColor = Color.Black,
        disabledLeadingIconColor = Color.Gray,
        errorLeadingIconColor = Color.Red,

        focusedTrailingIconColor = Color.Black,
        unfocusedTrailingIconColor = Color.Black,
        disabledTrailingIconColor = Color.Gray,
        errorTrailingIconColor = Color.Red,

        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        disabledLabelColor = Color.Gray,
        errorLabelColor = Color.Red,

        focusedPlaceholderColor = Color.Black,
        unfocusedPlaceholderColor = Color.Black,
        disabledPlaceholderColor = Color.Gray,
        errorPlaceholderColor = Color.Red,

        focusedSupportingTextColor = Color.Black,
        unfocusedSupportingTextColor = Color.Black,
        disabledSupportingTextColor = Color.Gray,
        errorSupportingTextColor = Color.Red,

        focusedPrefixColor = Color.Black,
        unfocusedPrefixColor = Color.Black,
        disabledPrefixColor = Color.Gray,
        errorPrefixColor = Color.Red,

        focusedSuffixColor = Color.Black,
        unfocusedSuffixColor = Color.Black,
        disabledSuffixColor = Color.Gray,
        errorSuffixColor = Color.Red
    )

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
                colors = customTextFieldColors
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Add filter logic */ },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xfff2f2f2))
                ) {
                    Text(
                        text = "Filter",
                        color = Color.Black
                    )
                }
                Button(
                    onClick = { /* Add sort logic */ },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xfff2f2f2))
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
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xfff2f2f2)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = pokemon.imageURL,
                                contentDescription = "${pokemon.name} Image",
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(end = 2.dp)
                            )
                            Text(
                                text = pokemon.name,
                                color = Color.Black,
                                fontSize = TextUnit(16F, TextUnitType.Unspecified)
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
    SearchView()
}
