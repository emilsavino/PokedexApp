package com.example.pokedex.mainViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MakeHeader()

        MakeGrids(viewModel)
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
private fun MakeGrids(viewModel: MyTeamsViewModel) {
    var teamNumber = 1
    for (team in viewModel.mockData) {
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
                        PokemonGridItem(pokemon = Pokemon("Pikachu", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/132.png"))
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
