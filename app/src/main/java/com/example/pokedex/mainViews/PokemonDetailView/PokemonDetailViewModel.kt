package com.example.pokedex.mainViews.PokemonDetailView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PokemonDetailViewModel(private val name: String): ViewModel() {
    private val pokemonRepository = DependencyContainer.pokemonRepository
    private val favouritesRepository = DependencyContainer.favouritesRepository
    private val recentlyViewedRepository = DependencyContainer.recentlyViewedRepository

    var isFavorited by mutableStateOf(false)

    private val _pokemon: MutableStateFlow<PokemonDetailUIState> = MutableStateFlow(PokemonDetailUIState.Empty)
    val pokemon: StateFlow<PokemonDetailUIState> = _pokemon.asStateFlow()

    private val _description = MutableStateFlow<String>("")
    val description: StateFlow<String> = _description.asStateFlow()

    /*private val _types = MutableStateFlow<List<TypeObject>>(emptyList())
    val types: StateFlow<List<TypeObject>> = _types.asStateFlow()

    private val _weaknesses = MutableStateFlow<Weaknesses>(Weaknesses(emptyList()))
    val weaknesses: StateFlow<Weaknesses> = _weaknesses.asStateFlow()*/

    init {
        viewModelScope.launch {
            pokemonRepository.pokemonFlow.collect { newPokemon ->
                recentlyViewedRepository.addToRecents(newPokemon)
                _pokemon.update {
                    onFavouriteButton(newPokemon)
                    PokemonDetailUIState.Data(newPokemon)
                }
            }
        }
        getPokemonByName()
        //loadPokeTypesAndWeaknesses(name)
    }

    /*
    fun loadPokeDesc(name: String) {
        viewModelScope.launch {
            try {
                val species = pokemonRepository.getPokemonDescription(name)
                val englishEntry = species.flavor_text_entries.firstOrNull(){
                    it.language.name == "en"
                }
                _description.value = englishEntry?.flavor_text?.replace("\n", " ") ?: "No description available."
            } catch (e: Exception) {
                _description.value = "Fetch Error: No Description Exists"
            }
        }
    }

    fun getWeaknessFromList(): String {
        return weaknesses.value.double_damage_from.joinToString("\n")
    }

    private fun loadPokeTypesAndWeaknesses(name: String) = viewModelScope.launch {
        loadPokemonTypes(name)
        loadPokemonWeakness()
    }


    private suspend fun loadPokemonTypes(name: String) {
        pokemonRepository.typesFlow.collect { newTypes ->
            _types.update { newTypes }
        }
        pokemonRepository.getPokemonTypes(name)

    }

    private suspend fun loadPokemonWeakness() {
        pokemonRepository.weaknessesFlow.collect { newWeaknesses ->
            _weaknesses.update { newWeaknesses }
        }
        pokemonRepository.getPokemonWeakness(types.value)

    }*/

    private fun getPokemonByName() = viewModelScope.launch {
        _pokemon.update {
            PokemonDetailUIState.Loading
        }
        pokemonRepository.getPokemonByName(name)
    }

    suspend fun savePokemon(pokemon: Pokemon) {
        if (favouritesRepository.pokemonIsFavourite(pokemon)) {
            favouritesRepository.removeFromFavourites(pokemon)
        } else {
            favouritesRepository.makeFavourite(pokemon)
        }
        onFavouriteButton(pokemon)
    }

    private fun onFavouriteButton(pokemon: Pokemon)  {
        isFavorited = favouritesRepository.pokemonIsFavourite(pokemon)
    }
}

sealed class PokemonDetailUIState {
    data class Data(val pokemon: Pokemon): PokemonDetailUIState()
    object Loading: PokemonDetailUIState()
    object Empty: PokemonDetailUIState()
}