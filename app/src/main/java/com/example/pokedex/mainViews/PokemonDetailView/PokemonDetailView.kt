package com.example.pokedex.mainViews.PokemonDetailView

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.launch

@Composable
fun PokemonDetailView(pokemonName: String, navController: NavController) {
    val viewModel = viewModel<PokemonDetailViewModel>(key = pokemonName) { PokemonDetailViewModel(pokemonName) }
    val pokemon = viewModel.pokemon.collectAsState().value

    when (pokemon) {
        is PokemonDetailUIState.Empty -> {
            EmptyState()
        }
        is PokemonDetailUIState.Loading -> {
            LoadingState()
        }
        is PokemonDetailUIState.Data -> {
            PokemonDetail(navController, pokemon.pokemon, viewModel)
        }
    }
}

@Composable
fun PokemonDetail(navController: NavController, pokemon: Pokemon, viewModel: PokemonDetailViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf("") }
    var showTeamCreationDialog by remember { mutableStateOf(false) }
    var newTeamName by remember { mutableStateOf("") }
    val teams = viewModel.teams.collectAsState().value
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.size(500.dp)
        ) {
            AsyncImage(
                model = pokemon.sprites.front_default,
                contentDescription = "Picture of a Pokemon",
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            modifier = Modifier
                .padding(10.dp)
                .align(CenterHorizontally),
            text = pokemon.name,
            fontSize = 20.sp,
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.savePokemon(pokemon)
                }
            },
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(10.dp)
        ) {
            Text(text = viewModel.favouriteButtonText)
        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(10.dp)
        ) {
            Text(text = viewModel.teamButtonText)
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(10.dp)
        ) {
            Text(text = "Go back")
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = androidx.compose.ui.graphics.Color.Red,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(8.dp)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Select Team") },
            text = {
                Column {
                    if (teams.isEmpty()) {
                        Text(text = "No teams available")
                    } else {
                        for (team in teams) {
                            TextButton(onClick = { selectedTeam = team.name }) {
                                Text(text = team.name)
                            }
                        }
                    }
                    TextButton(onClick = {
                        showDialog = false
                        showTeamCreationDialog = true
                    }) {
                        Text(text = "Create New Team")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        coroutineScope.launch {
                            try {
                                if (selectedTeam.isNotEmpty()) {
                                    viewModel.addToTeam(pokemon, selectedTeam)
                                    errorMessage = null
                                }
                            } catch (e: IllegalStateException) {
                                errorMessage = e.message
                            }
                        }
                    }
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (showTeamCreationDialog) {
        AlertDialog(
            onDismissRequest = { showTeamCreationDialog = false },
            title = { Text(text = "Create New Team") },
            text = {
                Column {
                    Text("Enter Team Name")
                    androidx.compose.material3.OutlinedTextField(
                        value = newTeamName,
                        onValueChange = { newTeamName = it },
                        label = { Text("Team Name") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTeamCreationDialog = false
                        coroutineScope.launch {
                            if (newTeamName.isNotBlank()) {
                                viewModel.createNewTeam(pokemon, newTeamName)
                                newTeamName = ""
                            }
                        }
                    }
                ) {
                    Text(text = "Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTeamCreationDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (errorMessage != null) {
        Text(
            text = errorMessage!!,
            color = androidx.compose.ui.graphics.Color.Red,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(8.dp)
        )
    }



    if (showTeamCreationDialog) {
        AlertDialog(
            onDismissRequest = { showTeamCreationDialog = false },
            title = { Text(text = "Create New Team") },
            text = {
                Column {
                    Text("Enter Team Name")
                    androidx.compose.material3.OutlinedTextField(
                        value = newTeamName,
                        onValueChange = { newTeamName = it },
                        label = { Text("Team Name") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTeamCreationDialog = false
                        coroutineScope.launch {
                            if (newTeamName.isNotBlank()) {
                                viewModel.createNewTeam(pokemon, newTeamName)
                                newTeamName = ""
                            }
                        }
                    }
                ) {
                    Text(text = "Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTeamCreationDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}


@Composable
fun EmptyState() {
    Text(
        text = "No Pokemon found",
        fontSize = 20.sp
    )
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp)
        )
    }
}

