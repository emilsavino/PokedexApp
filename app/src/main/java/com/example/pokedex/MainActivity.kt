package com.example.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pokedex.ui.theme.PokedexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun TabBar(navController: NavController) {
    val tabs = listOf(
        Screen.Saved to "Saved",
        Screen.MyTeams to "My Teams",
        Screen.Home to "Home",
        Screen.Search to "Search",
        Screen.Profile to "Profile"
    )

    val currentDestination = navController.currentDestination?.route
    val selectedTabIndex = tabs.indexOfFirst { it.first.route == currentDestination }.takeIf { it >= 0 } ?: 2

    TabRow(selectedTabIndex = selectedTabIndex) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = {
                    navController.navigate(tab.first.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                text = { Text(tab.second) }
            )
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
