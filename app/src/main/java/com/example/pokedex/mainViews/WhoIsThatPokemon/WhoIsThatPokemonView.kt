package com.example.pokedex.mainViews.WhoIsThatPokemon

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.pokedex.navigation.Screen
import com.example.pokedex.shared.BackButton
import com.example.pokedex.shared.Option

@Composable
fun WhoIsThatPokemonView(modifier: Modifier = Modifier, navController: NavController)
{
    val whoIsThatPokemonViewModel = WhoIsThatPokemonViewModel()
    val whoIsThatPokemon = whoIsThatPokemonViewModel.whoIsThatPokemonStateFlow.collectAsState().value
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        BackButton(
            navController = navController,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Text(text = "Who Is That Pokemon",
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(10.dp)
        )

        AsyncImage(
            modifier = modifier.height(250.dp),
            model = whoIsThatPokemon.pokemon.imageURL,
            contentDescription = "Blacked out image of pokemon"
        )

        Options(whoIsThatPokemonViewModel, navController)
    }

}
@Composable
fun Options (whoIsThatPokemonViewModel: WhoIsThatPokemonViewModel, navController: NavController)
{
    val whoIsThatPokemon = whoIsThatPokemonViewModel.whoIsThatPokemonStateFlow.collectAsState().value
    LazyColumn (modifier = Modifier
        .fillMaxHeight(),
    )
    {
        items(whoIsThatPokemon.options) { item: Option ->
            OptionComposable(option = item, navController = navController, whoIsThatPokemonViewModel = whoIsThatPokemonViewModel)
        }
    }
}

@Composable
fun OptionComposable(modifier : Modifier = Modifier, navController: NavController, option : Option, whoIsThatPokemonViewModel: WhoIsThatPokemonViewModel)
{
    Button(
        modifier = modifier
            .padding(5.dp)
            .height(100.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        onClick =
        {
            whoIsThatPokemonViewModel.guessed(guessedName = option.name)
        }
    ) {
        Text(text = option.name,
            color = whoIsThatPokemonViewModel.getColor(option),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold)
    }
}
@Preview (showBackground = true)
@Composable
fun WhoIsThatPokemonPreview()
{
    val navController = rememberNavController()
    WhoIsThatPokemonView(navController = navController)
}

@Preview (showBackground = true)
@Composable
fun OptionsPreview()
{
    val navController = rememberNavController()
    Options(WhoIsThatPokemonViewModel(), navController)
}
