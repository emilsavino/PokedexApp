package com.example.pokedex.mainViews.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonGridItem

@Composable
fun SavedView(modifier: Modifier = Modifier)
{
    val savedViewModel = SavedViewModel()
    Column(
        modifier = modifier
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = "Favorites",
            fontSize = 60.sp,
            modifier = modifier.padding(10.dp)
        )
        SavedList(savedViewModel)
    }

}
@Composable
fun SavedList (savedViewModel: SavedViewModel)
{
    val pokemons = savedViewModel.savedState.collectAsState().value
    val pokemonsChunked = pokemons.chunked(3)
    LazyColumn (modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        )
    {
        items(pokemonsChunked) { item: List<Pokemon> ->
            SavedRow(pokemons = item)
        }
    }
}

@Composable
fun SavedRow(modifier: Modifier = Modifier, pokemons : List<Pokemon>)
{
    Row (modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(19.dp)
    )
    {
        for (pokemon in pokemons)
        {
            PokemonGridItem(pokemon = pokemon)
        }
    }

}

@Preview (showBackground = true)
@Composable
fun SavedViewPreview()
{
    SavedView()
}