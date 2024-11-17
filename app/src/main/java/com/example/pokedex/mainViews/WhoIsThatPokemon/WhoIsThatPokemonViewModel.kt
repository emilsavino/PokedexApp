package com.example.pokedex.mainViews.WhoIsThatPokemon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.WhoIsThatPokemonRepository
import com.example.pokedex.shared.WhoIsThatPokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WhoIsThatPokemonViewModel: ViewModel() {
    private val whoIsThatPokemonRepository = WhoIsThatPokemonRepository()

    private val _whoThepokemonMutable = MutableStateFlow<WhoIsThatPokemon?>(null)
    val whoIsThatPokemon: StateFlow<WhoIsThatPokemon?> = _whoThepokemonMutable.asStateFlow()

    init {
        getWhoIsThatPokemon()
    }

    fun getWhoIsThatPokemon() {
        viewModelScope.launch {
            whoIsThatPokemonRepository.getWhoIsThatPokemon()
            whoIsThatPokemon.collect { _whoThepokemonMutable.value }
        }
    }
}
