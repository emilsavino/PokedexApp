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
                mutableStateFlow.update {
                    SavedUIState.Data(applyFilterAndSort(saved))
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

    fun selectFilterOption(option: String) {
        if (selectedFilterOptionsList.value.contains(option)) {
            selectedFilterOptionsList.value =
                selectedFilterOptionsList.value.toMutableList().apply {
                    remove(option)
                }
        } else {
            selectedFilterOptionsList.value =
                selectedFilterOptionsList.value.toMutableList().apply {
                    add(option)
                }
        }
        refreshSavedList()
    }

    fun selectSortOption(option: String) {
        selectedSortOption.value = if (selectedSortOption.value == option) "" else option
        refreshSavedList()
    }

    fun getAllFilterOptions(): List<String> {
        return favouritesRepository.filterOptions
    }

    fun getAllSortOptions(): List<String> {
        return favouritesRepository.sortOptions
    }

    private fun refreshSavedList() {
        val currentState = savedState.value
        if (currentState is SavedUIState.Data) {
            val updatedList = applyFilterAndSort(currentState.saved)
            mutableStateFlow.update { SavedUIState.Data(updatedList) }
        }
    }

    private suspend fun applyFilterAndSort() {
        if (selectedFilterOptionsList.value.isNotEmpty() || selectedSortOption.value.isNotEmpty()) {
            favouritesRepository.savedPokemonByNameAndFilterWithSort(
                selectedFilterOptionsList.value,
                selectedSortOption.value
            )
        }
    }

    sealed class SavedUIState {
        data class Data(val saved: List<Pokemon>) : SavedUIState()
        object Loading : SavedUIState()
        object Empty : SavedUIState()
    }