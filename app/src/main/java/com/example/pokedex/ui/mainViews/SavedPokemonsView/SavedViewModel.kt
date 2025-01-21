package com.example.pokedex.mainViews.SavedPokemonsView

import androidx.compose.runtime.mutableStateOf
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

    var selectedFilterOptionsList = mutableStateOf<List<String>>(emptyList())
    var selectedSortOption = mutableStateOf("")

    private val mutableStateFlow = MutableStateFlow<SavedUIState>(SavedUIState.Empty)
    val savedState: StateFlow<SavedUIState> = mutableStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            favouritesRepository.savedPokemonsFlow.collect { saved ->
                if (saved.isNotEmpty()) {
                    mutableStateFlow.update {
                        SavedUIState.Data(saved)
                    }
                } else {
                    mutableStateFlow.update { SavedUIState.Empty }
                }
            }
        }
        fetchSaved()
    }

    fun savedIsEmpty() {
        mutableStateFlow.update { SavedUIState.Empty }
    }

    private fun fetchSaved() = viewModelScope.launch {
        mutableStateFlow.update { SavedUIState.Loading }
        favouritesRepository.fetchSaved()
    }

    fun selectFilterOption(option: String) {
        selectedFilterOptionsList.value = if (selectedFilterOptionsList.value.contains(option)) {
            selectedFilterOptionsList.value - option
        } else {
            selectedFilterOptionsList.value + option
        }
        refreshSavedList()
    }

    fun selectSortOption(option: String) {
        selectedSortOption.value = if (selectedSortOption.value == option) "" else option
        refreshSavedList()
    }

    fun getAllFilterOptions(): List<String> = favouritesRepository.filterOptions

    fun getAllSortOptions(): List<String> = favouritesRepository.sortOptions

    private fun refreshSavedList() = viewModelScope.launch {
        mutableStateFlow.update { SavedUIState.Loading }
        favouritesRepository.savedPokemonByNameAndFilterWithSort(
            selectedFilterOptionsList.value,
            selectedSortOption.value,
        )
    }

    sealed class SavedUIState {
        data class Data(val saved: List<Pokemon>) : SavedUIState()
        object Loading : SavedUIState()
        object Empty : SavedUIState()
    }
}