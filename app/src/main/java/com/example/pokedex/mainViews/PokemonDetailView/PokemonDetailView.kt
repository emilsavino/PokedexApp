package com.example.pokedex.mainViews.PokemonDetailView

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.shared.Pokemon
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.pokedex.R
import com.example.pokedex.shared.BackButton
import com.example.pokedex.shared.formatPokemonName
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonAttributes
import com.example.pokedex.shared.Team
import kotlinx.coroutines.CoroutineScope
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
fun PokemonDetailContent(navController: NavController, pokemon: PokemonAttributes, viewModel: PokemonDetailViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFDD99))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CreateTopRow(navController, pokemon, viewModel) { viewModel.showDialog = true }

        Spacer(modifier = Modifier.height(16.dp))

        CreatePokemonBox(pokemon)

        Spacer(modifier = Modifier.height(16.dp))

        CreateDescBox(pokemon)

        Spacer(modifier = Modifier.height(8.dp))

        CreateTypeWeaknessBox(pokemon)

        Spacer(modifier = Modifier.height(8.dp))

        CreateAbilitiesBox(pokemon)

        Spacer(modifier = Modifier.height(8.dp))

        CreateEvoBox(pokemon)
    }
}

@Composable
fun CreateEvoBox(pokemon: PokemonAttributes) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Evolutions",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Text(
                text = pokemon.sprites.toString() //TODO: Make use of the link
            )
        }

    }
}

