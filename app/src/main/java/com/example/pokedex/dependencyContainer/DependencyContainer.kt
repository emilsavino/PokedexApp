package com.example.pokedex.dependencyContainer

import PokemonTriviaRepository
import android.app.Application
import com.example.pokedex.manager.GoogleAuthenticationManager
import com.example.pokedex.data.PokemonDataStore
import com.example.pokedex.manager.EmailAuthManager
import com.example.pokedex.repositories.PokemonRepository
import com.example.pokedex.repositories.WhoIsThatPokemonRepository
import com.example.pokedex.repositories.FavouritesRepository
import com.example.pokedex.repositories.PokemonOfTheDayRepository
import com.example.pokedex.repositories.RecentlySearchedRepository
import com.example.pokedex.repositories.RecentlyViewedRepository
import com.example.pokedex.repositories.TeamsRepository

object DependencyContainer {
    private lateinit var application: Application

    fun init(application: Application) {
        this.application = application
    }

    val pokemonRepository: PokemonRepository by lazy {
        PokemonRepository()
    }

    val whoIsThatPokemonRepository: WhoIsThatPokemonRepository by lazy {
        WhoIsThatPokemonRepository()
    }

    val pokemonTriviaRepository: PokemonTriviaRepository by lazy {
        PokemonTriviaRepository()
    }

    val favouritesRepository: FavouritesRepository by lazy {
        FavouritesRepository(application.applicationContext)
    }

    val pokemonOfTheDayRepository: PokemonOfTheDayRepository by lazy {
        PokemonOfTheDayRepository()
    }

    val recentlyViewedRepository: RecentlyViewedRepository by lazy {
        RecentlyViewedRepository(application.applicationContext)
    }

    val pokemonDataStore : PokemonDataStore by lazy {
        PokemonDataStore()
    }

    val teamsRepository: TeamsRepository by lazy {
        TeamsRepository(application.applicationContext)
    }


    val googleAuthenticationManager: GoogleAuthenticationManager by lazy {
        GoogleAuthenticationManager(application.applicationContext)
    }

    val emailAuthManager: EmailAuthManager by lazy {
        EmailAuthManager()
    }

    val recentlySearchedRepository : RecentlySearchedRepository by lazy {
        RecentlySearchedRepository(application.applicationContext)
    }
}