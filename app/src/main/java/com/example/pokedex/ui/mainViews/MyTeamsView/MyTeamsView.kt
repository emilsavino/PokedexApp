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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pokedex.ui.shared.PokemonGridItem
import com.example.pokedex.dataClasses.Team
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokedex.dataClasses.PokemonTypeResources
import com.example.pokedex.mainViews.SearchView.SearchView
import com.example.pokedex.ui.shared.AddToTeamGridItem
import com.example.pokedex.ui.shared.EmptyGridItem
import com.example.pokedex.ui.shared.NoInternetAlert
import com.example.pokedex.ui.shared.ProgressIndicator

@Composable
fun MyTeamsView(navController: NavController) {
    val viewModel = viewModel<MyTeamsViewModel>()

    if (viewModel.isShowingAddPokemon) {
        SearchView(
            navController = navController,
            filterOption = "",
            viewModel = viewModel.addToTeamViewModel!!
        )
    } else {
        Column(
            modifier = Modifier
                .background(PokemonTypeResources().appGradient())
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MakeHeader()

            MakeContent(navController, viewModel)

            Spacer(modifier = Modifier.padding(10.dp))

            if (viewModel.isShowingDialog) {
                DeleteTeamConfirmationDialog(viewModel.teamToEdit, viewModel)
            }

            if (viewModel.isShowingDeletePokemonDialog) {
                DeletePokemonConfirmationDialog(viewModel)
            }

            if (viewModel.showNoInternetAlert) {
                NoInternetAlert("add to a team", { viewModel.showNoInternetAlert = false })
            }
        }
    }
}

@Composable
private fun MakeHeader() {
    Text(
        text = "Teams",
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp),
    )

}

@Composable
private fun MakeContent(navController: NavController, viewModel: MyTeamsViewModel) {
    val teamsState = viewModel.teamsState.collectAsState().value

    when (teamsState) {
        is TeamsUIState.Empty -> {
            Text(text = "No teams found")
        }

        is TeamsUIState.Loading -> {
            ProgressIndicator()
        }

        is TeamsUIState.Data -> {
            MakeTeamsGrid(navController, teamsState.teams, viewModel)
        }
    }
}

@Composable
private fun MakeTeamsGrid(
    navController: NavController,
    teams: List<Team>,
    viewModel: MyTeamsViewModel
) {
    var teamNumber = 1
    for (team in teams) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Team $teamNumber: ")
                    }
                    withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(team.name)
                    }
                },
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                modifier = Modifier.padding(start = 8.dp),
                onClick = { viewModel.onDeleteTeamClicked(team.name) }
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(start = 16.dp)
        ) {
            val totalGridItems = 6
            val pokemonChunks = team.getPokemons().chunked(3)
            val displayedPokemonCount = team.getPokemons().size

            for ((index, chunk) in pokemonChunks.withIndex()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (pokemon in chunk) {
                        PokemonGridItem(
                            navController = navController,
                            pokemon = pokemon,
                            onLongClick = { viewModel.onLongClick(pokemon.name, team.name) }
                        )
                    }
                    if (index == pokemonChunks.lastIndex && chunk.size < 3) {
                        val remainingSlots = 3 - chunk.size
                        repeat(remainingSlots) {
                            if (displayedPokemonCount + it == displayedPokemonCount) {
                                AddToTeamGridItem( onClick = { viewModel.onAddPokemonClicked(team.name) })
                            } else {
                                EmptyGridItem(navController)
                            }
                        }
                    }
                }
            }

            val currentRowCount = pokemonChunks.size
            if (currentRowCount < 2) {
                repeat(2 - currentRowCount) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val remainingSlots = 3
                        for (slot in 1..remainingSlots) {
                            if (team.getPokemons().size == 3 && slot == 1) {
                                AddToTeamGridItem( onClick = { viewModel.onAddPokemonClicked(team.name) })
                            } else {
                                EmptyGridItem(navController)
                            }
                        }
                    }
                }
            }
        }

        teamNumber++
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

@Composable
private fun DeletePokemonConfirmationDialog(viewModel: MyTeamsViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.isShowingDeletePokemonDialog = false },
        title = { Text(text = "Are you sure you want to remove ${viewModel.pokemonToDelete} from ${viewModel.teamToEdit}? ") },
        text = {
            Text(text = "This action cannot be undone.")
        },
        confirmButton = {
            TextButton(onClick = { viewModel.deletePokemonFromTeam() }) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.isShowingDeletePokemonDialog = false }) {
                Text(text = "Cancel")
            }
        }
    )
}
