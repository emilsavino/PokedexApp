package com.example.pokedex.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pokedex.mainViews.HomeView.HomeView
import com.example.pokedex.mainViews.PokemonDetailView.PokemonDetailView
import com.example.pokedex.mainViews.ProfileView.ProfileView
import com.example.pokedex.mainViews.MyTeamsView.MyTeamsView
import com.example.pokedex.mainViews.SavedPokemonsView.SavedView
import com.example.pokedex.mainViews.SearchView.SearchView

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Saved.route) {
            SavedView(navController = navController)
        }
        composable(Screen.MyTeams.route) {
            MyTeamsView(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeView(navController = navController)
        }
        composable(Screen.Search.route) {
            SearchView(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileView(navController = navController)
        }
        composable(
            route = Screen.PokemonDetails.route,
            arguments = listOf(
                navArgument("pokemonName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val pokemonName = backStackEntry.arguments?.getString("pokemonName")
            if (pokemonName != null) {
                PokemonDetailView(pokemonName, navController)
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Saved : Screen("saved")
    object MyTeams : Screen("myTeams")
    object Home : Screen("home")
    object Search : Screen("search")
    object Profile : Screen("profile")
    object PokemonDetails : Screen("pokemonDetails/{pokemonName}") {
        fun createRoute(pokemonName: String) = "pokemonDetails/$pokemonName"
    }
}