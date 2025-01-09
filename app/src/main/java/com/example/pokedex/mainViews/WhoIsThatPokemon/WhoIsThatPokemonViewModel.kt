package com.example.pokedex.mainViews.WhoIsThatPokemon

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Ability
import com.example.pokedex.shared.AbilityObject
import com.example.pokedex.shared.Option
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.Sprites
import com.example.pokedex.shared.Type
import com.example.pokedex.shared.TypeObject
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

    val hasAnswered = mutableStateOf(false)

    fun getColor(option: Option) : Color {
        // We will hopefully remake the entire system regarding options, so i am choosing
        // not to spend time on this rn.
        if (!hasAnswered.value) {
            return Color.Black
        }
        return Color.Green
    }

    init {
        viewModelScope.launch {
            whoIsThatPokemonRepository.whoIsThatPokemonSharedFlow
                .collect { whoThePokemon ->
                    if (whoThePokemon == null)
                    {
                        fetchWhoThatPokemon()
                    }
                    else
                    {
                        _whoThepokemonMutableStateFlow.update {
                            WhoIsThatPokemonUIState.Data(whoThePokemon)
                        }
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

    fun guessed(guessedName : String) {
        hasAnswered.value = true
    }
}


sealed class WhoIsThatPokemonUIState {
    data class Data(val pokemon: WhoIsThatPokemon): WhoIsThatPokemonUIState()
    object Loading: WhoIsThatPokemonUIState()
    object Empty: WhoIsThatPokemonUIState()
}
