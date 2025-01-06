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
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.pokedex.data.PokemonDataStore
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.navigation.Navigation
import com.example.pokedex.navigation.TabBar
import com.example.pokedex.repositorys.PokemonRepository
import com.example.pokedex.ui.theme.PokedexTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyContainer.init(application)
        CoroutineScope(Dispatchers.IO).launch {
            val charmander = PokemonDataStore().fetchPokemon("charmander")
            println("dean")
            println(charmander)
        }
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
