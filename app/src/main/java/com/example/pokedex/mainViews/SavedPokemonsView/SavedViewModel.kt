package com.example.pokedex.mainViews.SavedPokemonsView

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SavedViewModel : ViewModel() {
    private val favouritesRepository = DependencyContainer.favouritesRepository
    private val mutableStateFlow = MutableStateFlow<List<Pokemon>>(emptyList())
    val savedState: StateFlow<List<Pokemon>> = mutableStateFlow

    init {
        viewModelScope.launch {
            favouritesRepository.savedPokemonsFlow
                .collect { saved ->
                    mutableStateFlow.update {
                        saved
                    }
                }
        }
        fetchSaved()
    }

    private fun fetchSaved() = viewModelScope.launch {
        favouritesRepository.fetchSaved()
    }

}