package com.example.pokedex.shared

data class SearchResult(
    val indexOfSearch : Int,
    val pokemons : List<Pokemon>
)