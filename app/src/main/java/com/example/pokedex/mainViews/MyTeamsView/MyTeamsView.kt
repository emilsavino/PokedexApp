package com.example.pokedex.mainViews.MyTeamsView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pokedex.shared.PokemonGridItem
import com.example.pokedex.shared.Team
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MyTeamsView(navController: NavController) {
    val viewModel = viewModel<MyTeamsViewModel>()
    Column(
        modifier = Modifier
            .background(Color(0xFFFFDD99))
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MakeHeader()

        MakeContent(navController, viewModel)

        Spacer(modifier = Modifier.padding(10.dp))

        if (viewModel.isShowingDialog) {
            DeleteTeamConfirmationDialog(viewModel.teamToDelete, viewModel)
        }
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
private fun MakeContent(navController: NavController, viewModel: MyTeamsViewModel) {
    val teamsState = viewModel.teamsState.collectAsState().value

    when (teamsState) {
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
            MakeTeamsGrid(navController, teamsState.teams, viewModel)
        }
    }
}

@Composable
private fun MakeTeamsGrid(navController: NavController, teams: List<Team>, viewModel: MyTeamsViewModel) {
    var teamNumber = 1
    for (team in teams) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "Team $teamNumber: ${team.name}",
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                modifier = Modifier.padding(start = 8.dp),
                onClick = { viewModel.onDeleteTeam(team.name) }
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }

        teamNumber++

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(start = 16.dp)
        ) {
            for (pokemons in team.pokemons.chunked(3)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (pokemon in pokemons) {
                        PokemonGridItem(navController, pokemon = pokemon)
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteTeamConfirmationDialog(teamName: String, viewModel: MyTeamsViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.isShowingDialog = false },
        title = { Text(text = "Are you sure you want to delete ${teamName}?") },
        text = {
            Text(text = "This action cannot be undone.")
        },
        confirmButton = {
            TextButton(onClick = { viewModel.deleteTeam(teamName) }) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.isShowingDialog = false }) {
                Text(text = "Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MyTeamsViewPreview() {
    val navController = rememberNavController()
    MyTeamsView(navController = navController)
}
