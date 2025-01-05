package com.example.pokedex.dependencyContainer

import android.app.Application
import com.example.pokedex.repositorys.PokemonRepository
import com.example.pokedex.repositorys.WhoIsThatPokemonRepository

object DependencyContainer {
    private lateinit var application: Application

    fun init(application: Application) {
        this.application = application
    }

    val pokemonRepository: PokemonRepository by lazy {
        PokemonRepository(application.applicationContext)
    }

    val whoIsThatPokemonRepository: WhoIsThatPokemonRepository by lazy {
        WhoIsThatPokemonRepository()
    }
}