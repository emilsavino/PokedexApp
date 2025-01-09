package com.example.pokedex.navigation

import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import com.example.pokedex.mainViews.WhoIsThatPokemon.WhoIsThatPokemonView
import com.example.pokedex.mainViews.pokemonTriviaView.PokemonTriviaView

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.SignIn.route
    ) {
        composable(Screen.SignIn.route) {
            Button(onClick = {navController.navigate(Screen.Home.route)}) {
                Text("Sign In")
            }
        }

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
        composable(Screen.WhoIsThatPokemon.route) {
            WhoIsThatPokemonView(navController = navController)
        }
        composable(Screen.PokemonTrivia.route) {
            PokemonTriviaView(navController = navController)
        }

    }
}

sealed class Screen(val route: String) {
    object Saved : Screen("saved")
    object MyTeams : Screen("myTeams")
    object Home : Screen("home")
    object Search : Screen("search")
    object Profile : Screen("profile")
    object SignIn : Screen("signIn")
    object PokemonDetails : Screen("pokemonDetails/{pokemonName}") {
        fun createRoute(pokemonName: String) = "pokemonDetails/$pokemonName"
    }
    object WhoIsThatPokemon : Screen("whoIsThatPokemon")
    object PokemonTrivia : Screen("pokemonTrivia")
}