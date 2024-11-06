package com.example.pokedex.mainViews.myTeams

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonGridItem


@Composable
fun MyTeamsView(modifier: Modifier = Modifier) {
    val viewModel = MyTeamsViewModel()

    Column(
        modifier = Modifier
            .background(Color(0xFFFFDD99))
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MakeHeader()

        MakeContent(viewModel)
    }
}

@Composable
private fun MakeHeader() {
    Text(
        modifier = Modifier.padding(16.dp),
        text = "TEAMS",
        fontSize = 60.sp
    )
}

@Composable
private fun MakeContent(viewModel: MyTeamsViewModel) {
    val teams = viewModel.teamsState.collectAsState().value

    when (teams) {
        is TeamsUIState.Empty -> {
            Text(
                text = "No teams found",
                fontSize = 20.sp
            )
        }
        is TeamsUIState.Loading -> {
            Text(
                text = "Loading...",
                fontSize = 20.sp
            )
        }
        is TeamsUIState.Data -> {
            MakeTeamsGrid(teams.teams)
        }
    }
}

@Composable
private fun MakeTeamsGrid(teams: List<List<Pokemon>>) {
    var teamNumber = 1
    for (team in teams) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Team $teamNumber",
            fontSize = 20.sp
        )
        teamNumber++
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            for (pokemons in team.chunked(3)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (pokemon in pokemons) {
                        PokemonGridItem(pokemon = pokemon)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyTeamsViewPreview() {
    MyTeamsView()
}