package com.example.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.pokedex.data.PokemonDataStore
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.navigation.Navigation
import com.example.pokedex.navigation.TabBar
import com.example.pokedex.ui.theme.PokedexTheme
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        DependencyContainer.init(application)
        setContent {
            PokedexTheme {
                enableEdgeToEdge()
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    val pokemonRepo = DependencyContainer.pokemonRepository
    LaunchedEffect(Dispatchers.IO) {
        pokemonRepo.getPokemonDetailsByName("bulbasaur")
    }
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            TabBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Navigation(navController = navController)
        }
    }
}
