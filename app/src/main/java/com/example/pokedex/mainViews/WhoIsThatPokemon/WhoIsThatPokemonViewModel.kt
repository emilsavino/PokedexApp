package com.example.pokedex.mainViews.WhoIsThatPokemon

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.WhoIsThatPokemonRepository
import com.example.pokedex.shared.Option
import com.example.pokedex.shared.MockPokemon
import com.example.pokedex.shared.WhoIsThatPokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WhoIsThatPokemonViewModel: ViewModel() {
    private val whoIsThatPokemonRepository = WhoIsThatPokemonRepository()

    private val _whoThepokemonMutableStateFlow = MutableStateFlow<WhoIsThatPokemon>(WhoIsThatPokemon(MockPokemon("",""),
        listOf(Option(name = "", Color.Black))
    ))
    val whoIsThatPokemonStateFlow: StateFlow<WhoIsThatPokemon> = _whoThepokemonMutableStateFlow.asStateFlow()

    val hasAnswered = mutableStateOf(false)

    fun getColor(option: Option) : Color
    {
        if (!hasAnswered.value)
        {
            return Color.Black
        }

        if (option.name == whoIsThatPokemonStateFlow.value.pokemon.name)
        {
            return Color.Green
        }
        else
        {
            return Color.Red
        }
    }

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

    fun guessed(guessedName : String)
    {
        hasAnswered.value = true
    }
}
