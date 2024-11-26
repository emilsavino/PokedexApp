package com.example.pokedex.shared

class PokemonTriviaModel(val question: String, val options: List<PokemonTriviaAnswer>)

class PokemonTriviaAnswer(val answer: String, val isCorrect: Boolean)