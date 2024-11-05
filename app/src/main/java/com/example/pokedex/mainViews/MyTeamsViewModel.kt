package com.example.pokedex.mainViews

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.pokedex.shared.Pokemon

class MyTeamsViewModel: ViewModel() {
    var teams: List<List<Pokemon>> by mutableStateOf(listOf())
    var mockData: List<List<Int>> by mutableStateOf(
        listOf(
            listOf(1, 2, 3, 4, 5, 6),
            listOf(4, 5, 6, 7, 8, 9),
            listOf(7, 8, 9, 10, 11, 12),
        )
    )

}