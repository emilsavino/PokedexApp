package com.example.pokedex.mainViews.SavedPokemonsView

import androidx.lifecycle.ViewModel
import com.example.pokedex.data.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import com.example.pokedex.shared.MockPokemon
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SavedViewModel : ViewModel()
{
    private val pokemonRepository = PokemonRepository()
    private val mutableStateFlow = MutableStateFlow<List<MockPokemon>>(emptyList())
    val savedState: StateFlow<List<MockPokemon>> = mutableStateFlow

    init {
        viewModelScope.launch {
            pokemonRepository.savedPokemonsFlow
                .collect { saved ->
                    mutableStateFlow.update {
                        saved
                    }
                }
        }
        fetchSaved()
    }

    private fun fetchSaved() = viewModelScope.launch {
        pokemonRepository.fetchSaved()
    }

}