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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.navigation.Navigation
import com.example.pokedex.navigation.Screen
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
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val authManager = DependencyContainer.googleAuthenticationManager
    var isSignedIn by remember { mutableStateOf(false) }


    val showTabBar = when (currentRoute) {
        Screen.SignIn.route -> false
        else -> true
    }

    LaunchedEffect(Dispatchers.IO) {
        isSignedIn = authManager.fetchSignedIn()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showTabBar) {
                TabBar(navController = navController)
            }

        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Navigation(navController = navController, isSignedIn)
        }
    }
}
