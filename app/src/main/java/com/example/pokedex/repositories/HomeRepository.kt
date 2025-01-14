package com.example.pokedex.repositories

import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class HomeRepository {
    val recentlyViewedRepository = DependencyContainer.recentlyViewedRepository
    val pokemonOfTheDayRepository = DependencyContainer.pokemonOfTheDayRepository

    private val mutableHomeFlow = MutableSharedFlow<HomeData>()
    val homeFlow: Flow<HomeData> = mutableHomeFlow.asSharedFlow()

    suspend fun fetchHomeData() {
        val recentlyViewed = recentlyViewedRepository.fetchRecents()
        val pokemonOfTheDay = pokemonOfTheDayRepository.determinPokemonOfTheDay()
        val data = HomeData(pokemonOfTheDay, recentlyViewed)
        mutableHomeFlow.emit(data)
    }
}

data class HomeData(
    val pokemonOfTheDay: Pokemon,
    val recentlyViewedPokemons: List<Pokemon>
)