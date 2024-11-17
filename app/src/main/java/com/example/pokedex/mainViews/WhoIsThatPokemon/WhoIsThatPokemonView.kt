package com.example.pokedex.mainViews.WhoIsThatPokemon

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pokedex.mainViews.saved.SavedViewModel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

@Composable
fun WhoIsThatPokemonView(modifier: Modifier = Modifier, navController: NavController)
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
        Options(savedViewModel, navController)
    }

}
@Composable
fun Options (savedViewModel: SavedViewModel, navController: NavController)
{
    val whoIsThatPokemon = savedViewModel.savedState.collectAsState().value
    val options = listOf("DEAN","DEAN")
    if (options == null)
    {
        Text("DEEAN")
    }
    LazyColumn (modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    )
    {
        items(options) { item: String ->
            Option(name = item, navController = navController)
        }
    }
}

@Composable
fun Option(modifier : Modifier = Modifier, navController: NavController, name : String)
{
    Text(name)
}

@Preview (showBackground = true)
@Composable
fun WhoIsThatPokemonPreview()
{
    val navController = rememberNavController()
    WhoIsThatPokemonView(navController = navController)
}
