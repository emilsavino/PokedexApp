package com.example.pokedex.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel

class TabBarViewModel: ViewModel() {
    var selectedTabIndex by mutableIntStateOf(2)
    val tabs = listOf(
        Tab(route = Screen.Saved.route, title = "Saved", icon = Icons.Outlined.FavoriteBorder),
        Tab(route = Screen.MyTeams.route, title = "Teams", icon = Icons.Outlined.List),
        Tab(route = Screen.Home.route, title = "Home", icon = Icons.Outlined.Home),
        Tab(route = Screen.Search.route, title = "Search", icon = Icons.Outlined.Search),
        Tab(route = Screen.Profile.route, title = "Profile", icon = Icons.Outlined.Person)
    )
}

data class Tab(
    val route: String,
    val title: String,
    val icon: ImageVector
)