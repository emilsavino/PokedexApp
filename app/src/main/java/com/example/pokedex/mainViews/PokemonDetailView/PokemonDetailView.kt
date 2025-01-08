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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.pokedex.shared.BackButton
import com.example.pokedex.shared.Team
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon


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
    val showDialog by viewModel.showDialog.collectAsState()
    val selectedTeam by viewModel.selectedTeam.collectAsState()
    val newTeamName by viewModel.newTeamName.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val teams by viewModel.teams.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFDD99))
            .padding(16.dp)
    ) {
        CreateTopRow(navController, pokemon, viewModel) { viewModel.setShowDialog(true) }

        Spacer(modifier = Modifier.height(16.dp))

        CreatePokemonBox(pokemon)

        Spacer(modifier = Modifier.height(16.dp))

        CreateDescBox()

        Spacer(modifier = Modifier.height(8.dp))

        CreateTypeWeaknessBox()

        Spacer(modifier = Modifier.height(8.dp))

        CreateAbilitiesBox()

        Spacer(modifier = Modifier.height(8.dp))

        CreateEvoBox()

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(8.dp)
            )
        }
    }

    if (showDialog) {
        TeamSelectionDialog(
            teams = teams,
            onTeamSelected = { teamName ->
                viewModel.setSelectedTeam(teamName)
                viewModel.setShowDialog(false)
                coroutineScope.launch {
                    viewModel.confirmAddToTeam(pokemon)
                }
            },
            onCreateNewTeam = {
                viewModel.setShowDialog(false)
                viewModel.setNewTeamName("")
            },
            onDismiss = { viewModel.setShowDialog(false) }
        )
    }

    if (newTeamName.isNotEmpty()) {
        TeamCreationDialog(
            newTeamName = newTeamName,
            onTeamNameChange = { viewModel.setNewTeamName(it) },
            onCreateTeam = {
                coroutineScope.launch {
                    viewModel.createTeamWithPokemon(pokemon)
                }
                viewModel.setNewTeamName("")
            },
            onDismiss = { viewModel.setNewTeamName("") }
        )
    }
}

@Composable
fun TeamSelectionDialog(
    teams: List<Team>,
    onTeamSelected: (String) -> Unit,
    onCreateNewTeam: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Select Team") },
        text = {
            Column {
                if (teams.isEmpty()) {
                    Text(text = "No teams available")
                } else {
                    teams.forEach { team ->
                        TextButton(onClick = { onTeamSelected(team.name) }) {
                            Text(text = team.name)
                        }
                    }
                }
                TextButton(onClick = onCreateNewTeam) {
                    Text(text = "Create New Team")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
fun TeamCreationDialog(
    newTeamName: String,
    onTeamNameChange: (String) -> Unit,
    onCreateTeam: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Create New Team") },
        text = {
            Column {
                Text("Enter Team Name")
                OutlinedTextField(
                    value = newTeamName,
                    onValueChange = onTeamNameChange,
                    label = { Text("Team Name") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onCreateTeam) {
                Text(text = "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
fun CreateTopRow(
    navController: NavController,
    pokemon: Pokemon,
    viewModel: PokemonDetailViewModel,
    showDialog: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(navController)

        Text(
            text = pokemon.name.replaceFirstChar { it.uppercase() },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.savePokemon(pokemon)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = if (viewModel.isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (viewModel.isFavorited) "Remove" else "Add",
                tint = if (viewModel.isFavorited) Color.Red else Color.Black
            )
        }

        Button(onClick = showDialog, modifier = Modifier.padding(8.dp)) {
            Text(text = viewModel.teamButtonText)
        }
    }
}

@Composable
fun CreatePokemonBox(pokemon: Pokemon) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = pokemon.sprites.front_default,
            contentDescription = "Picture of a Pokémon",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun CreateDescBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Description",
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CreateTypeWeaknessBox() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Type",
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Weakness",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CreateAbilitiesBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Abilities",
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CreateEvoBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Evolutions",
            fontWeight = FontWeight.Bold
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