@Composable
fun CreateAbilitiesBox(pokemon: PokemonAttributes) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Abilities",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp

            )
            Spacer(modifier = Modifier.padding(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                pokemon.abilities.forEachIndexed { index, ability ->
                    Text(
                        text = "Ability ${index + 1}: ",
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 16.sp
                    )

                    Text(
                        text = ability.ability.name.formatPokemonName(),
                        fontStyle = FontStyle.Italic,
                        fontSize = 16.sp
                    )

                    if (index < pokemon.abilities.size - 1) {
                        Text(
                            text = "   |   ",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun ErrorMessage(errorMessage: String) {
    Text(
        text = errorMessage,
        color = Color.Red,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(CenterHorizontally)
            .padding(8.dp)
    )
}

@Composable
fun CreateTypeWeaknessBox(pokemon: PokemonAttributes) {
    val typeImageMap: Map<String, Painter> = mapOf(
        "bug" to painterResource(id = R.drawable.bug),
        "dark" to painterResource(id = R.drawable.dark),
        "dragon" to painterResource(id = R.drawable.dragon),
        "electric" to painterResource(id = R.drawable.electric),
        "fairy" to painterResource(id = R.drawable.fairy),
        "fighting" to painterResource(id = R.drawable.fighting),
        "fire" to painterResource(id = R.drawable.fire),
        "flying" to painterResource(id = R.drawable.flying),
        "ghost" to painterResource(id = R.drawable.ghost),
        "grass" to painterResource(id = R.drawable.grass),
        "ground" to painterResource(id = R.drawable.ground),
        "ice" to painterResource(id = R.drawable.ice),
        "normal" to painterResource(id = R.drawable.normal),
        "poison" to painterResource(id = R.drawable.poison),
        "psychic" to painterResource(id = R.drawable.psychic),
        "rock" to painterResource(id = R.drawable.rock),
        "steel" to painterResource(id = R.drawable.steel),
        "water" to painterResource(id = R.drawable.water)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(end = 8.dp)
                .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Types",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.padding(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    pokemon.types.types.forEach { type ->
                        val typeImage = typeImageMap[type.name]
                        typeImage?.let {
                            Image(
                                painter = it,
                                contentDescription = "${type.name} type image",
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 8.dp)
                .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Weaknesses",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.padding(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    pokemon.weaknesses.double_damage_from.forEach { weakness ->
                        val weaknessImage = typeImageMap[weakness.name]
                        weaknessImage?.let {
                            Image(
                                painter = it,
                                contentDescription = "${weakness.name} weakness image",
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateDescBox(pokemon: PokemonAttributes) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Description",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Text(
                text = pokemon.description.flavor_text.replace("\n", " "),
                fontStyle = FontStyle.Italic
            )
        }

    TeamSelectionAndCreationDialogs(pokemon, viewModel)
    }
}

@Composable
fun CreatePokemonBox(pokemon: PokemonAttributes) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = pokemon.pokemon.sprites.front_default,
            contentDescription = "Picture of a Pokémon",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun CreateTopRow(
    navController: NavController,
    pokemon: PokemonAttributes,
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
            text = pokemon.pokemon.name.formatPokemonName(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        CreateSmallButton(
            imageVector = Icons.Default.AddCircle,
            color = Color.Black,
            contentDescription = "Add to Team",
            onClick = { showDialog() }
        )

        CreateSmallButton(
            imageVector = if (viewModel.isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            color = if (viewModel.isFavorited) Color.Red else Color.Black,
            contentDescription = if (viewModel.isFavorited) "Remove" else "Add",
            onClick = {
                coroutineScope.launch {
                    viewModel.savePokemon(pokemon.pokemon)
                }
            }
        )
    }
}

@Composable
fun CreateSmallButton(imageVector: ImageVector, color: Color, contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(6.dp)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier.size(34.dp)
        )
    }
}

@Composable
fun TeamSelectionAndCreationDialogs(
    pokemon: Pokemon,
    viewModel: PokemonDetailViewModel,
) {
    if (viewModel.showDialog) {
        TeamSelectionDialog(
            teams = viewModel.teams.collectAsState().value,
            viewModel = viewModel,
            onTeamSelected = { teamName -> viewModel.addToTeam(pokemon, teamName) },
            onCreateNewTeam = { viewModel.onCreateNewTeam() },
            onDismiss = { viewModel.onDismiss() }
        )
    }

    if (viewModel.showTeamCreationDialog) {
        TeamCreationDialog(
            newTeamName = viewModel.newTeamName,
            viewModel = viewModel,
            onTeamNameChange = { viewModel.newTeamName = it },
            onCreateTeam = { viewModel.onCreateTeamClicked(pokemon) },
            onDismiss = { viewModel.onCancelTeamCreation() }
        )
    }
}

@Composable
fun TeamSelectionContent(
    teams: List<Team>,
    viewModel: PokemonDetailViewModel,
    onTeamSelected: (String) -> Unit,
    onCreateNewTeam: () -> Unit
) {
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
        Text(
            text = viewModel.errorMessage ?: "", color = Color.Red,
            modifier = Modifier.align(CenterHorizontally)
        )
    }
}

@Composable
fun TeamSelectionDialog(
    teams: List<Team>,
    viewModel: PokemonDetailViewModel,
    onTeamSelected: (String) -> Unit,
    onCreateNewTeam: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Select Team") },
        text = {
            TeamSelectionContent(
                teams = teams,
                viewModel = viewModel,
                onTeamSelected = onTeamSelected,
                onCreateNewTeam = onCreateNewTeam
            )
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
fun TeamCreationContent(
    newTeamName: String,
    onTeamNameChange: (String) -> Unit,
    viewModel: PokemonDetailViewModel
) {
    Column {
        Text("Enter Team Name")
        OutlinedTextField(
            value = newTeamName,
            onValueChange = onTeamNameChange,
            label = { Text("Team Name") }
        )
        Text(
            text = viewModel.errorMessage ?: "", color = Color.Red,
            modifier = Modifier.align(CenterHorizontally)
        )
    }
}

@Composable
fun TeamCreationDialog(
    newTeamName: String,
    viewModel: PokemonDetailViewModel,
    onTeamNameChange: (String) -> Unit,
    onCreateTeam: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Create New Team") },
        text = {
            TeamCreationContent(
                newTeamName = newTeamName,
                onTeamNameChange = onTeamNameChange,
                viewModel = viewModel
            )
        },
        confirmButton = {
            TextButton(onClick = { onCreateTeam() }) {
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(navController)

        Text(
            text = pokemon.name.formatPokemonName(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        CreateSmallButton(
            imageVector = Icons.Default.AddCircle,
            color = Color.Black,
            contentDescription = "Add to Team",
            onClick = { showDialog() }
        )

        CreateSmallButton(
            imageVector = if (viewModel.isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            color = if (viewModel.isFavorited) Color.Red else Color.Black,
            contentDescription = if (viewModel.isFavorited) "Remove" else "Add",
            onClick = { viewModel.savePokemon(pokemon) }
        )
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
fun CreateSmallButton(imageVector: ImageVector, color: Color, contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(6.dp)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier.size(34.dp)
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
fun AttributeBoxColumns(text1: String, text2: String) {
    Column {
        Text(
            text = text1,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Text(
            text = text2
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