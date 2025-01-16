package com.example.pokedex.mainViews.splashScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pokedex.R
import kotlinx.coroutines.Dispatchers

@Composable
fun MakeSplashScreen(navController: NavController) {
    val viewModel = viewModel<SplashScreenViewModel>()

    LaunchedEffect(Dispatchers.IO) {
        viewModel.onAppear(navController)
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.loading_screen),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        CircularProgressIndicator(
            color = Color.Blue,
            strokeWidth = 4.dp
        )
    }
}