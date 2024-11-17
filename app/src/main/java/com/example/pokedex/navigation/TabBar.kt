package com.example.pokedex.navigation

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

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
                            saveState = false
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