package com.example.pokedex.mainViews.saved

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.pokedex.data.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import com.example.pokedex.mainViews.myTeams.TeamsUIState
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SavedViewModel : ViewModel()
{
    private val pokemonRepository = PokemonRepository()
    private val mutableSavedState = MutableStateFlow<SavedUIState>(SavedUIState.Empty)
    val savedState: StateFlow<SavedUIState> = mutableSavedState

    init {
        viewModelScope.launch {
            pokemonRepository.savedPokemonsFlow
                .collect { saved ->
                    mutableSavedState.update {
                        SavedUIState.Data(saved)
                    }
                }
        }
        fetchSaved()
    }

    private fun fetchSaved() = viewModelScope.launch {
        mutableSavedState.update {
            SavedUIState.Loading
        }
        pokemonRepository.fetchSaved()
    }

}

sealed class SavedUIState {
    data class Data(val saved:List<Pokemon>): SavedUIState()
    object Loading: SavedUIState()
    object Empty: SavedUIState()
}