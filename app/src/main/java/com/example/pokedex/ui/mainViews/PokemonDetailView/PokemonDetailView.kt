package com.example.pokedex.mainViews.PokemonDetailView

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.dataClasses.Pokemon
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.example.pokedex.ui.navigation.Screen
import com.example.pokedex.ui.shared.BackButton
import com.example.pokedex.dataClasses.formatPokemonName
import com.example.pokedex.dataClasses.PokemonAttributes
import com.example.pokedex.dataClasses.PokemonTypeResources
import com.example.pokedex.dataClasses.Team
import com.example.pokedex.dataClasses.Type
import com.example.pokedex.dataClasses.getSprite
import com.example.pokedex.ui.shared.horizontalScrollBar

val typeResources = PokemonTypeResources()

@Composable
fun PokemonDetailView(
    pokemonName: String,
    navController: NavController
) {
    val viewModel = viewModel<PokemonDetailViewModel>()
    LaunchedEffect(Unit) {
        viewModel.currentPokemonName = pokemonName
        viewModel.getPokemonByName()
    }
    BackHandler { viewModel.navigateToPrevious(navController) }

    when (val pokemon = viewModel.pokemon.collectAsState().value) {
        is PokemonDetailUIState.Empty -> {
            EmptyState()
        }

        is PokemonDetailUIState.Loading -> {
            LoadingState()
        }

        is PokemonDetailUIState.Data -> {
            PokemonDetailContent(navController, pokemon.pokemon, viewModel)
        }
    }
}

@Composable
private fun EmptyState() {
    Text(
        text = "No Pokemon found",
        fontSize = 20.sp
    )
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp)
        )
    }
}

@Composable
private fun PokemonDetailContent(
    navController: NavController,
    pokemon: PokemonAttributes,
    viewModel: PokemonDetailViewModel
) {

    val primaryType = pokemon.types.types.firstOrNull()?.name ?: "normal"
    val gradientBrush = typeResources.getTypeGradient(primaryType)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CreateTopRow(navController, pokemon, viewModel) { viewModel.showDialog = true }

        Spacer(modifier = Modifier.height(16.dp))

        CreatePokemonBox(pokemon)

        Spacer(modifier = Modifier.height(16.dp))

        CreateDescBox(pokemon)

        Spacer(modifier = Modifier.height(8.dp))

        CreateTypeWeaknessBox(pokemon, navController)

        Spacer(modifier = Modifier.height(8.dp))

        CreateAbilitiesBox(pokemon)

        Spacer(modifier = Modifier.height(8.dp))

        CreateEvoBox(pokemon, viewModel)

        TeamSelectionAndCreationDialogs(pokemon.pokemon, viewModel)
    }
}

@Composable
private fun CreateTopRow(
    navController: NavController,
    pokemon: PokemonAttributes,
    viewModel: PokemonDetailViewModel,
    showDialog: () -> Unit
) {
    val textColor = if (pokemon.types.types.firstOrNull()?.name == "dark" ) {
        Color.LightGray
    } else {
        Color.Black
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(
            navController = navController,
            onClick = { viewModel.navigateToPrevious(navController) }
        )

        Text(
            text = pokemon.pokemon.name.formatPokemonName(),
            fontSize = 24.sp,
            color = textColor,
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
            onClick = { viewModel.savePokemon(pokemon.pokemon) }
        )
    }
}

@Composable
private fun CreateSmallButton(
    imageVector: ImageVector,
    color: Color,
    contentDescription: String,
    onClick: () -> Unit
) {

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
private fun CreatePokemonBox(pokemon: PokemonAttributes) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = pokemon.pokemon.getSprite(),
            contentDescription = "Picture of a Pokémon",
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(TopStart)
                .padding(2.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            CreateStatsText("hp", pokemon, pokemon.types.types)
            CreateStatsText("speed", pokemon, pokemon.types.types)
        }

        Column(
            modifier = Modifier
                .align(BottomEnd)
                .padding(2.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            CreateStatsText("attack", pokemon, pokemon.types.types)
            CreateStatsText("defense", pokemon, pokemon.types.types)
        }
    }
}

