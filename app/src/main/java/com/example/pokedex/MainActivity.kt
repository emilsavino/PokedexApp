package com.example.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pokedex.mainViews.HomeView
import com.example.pokedex.mainViews.MyTeamsView
import com.example.pokedex.mainViews.ProfileView
import com.example.pokedex.mainViews.SavedView
import com.example.pokedex.mainViews.SearchView
import com.example.pokedex.ui.theme.PokedexTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexTheme {
                var selectedTabIndex by remember { mutableStateOf(2) }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { TabBar(selectedTabIndex, onTabSelected = { selectedTabIndex = it }) }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MainContent(selectedTabIndex)
                    }
                }
            }
        }
    }
}

@Composable
fun TabBar(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("Saved", "My Teams", "Home", "Search", "Profile")

    TabRow(selectedTabIndex = selectedTabIndex) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
fun MainContent(selectedTabIndex: Int, modifier: Modifier = Modifier) {
    when (selectedTabIndex) {
        0 -> SavedView()
        1 -> MyTeamsView()
        2 -> HomeView()
        3 -> SearchView()
        4 -> ProfileView()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PokedexTheme {
        var selectedTabIndex by remember { mutableStateOf(2) }
        TabBar(selectedTabIndex, onTabSelected = { selectedTabIndex = it })
    }
}