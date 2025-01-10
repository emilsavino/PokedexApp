package com.example.pokedex.shared

class PokemonTriviaModel(
    val question: String,
    val options: List<Option>,
    val correctAnswer: String
)

class PokemonTriviaAnswer(
    val answer: String,
    val isCorrect: Boolean
)