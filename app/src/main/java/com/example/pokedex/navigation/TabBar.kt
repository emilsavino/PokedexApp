package com.example.pokedex.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController

@Composable
fun TabBar(navController: NavController) {
    val viewModel = remember { TabBarViewModel() }

    NavigationBar {
        viewModel.tabs.forEachIndexed() { index, tab ->
            NavigationBarItem(
                icon = { Text(tab.second) },
                selected = index == viewModel.selectedTabIndex,
                onClick = {
                    viewModel.selectedTabIndex = index
                    navController.navigate(tab.first)
                }
            )
        }
    }
}