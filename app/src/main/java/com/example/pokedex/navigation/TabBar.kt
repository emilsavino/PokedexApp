package com.example.pokedex.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun TabBar(navController: NavController) {
    val viewModel = remember { TabBarViewModel() }

    NavigationBar {
        viewModel.tabs.forEachIndexed() { index, tab ->
            NavigationBarItem(
                selected = index == viewModel.selectedTabIndex,
                onClick = {
                    viewModel.selectedTabIndex = index
                    navController.navigate(tab.first)
                },
                icon = {
                    Text(
                        tab.second,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TabBarPreview() {
    val NavController = rememberNavController()
    TabBar(navController = NavController)
}