@Composable
fun CreateStatsText(nameOfStat: String, pokemon: PokemonAttributes, types: List<Type>) {
    val textColor = if (types.firstOrNull()?.name == "dark") {
        Color.LightGray
    } else {
        Color.Black
    }

    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                append("${nameOfStat.uppercase()}: ")
            }
            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic, fontSize = 16.sp)) {
                append("${pokemon.pokemon.stats.firstOrNull { it.stat.name == nameOfStat.lowercase() }?.base_stat ?: "N/A"}")
            }
        },
        style = TextStyle(
            color = textColor
        ),
        modifier = Modifier
            .padding(2.dp)
    )
}

@Composable
private fun CreateDescBox(pokemon: PokemonAttributes) {
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
    }
}

@Composable
private fun CreateTypeWeaknessBox(pokemon: PokemonAttributes, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            CreateTypeBox(pokemon, navController)
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            CreateWeaknessBox(pokemon, navController)
        }
    }
}

@Composable
private fun CreateTypeBox(pokemon: PokemonAttributes, navController: NavController) {
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .fillMaxSize()
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
                    .horizontalScroll(rememberScrollState())
            ) {
                pokemon.types.types.forEach { type ->
                    val typeImage = typeResources.getTypeImage(type.name)
                    Image(
                        painter = typeImage,
                        contentDescription = "${type.name} type image",
                        modifier = Modifier
                            .clickable { navController.navigate(Screen.Search.createRoute(type.name)) }
                            .size(60.dp)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateWeaknessBox(pokemon: PokemonAttributes, navController: NavController) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .padding(start = 8.dp)
            .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .fillMaxSize()
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
                    .horizontalScrollBar(
                        scrollState = scrollState,
                        height = 2.dp,
                        showScrollBarTrack = false,
                        scrollBarColor = Color.Gray,
                    )
                    .horizontalScroll(scrollState)
                    .defaultMinSize(minWidth = 149.dp)
            ) {
                pokemon.weaknesses.double_damage_from.forEach { weakness ->
                    val weaknessImage = typeResources.getTypeImage(weakness.name)
                    Image(
                        painter = weaknessImage,
                        contentDescription = "${weakness.name} weakness image",
                        modifier = Modifier
                            .clickable { navController.navigate(Screen.Search.createRoute(weakness.name)) }
                            .size(60.dp)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateAbilitiesBox(pokemon: PokemonAttributes) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = CenterHorizontally
        ) {

            Text(
                text = "Abilities",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.Start)
            )

            val abilities = pokemon.abilities
            if (abilities.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = when (abilities.size) {
                        1 -> Arrangement.Center
                        2 -> Arrangement.SpaceEvenly
                        else -> Arrangement.SpaceAround
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    abilities.forEachIndexed { index, ability ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (ability.is_hidden) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Hidden Ability",
                                    modifier = Modifier
                                        .size(14.dp)
                                        .padding(end = 4.dp)
                                )
                            }
                            Text(
                                text = ability.ability.name.formatPokemonName(),
                                fontSize = 16.sp
                            )
                        }

                        if (index < abilities.size - 1) {
                            Text(
                                text = "|",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
            Text(
                text = "★ is a hidden ability",
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun CreateEvoBox(
    pokemon: PokemonAttributes,
    viewModel: PokemonDetailViewModel
) {
    val scrollState = rememberScrollState()
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

            if (pokemon.pokemons.isEmpty()) {
                Text(
                    text = viewModel.getEmptyEvoText(),
                    color = Color.Black,
                    fontStyle = FontStyle.Italic
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScrollBar(
                            scrollState = scrollState,
                            height = 2.dp,
                            showScrollBarTrack = false,
                            scrollBarColor = Color.Gray,
                        )
                        .horizontalScroll(scrollState)
                ) {
                    pokemon.pokemons.forEachIndexed { index, localPokemon ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.navigateToEvo(localPokemon.name) }
                        ) {
                            AsyncImage(
                                model = localPokemon.getSprite(),
                                contentDescription = "Sprite",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .scale(1.2f),
                                contentScale = ContentScale.Fit
                            )
                        }

                        if (index < pokemon.pokemons.size - 1) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Arrow",
                                tint = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamSelectionAndCreationDialogs(
    pokemon: Pokemon,
    viewModel: PokemonDetailViewModel,
) {
    val teams = viewModel.teams.collectAsState().value

    if (viewModel.showDialog) {
        TeamSelectionDialog(
            teams = teams,
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
private fun TeamSelectionDialog(
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
private fun TeamSelectionContent(
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
private fun TeamCreationDialog(
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
private fun TeamCreationContent(
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