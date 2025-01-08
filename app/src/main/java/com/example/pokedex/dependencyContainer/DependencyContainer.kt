package com.example.pokedex.dependencyContainer

import android.app.Application
import com.example.pokedex.mainViews.ProfileView.signIn.GoogleAuthenticationManager
import com.example.pokedex.repositories.PokemonRepository
import com.example.pokedex.repositories.WhoIsThatPokemonRepository
import com.example.pokedex.repositories.FavouritesRepository
import com.example.pokedex.repositories.PokemonOfTheDayRepository
import com.example.pokedex.repositories.RecentlyViewedRepository

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

    val favouritesRepository: FavouritesRepository by lazy {
        FavouritesRepository(application.applicationContext)
    }

    val pokemonOfTheDayRepository: PokemonOfTheDayRepository by lazy {
        PokemonOfTheDayRepository()
    }

    val recentlyViewedRepository: RecentlyViewedRepository by lazy {
        RecentlyViewedRepository(application.applicationContext)
    }

    val googleAuthenticationManager: GoogleAuthenticationManager by lazy {
        GoogleAuthenticationManager(application.applicationContext)

    }
}