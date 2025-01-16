package com.example.pokedex.mainViews.SavedPokemonsView

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.dataClasses.Pokemon
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SavedViewModel : ViewModel() {
    private val favouritesRepository = DependencyContainer.favouritesRepository

    private val mutableStateFlow = MutableStateFlow<SavedUIState>(SavedUIState.Empty)
    val savedState: StateFlow<SavedUIState> = mutableStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            favouritesRepository.savedPokemonsFlow
                .collect { saved ->
                    mutableStateFlow.update {
                        SavedUIState.Data(saved)
                    }
                }
        }
        fetchSaved()
    }

    fun savedIsEmpty() {
        mutableStateFlow.update {
            SavedUIState.Empty
        }
    }

    private fun fetchSaved() = viewModelScope.launch {
        mutableStateFlow.update {
            SavedUIState.Loading
        }
        favouritesRepository.fetchSaved()
    }
}

sealed class SavedUIState {
    data class Data(val saved: List<Pokemon>) : SavedUIState()
    object Loading : SavedUIState()
    object Empty : SavedUIState()
}