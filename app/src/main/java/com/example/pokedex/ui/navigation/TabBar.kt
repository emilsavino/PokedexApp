package com.example.pokedex.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.pokedex.R


@Composable
fun TabBar(navController: NavController) {
    val viewModel = remember { TabBarViewModel() }

    NavigationBar {
        viewModel.tabs.forEachIndexed { index, tab ->
            NavigationBarItem(
                selected = index == viewModel.selectedTabIndex,
                onClick = {
                    viewModel.selectedTabIndex = index
                    navController.navigate(tab.route)
                },
                icon = {
                    if (tab.title == "Home") {
                        CreateHomeButton(tab)
                    } else if (tab.title == "Profile" && viewModel.isAuthenticated && viewModel.profilePicture != "") {
                        CreateProfileButton(tab, viewModel)
                    } else {
                        CreateOtherButtons(tab)
                    }
                }
            )
        }
    }
}

@Composable
fun CreateHomeButton(tab: Tab) {
    Column(
        modifier = Modifier.padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.pokeball),
            contentDescription = "Home",
            modifier = Modifier
                .size(25.dp)
        )
        Text(
            text = tab.title,
            fontSize = 12.sp,
        )
    }
}

@Composable
fun CreateProfileButton(tab: Tab, viewModel: TabBarViewModel) {
    Column(
        modifier = Modifier.padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = viewModel.profilePicture,
            contentDescription = "${tab.title} Icon",
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Text(
            text = tab.title,
            fontSize = 12.sp
        )
    }
}

@Composable
fun CreateOtherButtons(tab: Tab) {
    Column(
        modifier = Modifier.padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = "${tab.title} Icon",
            tint = Color.Black
        )
        Text(
            text = tab.title,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TabBarPreview() {
    val NavController = rememberNavController()
    TabBar(navController = NavController)
}