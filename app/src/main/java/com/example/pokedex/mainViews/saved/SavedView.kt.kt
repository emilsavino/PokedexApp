package com.example.pokedex.mainViews.saved

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.pokedex.mainViews.myTeams.MyTeamsViewModel
import com.example.pokedex.mainViews.myTeams.TeamsUIState
import com.example.pokedex.shared.Pokemon

@Composable
fun SavedView(modifier: Modifier = Modifier)
{
    val savedViewModel = SavedViewModel()
    val pokemons : SavedUIState = savedViewModel.savedState.collectAsState().value
    Column(
        modifier = modifier
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = "DEAN")
        MakeContent(savedViewModel)
    }

}

@Composable
private fun MakeContent(viewModel: SavedViewModel) {
    val savedUIState = viewModel.savedState.collectAsState().value

    when (savedUIState) {
        is SavedUIState.Empty -> {
            Text(
                text = "No teams found",
                fontSize = 20.sp
            )
        }
        is SavedUIState.Loading -> {
            Text(
                text = "Loading...",
                fontSize = 20.sp
            )
        }
        is SavedUIState.Data -> {
            SavedRow(savedUIState.saved)
        }
    }
}

@Composable
fun SavedRow(pokemons : List<Pokemon>)
{
    for (pokemon in pokemons)
    {
        println(pokemon.name)
        Text(text = pokemon.name)
    }
}

@Preview (showBackground = true)
@Composable
fun SavedViewPreview()
{
    SavedView()
}