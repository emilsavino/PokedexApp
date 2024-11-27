package com.example.pokedex.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TabBarViewModel: ViewModel() {
    var selectedTabIndex by mutableIntStateOf(2)
    val tabs = listOf(
        Screen.Saved.route to "Saved",
        Screen.MyTeams.route to "Teams",
        Screen.Home.route to "Home",
        Screen.Search.route to "Search",
        Screen.Profile.route to "Profile"
    )
}