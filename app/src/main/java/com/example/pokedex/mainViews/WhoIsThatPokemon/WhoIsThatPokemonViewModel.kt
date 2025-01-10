package com.example.pokedex.mainViews.WhoIsThatPokemon

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Option
import com.example.pokedex.shared.WhoIsThatPokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WhoIsThatPokemonViewModel: ViewModel() {
    private val whoIsThatPokemonRepository = DependencyContainer.whoIsThatPokemonRepository

    private val _whoThepokemonMutableStateFlow = MutableStateFlow<WhoIsThatPokemonUIState>(WhoIsThatPokemonUIState.Empty)
    val whoIsThatPokemonStateFlow: StateFlow<WhoIsThatPokemonUIState> = _whoThepokemonMutableStateFlow.asStateFlow()

    private val hasAnswered = mutableStateOf(false)

    fun getColor(option: Option) : Color {
        if (!hasAnswered.value) {
            return Color.Black
        }

        if (option.isCorrect) {
            return Color.Green
        }

        return Color.Red
    }

    init {
        viewModelScope.launch {
            whoIsThatPokemonRepository.whoIsThatPokemonSharedFlow
                .collect { whoThePokemon ->
                    _whoThepokemonMutableStateFlow.update {
                        WhoIsThatPokemonUIState.Data(whoThePokemon)
                    }

                }
        }
        fetchWhoThatPokemon()
    }

    private fun fetchWhoThatPokemon() {
        viewModelScope.launch {
            whoIsThatPokemonRepository.getWhoIsThatPokemon()

        }
    }

    fun guessed() {
        hasAnswered.value = true
    }
}


sealed class WhoIsThatPokemonUIState {
    data class Data(val pokemon: WhoIsThatPokemon): WhoIsThatPokemonUIState()
    object Loading: WhoIsThatPokemonUIState()
    object Empty: WhoIsThatPokemonUIState()
}
