package com.example.pokedex.mainViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MyTeamsView(modifier: Modifier = Modifier) {
    val viewModel = MyTeamsViewModel()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MakeHeader()

        for (team in viewModel.mockData) {
            Text(
                text = "Team"
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(16.dp)
            ) {
                items(team.size) { index ->
                    val pokemon = team[index]
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Gray)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MakeHeader() {
    Text(
        modifier = Modifier.padding(16.dp),
        text = "TEAMS",
        fontSize = 60.sp
    )
}

@Preview(showBackground = true)
@Composable
fun MyTeamsViewPreview() {
    MyTeamsView()
}
