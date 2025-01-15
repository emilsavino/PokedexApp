package com.example.pokedex.navigation

import android.window.SplashScreenView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pokedex.mainViews.HomeView.HomeView
import com.example.pokedex.mainViews.MyTeamsView.MyTeamsView
import com.example.pokedex.mainViews.PokemonDetailView.PokemonDetailView
import com.example.pokedex.mainViews.ProfileView.ProfileView
import com.example.pokedex.mainViews.SavedPokemonsView.SavedView
import com.example.pokedex.mainViews.SearchView.SearchView
import com.example.pokedex.mainViews.WhoIsThatPokemon.WhoIsThatPokemonView
import com.example.pokedex.mainViews.addToTeamView.AddToTeamView
import com.example.pokedex.mainViews.pokemonTriviaView.PokemonTriviaView
import com.example.pokedex.mainViews.signInView.SignInView
import com.example.pokedex.mainViews.splashScreen.MakeSplashScreen

@Composable
fun Navigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.SignIn.route) {
            SignInView(navController = navController)
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
        composable(
            route = Screen.Search.route,
            arguments = listOf(
                navArgument("filterOption") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val filterOption = backStackEntry.arguments?.getString("filterOption")
            if (filterOption != null) {
                SearchView(navController = navController, filterOption = filterOption)
            }
        }
        composable(Screen.Profile.route) {
            ProfileView(navController = navController)
        }
        composable(Screen.Splash.route) {
            MakeSplashScreen(navController = navController)
        }
        composable(
            route = Screen.AddToTeam.route,
            arguments = listOf(
                navArgument("teamName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val teamName = backStackEntry.arguments?.getString("teamName")
            if (teamName != null) {
                AddToTeamView(teamName = teamName, navController = navController)
            }
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
    object Search : Screen("search/{filterOption}"){
        fun createRoute(filterOption: String) = "search/$filterOption"
    }
    object Profile : Screen("profile")
    object SignIn : Screen("signIn")
    object Splash : Screen("splash")
    object AddToTeam : Screen("addToTeam/{teamName}") {
        fun createRoute(teamName:String) = "addToTeam/$teamName"
    }
    object PokemonDetails : Screen("pokemonDetails/{pokemonName}") {
        fun createRoute(pokemonName: String) = "pokemonDetails/$pokemonName"
    }
    object WhoIsThatPokemon : Screen("whoIsThatPokemon")
    object PokemonTrivia : Screen("pokemonTrivia")
}