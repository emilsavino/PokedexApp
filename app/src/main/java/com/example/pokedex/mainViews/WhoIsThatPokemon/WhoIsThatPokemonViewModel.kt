package com.example.pokedex.mainViews.WhoIsThatPokemon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.WhoIsThatPokemonRepository
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.WhoIsThatPokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WhoIsThatPokemonViewModel: ViewModel() {
    private val whoIsThatPokemonRepository = WhoIsThatPokemonRepository()

    private val _whoThepokemonMutableStateFlow = MutableStateFlow<WhoIsThatPokemon>(WhoIsThatPokemon(Pokemon("",""),
        listOf("")
    ))
    val whoIsThatPokemonStateFlow: StateFlow<WhoIsThatPokemon> = _whoThepokemonMutableStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            whoIsThatPokemonRepository.whoIsThatPokemonSharedFlow
                .collect { whoThePokemon ->
                    _whoThepokemonMutableStateFlow.update {
                        whoThePokemon
                    }
                }
        }
        fetchWhoThatPokemon()
    }

    private fun fetchWhoThatPokemon()
    {
        viewModelScope.launch {
            whoIsThatPokemonRepository.getWhoIsThatPokemon()

        }
    }
}
