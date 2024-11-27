package com.example.pokedex.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController

@Composable
fun TabBar(navController: NavController) {
    val tabs = listOf(
        Screen.Saved.route to "Saved",
        Screen.MyTeams.route to "Teams",
        Screen.Home.route to "Home",
        Screen.Search.route to "Search",
        Screen.Profile.route to "Profile"
    )

    var selectedTabIndex by remember { mutableIntStateOf(2) }

    NavigationBar {
        tabs.forEachIndexed() { index, tab ->
            NavigationBarItem(
                icon = { Text(tab.second) },
                selected = index == selectedTabIndex,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(tab.first)
                }
            )
        }
    }
}